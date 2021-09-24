package dev.coffeemug.sensor

import akka.actor.ActorSystem
import akka.stream.alpakka.file.scaladsl.Directory
import dev.coffeemug.sensor.flows.ReadFilesFlow

import java.nio.file.FileSystems
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Main extends App {

  def printUsage(): Unit = println(
    """USAGE: run <dir_path>
      |
      |Parameter:
      |   dir_path - valid path to directory with csv files""".stripMargin)

  if (args.length == 1) {
    implicit val system: ActorSystem = ActorSystem("akka-streams-sensor-statistics")
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    try {
      val fs = FileSystems.getDefault
      val path = fs.getPath(args(0))

      // fetch all csv files
      Directory.ls(path).filter(_.toString.endsWith(".csv")).map(_.toFile)
        .via(ReadFilesFlow.readFiles)
        .runForeach(i => println(i.toPrintableString))
        .onComplete(_ => system.terminate)
    } catch {
      case t: Throwable =>
        t.printStackTrace(System.err)
        printUsage()
        system.terminate
    }
  } else {
    printUsage()
  }

}

