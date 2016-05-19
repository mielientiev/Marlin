package com.marlin.db.dao

import java.util.UUID
import javax.inject.Inject

import com.marlin.db.client.MarlinMongoClient
import com.marlin.model.User
import com.mohiva.play.silhouette.api.LoginInfo
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
  * @author ntviet18@gmail.com
  */
class MongoUserDao @Inject()(client: MarlinMongoClient) extends UserDao {

  private val collection = client.mongoDatabase.getCollection("User")

  override def save(user: User): Future[User] = {
    Logger.trace(s"saving user $user...")
    val savedUser = user.copy(id = Some(UUID.randomUUID.toString))
    collection.insertOne(Document(Json.toJson(savedUser).toString)).toFuture.map(_ => savedUser)
  }

  override def findById(id: String): Future[Option[User]] = {
    Logger.trace(s"finding user by id $id...")
    collection.find(Document("id" -> id)).first.toFuture.map { docs =>
      Logger.trace(s"found users => $docs")
      docs.headOption.map(doc => Json.parse(doc.toJson).as[User])
    }
  }

  override def findAll(): Future[Seq[User]] = {
    Logger.trace("finding all users...")
    collection.find.toFuture.map { docs =>
      Logger.trace(s"found users => $docs")
      docs.map(doc => Json.parse(doc.toJson).as[User])
    }
  }

  override def remove(id: String): Future[Unit] = {
    Logger.trace(s"deleting user with id $id...")
    collection.deleteOne(Document("id" -> id)).toFuture.map(_ => ())
  }

  override def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]] = {
    Logger.trace(s"finding user by login info $loginInfo...")
    collection.find(Document("loginInfo" -> Document(Json.toJson(loginInfo).toString))).first.toFuture.map { docs =>
      Logger.trace(s"found users => $docs")
      docs.headOption.map(doc => Json.parse(doc.toJson).as[User])
    }
  }
}
