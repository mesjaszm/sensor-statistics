package dev.coffeemug.sensor.flows

import dev.coffeemug.sensor.models.{InvalidReading, ValidReading}
import org.scalatest.matchers.should

class ReadFilesFlowTest extends org.scalatest.funsuite.AsyncFunSuite with should.Matchers{

  test("Given invalid measurement Then returns invalid result") {

    val line = "s1,NaN"
    val expected = InvalidReading("s1")

    val future = ReadFilesFlow.parseLine(line)

    future map { result => assert(result == expected) }
  }

  test("Given valid measurement Then returns valid result") {

    val line = "s1,50"
    val expected = ValidReading("s1", 50)

    val future = ReadFilesFlow.parseLine(line)

    future map { result => assert(result == expected) }
  }

}
