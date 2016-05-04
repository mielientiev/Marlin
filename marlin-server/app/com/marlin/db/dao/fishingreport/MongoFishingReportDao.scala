package com.marlin.db.dao.fishingreport

import java.util.UUID
import javax.inject.Inject

import com.marlin.db.client.MarlinMongoClient
import com.marlin.model.FishingReport
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json

import scala.concurrent.Future


class MongoFishingReportDao @Inject()(client: MarlinMongoClient) extends FishingReportDao {

  private val collection = client.mongoDatabase.getCollection("fishingReports")

  override def findById(id: String): Future[Option[FishingReport]] = {
    collection.find(Document("id" -> id)).first().toFuture().map { docSeq =>
      docSeq.headOption.map { doc =>
        Json.parse(doc.toJson()).as[FishingReport]
      }
    }
  }

  override def save(report: FishingReport): Future[FishingReport] = {
    val id = UUID.randomUUID().toString
    val savedReport = report.copy(id = Some(id))
    collection.insertOne(Document(Json.toJson(savedReport).toString())).toFuture().map(_ => savedReport)
  }

  override def findAll(from: Int, limit: Int): Future[Seq[FishingReport]] = {
    collection.find().skip(from).limit(limit).toFuture().map{seqDoc =>
      seqDoc.map(doc=>Json.parse(doc.toJson()).as[FishingReport])
    }
  }

}
