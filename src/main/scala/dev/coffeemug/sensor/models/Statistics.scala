package dev.coffeemug.sensor.models

import scala.collection.immutable.ListMap

/**
 * A `Statistics` contains measurements for a set of sensors.
 *
 * @param sensors the measurements grouped by sensors' ids
 * @param totalCount the total number of readings
 * @param invalidCount the number of invalid readings
 */
case class Statistics(
                       sensors: Map[String, SensorMeasurement] = Map(),
                       totalCount: Int = 0,
                       invalidCount: Int = 0,
                       docCount: Int = 0
                     ) {

  /**
   * Add a reading to this statistics
   * @param reading the reading to be added
   * @return a new `Statistics` incremented by the reading
   */
  def +(reading: Reading): Statistics = Statistics(
    sensors + (reading.id -> (sensors.getOrElse(reading.id, EmptySensorMeasurement()) + reading)),
    totalCount + 1,
    reading match {
      case _: ValidReading => invalidCount
      case _: InvalidReading => invalidCount + 1
    },
    1
  )

  /**
   * Add data from another `Statistics`
   * @param statistics the statistics to be added
   * @return a new `Statistics` incremented by the statistics
   */
  def +(statistics: Statistics): Statistics = Statistics(
    sensors ++ statistics.sensors.map { case (k, v) => k -> (v + sensors.getOrElse(k, EmptySensorMeasurement())) },
    totalCount + statistics.totalCount,
    invalidCount + statistics.invalidCount,
    docCount + statistics.docCount
  )

  def toPrintableString: String = {
    val sb = new StringBuilder("")
    sb ++= s"Num of processed files: $docCount\r\n"
    sb ++= s"Num of processed measurements: $totalCount\r\n"
    sb ++= s"Num of failed measurements: $invalidCount\r\n"
    sb ++= s"\r\n"
    sb ++= s"Sensors with highest avg humidity:\r\n"
    sb ++= s"\r\n"
    sb ++= s"sensor-id,min,avg,max\r\n"

    val sortedMap = ListMap(sensors.toSeq.sortWith(_._2.average > _._2.average):_*)
    for ((k, v) <- sortedMap) {
      sb ++= s"$k,$v\r\n"
    }

    sb.toString()
  }

}