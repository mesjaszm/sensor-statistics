package dev.coffeemug.sensor.models

/**
 * The `Reading` trail can be used to represent a single reading, both valid or invalid.
 */
sealed trait Reading {
  def id: String
}

/**
 * A `ValidReading` represents a valid reading from the sensor
 * @param id the id of the sensor
 * @param value the value of the reading
 */
case class ValidReading(id: String, value: Int) extends Reading

/**
 * A `InvalidReading` represents an invalid reading from the sensor
 * @param id the id of the sensor
 */
case class InvalidReading(id: String) extends Reading