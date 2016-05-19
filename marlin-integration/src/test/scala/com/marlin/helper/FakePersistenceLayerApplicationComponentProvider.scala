package com.marlin.helper

import com.google.inject.AbstractModule
import com.marlin.config.MongoConfig
import com.marlin.helpers.ApplicationComponentProvider
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule

import scala.collection.JavaConversions._

/**
 * @author ntviet18@gmail.com
 */
class FakePersistenceLayerApplicationComponentProvider(mongoPort: String) extends ApplicationComponentProvider {

  override def modules: Seq[ScalaModule] = Seq(new PersistentModule)

  class PersistentModule extends AbstractModule with ScalaModule {

    override def configure(): Unit = {
      val configMap = Map(
        "marlin.db.mongo.url" -> "localhost",
        "marlin.db.mongo.port" -> mongoPort,
        "marlin.db.mongo.database" -> "marlin")
      val config = ConfigFactory.parseMap(configMap)
      bind[MongoConfig].toInstance(new MongoConfig(config))
    }
  }
}