import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "wager"
ThisBuild / organizationName := "wager"

lazy val root = (project in file("."))
  .settings(
    name := "wager",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += spray,
  )

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.9"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-spray-json
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.9"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.0-M6"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.0-M6"

// https://mvnrepository.com/artifact/de.heikoseeberger/akka-http-play-json
libraryDependencies += "de.heikoseeberger" %% "akka-http-play-json" % "1.27.0"

