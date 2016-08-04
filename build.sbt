import Dependencies.marlinWeatherDependencies
import _root_.scoverage.ScoverageKeys._
import play.sbt.PlayScala
import sbt.Keys._
import sbt._

lazy val sharedSettings = Seq(
  scalaVersion := "2.11.8",
  name := "Marlin",
  libraryDependencies ++= Dependencies.commonDependencies,
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xlint", // Enable recommended additional warnings.
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Xexperimental" // Add experimental scala feature
  ),
  coverageExcludedPackages := """.*\..*Reverse.*;router.Routes.*;""",
  resolvers += Resolver.sonatypeRepo("snapshots"),
  resolvers += Resolver.jcenterRepo,
  resolvers += ("Atlassian Releases" at "https://maven.atlassian.com/public/"),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

lazy val aRootProject = Project(id = "root", base = file("."),
  settings = Defaults.coreDefaultSettings ++ sharedSettings
).aggregate(marlinWeatherExtractorProject, marlinServer, marlinIntegration)

lazy val marlinWeatherExtractorProject = Project(id = "marlin-weather-extractor", base = file("marlin-weather-extractor"),
  settings = Defaults.coreDefaultSettings ++ sharedSettings ++ Seq(
    name := "Marlin Weather Extractor",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= marlinWeatherDependencies
  )
)

lazy val marlinServer = Project(id = "marlin-server", base = file("marlin-server"),
  settings = Defaults.coreDefaultSettings ++ sharedSettings ++ Seq(
    name := "Marlin Server",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Dependencies.marlinServerDependencies
  )
).enablePlugins(PlayScala)

lazy val marlinIntegration = Project(id ="marlin-integration", base = file("marlin-integration"),
  settings = Defaults.coreDefaultSettings ++ sharedSettings ++ Seq(
    name := "Marlin Integration",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Dependencies.marlinIntegrationDependencies
  )
).dependsOn(marlinServer % "test;test->test")