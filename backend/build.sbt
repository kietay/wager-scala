import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "wager"
ThisBuild / organizationName := "wager"

lazy val root = (project in file("."))
  .settings(
    name := "wager",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += requests,
    libraryDependencies += spray,
    libraryDependencies += apacheHttpClient
  )
