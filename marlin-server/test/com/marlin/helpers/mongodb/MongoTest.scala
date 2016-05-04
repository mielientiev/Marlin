package com.marlin.helpers.mongodb

import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{Completed, MongoCollection}
import org.scalatest.Suite

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


trait MongoTest extends MongoRequestBuilding with EmbeddedMongo {
  this: MongoTestFrameworkInterface ⇒

  private def dropCollection(collection: MongoCollection[Document]): Unit = {
    Await.ready(collection.drop().toFuture(), 10.seconds)
  }

  private def importJson(collection: MongoCollection[Document], lines: Seq[String]): Future[Seq[Completed]] = {
    Await.ready(collection.insertMany(lines.map(json => Document(json))).toFuture(), 10.seconds)
  }

  implicit class WithSeqMongoRequestLoading(seqRequest: SeqMongoRequest) {
    def ~>(newReq: SeqMongoRequest): SeqMongoRequest = {
      SeqMongoRequest(newReq, seqRequest)
    }

    def ~>[A](block: => A): Unit = {
      seqRequest.requests.foreach { request =>
        val collection = mongoDB.getDatabase(request.database).getCollection(request.collection)
        importJson(collection, request.jsonList)
      }
      block
      seqRequest.requests.foreach { request =>
        val collection = mongoDB.getDatabase(request.database).getCollection(request.collection)
        dropCollection(collection)
      }
    }
  }

}


trait MongoScalaTest extends MongoTest with MongoTestFrameworkInterface.ScalaTest {
  this: Suite =>

  override def setup() = {
    mongod
  }

  override def cleanUp() = {
    mongoDB.close()
    mongod.stop()
    mongodExe.stop()
  }

}