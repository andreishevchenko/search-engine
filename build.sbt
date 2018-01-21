
name := "search-engine"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "org.specs2" %% "specs2-core" % "3.8.9" % "test"
)
