package dev.coffeemug.sensor.models

/**
 * The `SensorMeasurement` trail can be used to represent statistical data of all reading for a single sensor
 */
sealed trait SensorMeasurement {

  /**
   * Add a new reading to this measurement
   *
   * @param reading the reading to be added
   * @return a new `SensorMeasurement` incremented by the reading
   */
  def +(reading: Reading): SensorMeasurement

  /**
   * Add statistical data from two measurements
   *
   * @param measurement the measurement to be added
   * @return a `SensorMeasurement` that combines data from both measurements
   */
  def +(measurement: SensorMeasurement): SensorMeasurement

  /**
   * Calculates an average from all readings
   * @return the average from all reading
   */
  def average: Float = 0
}

/**
 * A `EmptySensorMeasurement` represents an empty measurement or statistics for a sensor that has only invalid readings
 */
case class EmptySensorMeasurement() extends SensorMeasurement {

  def +(reading: Reading): SensorMeasurement = reading match {
    case _: InvalidReading => this
    case reading: ValidReading => NonEmptySensorMeasurement(
      reading.value,
      reading.value,
      reading.value,
      1
    )
  }

  def +(rhs: SensorMeasurement): SensorMeasurement = rhs match {
    case rhs: NonEmptySensorMeasurement => rhs
    case _: EmptySensorMeasurement => EmptySensorMeasurement()
  }

  override def average: Float = -1

  override def toString: String = "NaN,NaN,NaN"
}

/**
 * A `NonEmptySensorMeasurement` represents an non-empty statistics for a sensor, the sensor has at least one valid reading
 */
case class NonEmptySensorMeasurement(min: Int, max: Int, value: Int, count: Int) extends SensorMeasurement {

  def +(reading: Reading): SensorMeasurement = reading match {
    case _: InvalidReading => this
    case reading: ValidReading => NonEmptySensorMeasurement(
      math.min(min, reading.value),
      math.max(max, reading.value),
      value + reading.value,
      count + 1
    )
  }

  def +(rhs: SensorMeasurement): SensorMeasurement = rhs match {
    case rhs: NonEmptySensorMeasurement => NonEmptySensorMeasurement(
      math.min(min, rhs.min),
      math.max(max, rhs.max),
      value + rhs.value,
      count + rhs.count
    )
    case _: EmptySensorMeasurement => NonEmptySensorMeasurement(min, max, value, count)
  }

  override def average: Float = value.toFloat / count

  override def toString: String = s"$min,${math.round(average)},$max"

}
