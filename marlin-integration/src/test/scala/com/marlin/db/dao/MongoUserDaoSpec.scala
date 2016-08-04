package com.marlin.db.dao

import com.marlin.config.MongoConfig
import com.marlin.db.client.MarlinMongoClient
import com.marlin.helpers.mongodb.MongoScalaTest
import com.marlin.model.User
import com.mohiva.play.silhouette.api.LoginInfo
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}
import play.api.Logger

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author ntviet18@gmail.com
  */
class MongoUserDaoSpec extends WordSpec with Matchers with MongoScalaTest {

  private val configMap = Map(
    "marlin.db.mongo.url" -> mongoConnectionURL,
    "marlin.db.mongo.port" -> mongoConnectionPort,
    "marlin.db.mongo.database" -> "marlin"
  )

  private val config = ConfigFactory.parseMap(configMap)
  private val mongoConfig = new MongoConfig(config)

  trait Scope {
    val mongoUserDao = new MongoUserDao(new MarlinMongoClient(mongoConfig))
    val preparedLoginInfo = LoginInfo("70dbb86d-7711-4904-8d45-6ed41b3aa18f", "a4fcca4f-ce75-4684-9279-edf866c758cf")
    val preparedUser = User(Some("1a6d7460-2efa-4c76-a75e-2d8741e2ae4f"), preparedLoginInfo,
      Some("The Viet Nguyen"), Some("ntviet18@gmail.com"))
    val randomUser = preparedUser.copy(id = Some("fbdba1f1-0e68-44b6-87bf-9737348f0df9"))
  }

  "Mongo User Dao" should {
    "generate user id after saving" in new Scope {
      val saved = Await.result(mongoUserDao.save(preparedUser.copy(id = None)), 10.second)

      Logger.debug(s"saved $saved")

      saved.id should not be None
    }

    "retrieve user after saving" in new Scope {
      val saved = Await.result(mongoUserDao.save(preparedUser.copy(id = None)), 10.second)

      Logger.debug(s"saved $saved")

      val loaded = Await.result(mongoUserDao.findById(saved.id.get), 10.second)

      Logger.debug(s"loaded $loaded")

      saved shouldBe loaded.get
    }

    "find user by id" in new Scope {
      LoadFromResource("./datasets/security/user.json", "marlin", "User") ~> {
        val loaded = Await.result(mongoUserDao.findById("1a6d7460-2efa-4c76-a75e-2d8741e2ae4f"), 10.second)

        Logger.debug(s"loaded $loaded")

        loaded shouldBe Some(preparedUser)
      }
    }

    "find all users" in new Scope {
      LoadFromResource("./datasets/security/10users.json", "marlin", "User") ~> {
        val loaded = Await.result(mongoUserDao.findAll(), 10.second)

        Logger.debug(s"all users $loaded")

        loaded should have size 10
      }
    }

    "find saved user by listing" in new Scope {
      Await.result(mongoUserDao.save(preparedUser), 10.second)

      val loaded = Await.result(mongoUserDao.findAll(), 10.second)

      Logger.debug(s"loaded $loaded")

      loaded should have size 1
    }

    "delete user by id" in new Scope {
      ClearAfterTest("marlin", "User") ~> {
        Await.result(mongoUserDao.remove("1a6d7460-2efa-4c76-a75e-2d8741e2ae4f"), 10.second)
      }
    }

    "unable to retrieve deleted user" in new Scope {
      ClearAfterTest("marlin", "User") ~> {
        Await.result(mongoUserDao.remove("1a6d7460-2efa-4c76-a75e-2d8741e2ae4f"), 10.second)

        val loaded = Await.result(mongoUserDao.findById("1a6d7460-2efa-4c76-a75e-2d8741e2ae4f"), 10.second)

        Logger.debug(s"loaded $loaded")

        loaded shouldBe None
      }
    }

    "find by login info" in new Scope {
      LoadFromResource("./datasets/security/user.json", "marlin", "User") ~> {
        val loaded = Await.result(mongoUserDao.findByLoginInfo(preparedLoginInfo), 10.second)

        Logger.debug(s"loaded $loaded")

        loaded shouldBe Some(preparedUser)
      }
    }
  }
}
