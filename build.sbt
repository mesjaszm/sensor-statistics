name := "sensor-statistics"

version := "0.1"

scalaVersion := "2.13.6"

// scala test
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

// akka
val AkkaVersion = "2.6.16"
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.3",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion
)
