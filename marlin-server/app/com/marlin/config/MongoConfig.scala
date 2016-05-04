package com.marlin.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

class MongoConfig(config: Config) {

  final val connectionURL = config.as[String]("marlin.db.mongo.url")
  final val connectionPort = config.as[String]("marlin.db.mongo.port")
  final val marlinDatabase = config.as[String]("marlin.db.mongo.database")

}

