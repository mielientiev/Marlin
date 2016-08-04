package com.marlin.helpers.mongodb

import java.io.IOException

import com.marlin.helpers.ServerSocketUtils
import com.marlin.helpers.mongodb.EmbeddedMongo._
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{Command, MongodExecutable, MongodProcess, MongodStarter}
import de.flapdoodle.embed.process.extract.UUIDTempNaming
import de.flapdoodle.embed.process.io.directories.UUIDDir
import org.mongodb.scala.MongoClient

trait EmbeddedMongo {

  private val embedMongoDBVersion = Version.V3_3_1

  private lazy val runtime = MongodStarter.getInstance(runtimeConfig)

  private[mongodb] val embedConnectionURL: String = "localhost"
  private[mongodb] lazy val (mongodExe, mongod, mongoDB, embedConnectionPort) = {
    def initializeEnvironment(): (MongodExecutable, MongodProcess, MongoClient, Int) = {
      val port = ServerSocketUtils.availablePort
      val config = new MongodConfigBuilder().net(new Net("0.0.0.0", port, true)).version(embedMongoDBVersion).build
      try {
        val mongodExe = runtime.prepare(config)
        val mongod = mongodExe.start
        val mongoDB = MongoClient(s"mongodb://$embedConnectionURL:$port")
        (mongodExe, mongod, mongoDB, port)
      } catch {
        case _: IOException => initializeEnvironment()
      }
    }
    initializeEnvironment()
  }
}

object EmbeddedMongo {
  private val command = Command.MongoD

//  final val runtimeConfig = new RuntimeConfigBuilder()
//    .defaults(command)
//    .artifactStore(new ExtractedArtifactStoreBuilder()
//      .defaults(command)
//      .download(new DownloadConfigBuilder()
//        .defaultsForCommand(command)
//        .artifactStorePath(new UUIDDir)
//        .build)
//      .executableNaming(new UUIDTempNaming))
//    .build
  val runtimeConfig = new RuntimeConfigBuilder().defaults(command).build
}
