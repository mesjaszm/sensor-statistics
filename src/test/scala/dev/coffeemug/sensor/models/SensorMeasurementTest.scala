package dev.coffeemug.sensor.models

class SensorMeasurementTest extends org.scalatest.funsuite.AnyFunSuite {

  test("Given empty measurement When adding invalid reading Then returns empty measurement") {

    val invalidReading = InvalidReading("s1")
    val measurement = EmptySensorMeasurement()

    val result = measurement + invalidReading

    assert(result == measurement)

  }

  test("Given non-empty measurement When adding invalid reading Then returns the same measurement") {

    val invalidReading = InvalidReading("s1")
    val measurement = NonEmptySensorMeasurement(0, 100, 50, 1)

    val result = measurement + invalidReading

    assert(result == measurement)

  }

  test("Given non-empty measurement When adding valid reading Then returns incremented measurement") {

    val validReading = ValidReading("s1", 50)
    val measurement = NonEmptySensorMeasurement(0, 100, 50, 1)
    val expected = NonEmptySensorMeasurement(0, 100, 100, 2)

    val result = measurement + validReading

    assert(result == expected)

  }

  test("Setting new minimum") {

    val validReading = ValidReading("s1", 25)
    val measurement = NonEmptySensorMeasurement(50, 100, 50, 1)
    val expected = NonEmptySensorMeasurement(25, 100, 75, 2)

    val result = measurement + validReading

    assert(result == expected)

  }

  test("Setting new maximum") {

    val validReading = ValidReading("s1", 75)
    val measurement = NonEmptySensorMeasurement(50, 50, 50, 1)
    val expected = NonEmptySensorMeasurement(50, 75, 125, 2)

    val result = measurement + validReading

    assert(result == expected)

  }

}
