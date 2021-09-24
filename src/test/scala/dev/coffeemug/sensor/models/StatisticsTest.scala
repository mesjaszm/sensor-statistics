package dev.coffeemug.sensor.models

class StatisticsTest extends org.scalatest.funsuite.AnyFunSuite {

  test("Given empty statistics When adding invalid reading Then returns updated statistics") {

    val emptyStatistics = Statistics()
    val invalidReading = InvalidReading("s1")

    val result = emptyStatistics + invalidReading

    val expected = Statistics(Map("s1" -> EmptySensorMeasurement()), 1, 1, 1)
    assert(result == expected)
  }

  test("Given empty statistics When adding valid reading Then returns updated statistics") {

    val emptyStatistics = Statistics()
    val invalidReading = ValidReading("s1", 50)

    val result = emptyStatistics + invalidReading

    val expected = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 1, 0, 1)
    assert(result == expected)
  }

  test("Given non-empty statistics When adding invalid reading Then returns updated statistics") {
    val emptyStatistics = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 1, 0, 1)
    val invalidReading = InvalidReading("s1")

    val result = emptyStatistics + invalidReading

    val expected = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 2, 1, 1)
    assert(result == expected)
  }

  test("Given non-empty statistics When adding valid reading Then returns updated statistics") {
    val emptyStatistics = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 1, 0, 1)
    val invalidReading = ValidReading("s1", 50)

    val result = emptyStatistics + invalidReading

    val expected = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 100, 2)), 2, 0, 1)
    assert(result == expected)
  }

  test("Given empty statistics When adding empty statistics Then returns statistics") {
    val emptyStatistics1 = Statistics(Map("s1" -> EmptySensorMeasurement()))
    val emptyStatistics2 = Statistics(Map("s1" -> EmptySensorMeasurement()))

    val result = emptyStatistics1 + emptyStatistics2

    val expected = Statistics(Map("s1" -> EmptySensorMeasurement()))
    assert(result == expected)
  }

  test("Given empty statistics When adding non-empty statistics Then returns non-empty statistics") {
    val emptyStatistics1 = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 1, 0, 1)
    val emptyStatistics2 = Statistics(Map("s1" -> EmptySensorMeasurement()))

    val result = emptyStatistics1 + emptyStatistics2

    val expected = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 1, 0, 1)
    assert(result == expected)
  }

  test("Given non-empty statistics When adding non-empty statistics Then returns aggregated statistics") {
    val statistics1 = Statistics(Map("s1" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 3, 2, 1)
    val statistics2 = Statistics(Map("s2" -> NonEmptySensorMeasurement(50, 50, 50, 1)), 2, 1, 1)

    val result = statistics1 + statistics2

    val expected = Statistics(
      Map(
        "s1" -> NonEmptySensorMeasurement(50, 50, 50, 1),
        "s2" -> NonEmptySensorMeasurement(50, 50, 50, 1)
      ), 5, 3, 2)
    assert(result == expected)
  }

}
