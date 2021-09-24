package dev.coffeemug.sensor.flows

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Balance, Flow, Framing, GraphDSL, Merge, StreamConverters}
import akka.util.ByteString
import dev.coffeemug.sensor.models._

import java.io.{File, FileInputStream}

object ReadFilesFlow {

  import scala.concurrent._
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  /**
   * Parse a single line of humidity measurement to @Reading
   *
   * @param line the line to be parsed
   * @return Future for valid or invalid reading
   */
  def parseLine(line: String): Future[Reading] = Future {
    val fields = line.split(",")
    val id = fields(0)
    try {
      val value = fields(1).filter(_ > ' ').toInt
      ValidReading(id, value)
    } catch {
      case _: Throwable => InvalidReading(id)
    }
  }

  val lineDelimiter: Flow[ByteString, ByteString, NotUsed] = Framing.delimiter(ByteString("\r\n"), 128, allowTruncation = true)

  val parseFile: Flow[File, Reading, NotUsed] =
    Flow[File].flatMapConcat { file =>
      val parallelism = 8
      val stream = new FileInputStream(file)

      StreamConverters.fromInputStream(() => stream)
        .via(lineDelimiter)
        .drop(1) // drop the header line
        .map(_.utf8String)
        .mapAsyncUnordered(parallelism)(parseLine)
    }

  val mergeReadingsIntoStatistic: Flow[Reading, Statistics, NotUsed] = Flow[Reading].fold(Statistics())(_ + _)

  val readSingleFile: Flow[File, Statistics, NotUsed] = Flow[File].via(parseFile).via(mergeReadingsIntoStatistic)

  val readFiles: Flow[File, Statistics, NotUsed] = {
    val concurrentReads = 8

    // split all files into x concurrent reading pipelines
    val balancerGraph = GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val balance = builder.add(Balance[File](concurrentReads))
      val merge = builder.add(Merge[Statistics](concurrentReads))

      // connect balance's outputs to merge inputs
      for(i <- 0 until concurrentReads) {
        balance.out(i) ~> readSingleFile ~> merge.in(i)
      }

      FlowShape(balance.in, merge.out)
    }

    val balancer: Flow[File, Statistics, NotUsed] = Flow.fromGraph(balancerGraph)

    Flow[File].via(balancer)
      // merge all files' statistics into one statistics
      .fold(Statistics())(_ + _)
  }


}
