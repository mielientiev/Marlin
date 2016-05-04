package com.marlin.modules

import com.google.inject.AbstractModule
import com.marlin.config.MongoConfig
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule


class AppModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    val mongoConfig = ConfigFactory.load("database")
    bind[MongoConfig].toInstance(new MongoConfig(mongoConfig))
  }
}
