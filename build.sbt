enablePlugins(JavaAppPackaging, AshScriptPlugin)

name := "akkahttp-quickstart"

version := "0.1"

dockerBaseImage := "openjdk:8-jre-alpine"
packageName in Docker := "akkahttp-quickstart"

val akkaVersion = "2.5.13"
val akkaHttpVersion = "10.1.3"
val circeVersion = "0.9.3"
val apacheLogVersion = "2.11.0"
val slickVersion = "3.3.0"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",

  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % apacheLogVersion,
  "org.apache.logging.log4j" % "log4j-core" % apacheLogVersion % Runtime,
  "de.heikoseeberger" %% "akka-log4j" % "1.6.1",

  "org.scalatest" %% "scalatest" % "3.0.5" % Test,

  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
)
