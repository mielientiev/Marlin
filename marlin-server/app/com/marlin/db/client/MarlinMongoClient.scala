package com.marlin.db.client

import javax.inject.Inject

import com.marlin.config.MongoConfig
import org.mongodb.scala.MongoClient


class MarlinMongoClient @Inject()(config: MongoConfig) {

  val mongoClient = MongoClient(s"mongodb://${config.connectionURL}:${config.connectionPort}")
  val mongoDatabase = mongoClient.getDatabase(config.marlinDatabase)

}
