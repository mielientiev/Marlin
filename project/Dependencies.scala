import sbt._

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.16"
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.4"
  val typesafeConfig = "com.typesafe" % "config" % "1.3.0"
  val playScalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test"
  val mongoDbDriver = "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.0"
  val mockito = "org.mockito" % "mockito-core" % "1.10.19" % "test"
  val accordValidator = "com.wix" %% "accord-core" % "0.5"
  val mongoEmbedded = "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.3" % "test"
  val ficus = "com.iheart" %% "ficus" % "1.2.3"
  val scalaGuice = "net.codingwell" %% "scala-guice" % "4.0.1"

  val silhouette = "com.mohiva" %% "play-silhouette" % "4.0.0-BETA4"
  val silhouettePasswordBcrypt = "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-BETA4"
  val silhouettePersistenceMemory = "com.mohiva" %% "play-silhouette-persistence-memory" % "4.0.0-BETA4"
  val silhouetteTestKit = "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA4" % "test"

  val commonDependencies = Seq(scalaTest)
  val marlinServerDependencies = Seq(playScalaTest, mongoDbDriver, mockito, accordValidator, mongoEmbedded, ficus,
    scalaGuice, silhouette, silhouettePasswordBcrypt, silhouettePersistenceMemory, silhouetteTestKit)
  val marlinWeatherDependencies = Seq(slf4j, logback, typesafeConfig)
  val marlinIntegrationDependencies = Seq()
}