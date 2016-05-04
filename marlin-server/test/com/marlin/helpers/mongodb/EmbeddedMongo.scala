package com.marlin.helpers.mongodb

import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import org.mongodb.scala.MongoClient


trait EmbeddedMongo {

  private lazy val runtime: MongodStarter = MongodStarter.getDefaultInstance
  private lazy val config = new MongodConfigBuilder().net(new Net("0.0.0.0", embedConnectionPort.toInt, true)).version(embedMongoDBVersion).build()
  private[mongodb] lazy val mongodExe: MongodExecutable = runtime.prepare(config)
  private[mongodb] lazy val mongod: MongodProcess = mongodExe.start()
  private[mongodb] lazy val mongoDB = MongoClient(s"mongodb://$embedConnectionURL:$embedConnectionPort")

  def embedConnectionURL: String = "localhost"

  def embedConnectionPort: String = "12345"

  def embedMongoDBVersion: Version = Version.V3_3_1

}
