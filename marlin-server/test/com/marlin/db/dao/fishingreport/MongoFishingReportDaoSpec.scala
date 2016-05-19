package com.marlin.db.dao.fishingreport


import com.marlin.config.MongoConfig
import com.marlin.db.client.MarlinMongoClient
import com.marlin.helpers.mongodb.MongoScalaTest
import com.marlin.model.{Catch, FishingReport, Lure}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._

class MongoFishingReportDaoSpec extends WordSpec with Matchers with MongoScalaTest {

  private val configMap = Map(
    "marlin.db.mongo.url" -> mongoConnectionURL,
    "marlin.db.mongo.port" -> mongoConnectionPort,
    "marlin.db.mongo.database" -> "marlin"
  )

  private val config = ConfigFactory.parseMap(configMap)
  private val mongoConfig = new MongoConfig(config)

  trait Scope {
    val mongoFishingReportDao = new MongoFishingReportDao(new MarlinMongoClient(mongoConfig))
    val firstCatch = Catch("zander", "24/04/2016 7:45", Lure("bait breath", "curly grab", "2.5", "R29"))
    val expectedReport = FishingReport("YY", "24/04/2016 6:30", "24/04/2016 13:30", 4, Seq(firstCatch), Some("111-111-111-111"))
  }

  "Mongo Fishing Report Dao" should {
    "get report by id" in new Scope {
      LoadFromResource("./datasets/fishing-report/singleReport.json", "marlin", "fishingReports") ~> {
        val reportF = mongoFishingReportDao.findById("111-111-111-111")
        val report = Await.result(reportF, 10.second)
        report shouldBe Some(expectedReport)
      }
    }
    "return None if report doesn't exist" in new Scope {
      val reportF = mongoFishingReportDao.findById("111-111-111-111")
      val report = Await.result(reportF, 10.second)
      report shouldBe None
    }

    "save report to storage and return report with id" in new Scope {
      ClearAfterTest("marlin", "fishingReports") ~> {
        val savedReportF = mongoFishingReportDao.save(expectedReport)
        val savedReport = Await.result(savedReportF, 10.second)
        savedReport.id should not be None

        val byIdReport = Await.result(mongoFishingReportDao.findById(savedReport.id.getOrElse("")), 10.second)
        savedReport shouldBe byIdReport.get
      }
    }

    "find all reports if limit is greater than reports count" in new Scope {
      LoadFromResource("./datasets/fishing-report/10Reports.json", "marlin", "fishingReports") ~> {
        val reportsF = mongoFishingReportDao.findAll(from = 0, limit = 12)
        val reports = Await.result(reportsF, 10.second)
        reports should have size 10
      }
    }

    "find limited reports (from 0 and limit 10) when in DB 15" in new Scope {
      LoadFromResource("./datasets/fishing-report/15Reports.json", "marlin", "fishingReports") ~> {
        val reportsF = mongoFishingReportDao.findAll(from = 0, limit = 10)
        val reports = Await.result(reportsF, 10.second)

        reports should have size 10
      }
    }

    "find limited reports (from 12 and limit 8) when in DB 15" in new Scope {
      LoadFromResource("./datasets/fishing-report/15Reports.json", "marlin", "fishingReports") ~> {
        val reportsF = mongoFishingReportDao.findAll(from = 12, limit = 8)
        val reports = Await.result(reportsF, 10.second)

        reports should have size 3
      }
    }

    "find limited reports (from 15 and limit 2) when in DB 15" in new Scope {
      LoadFromResource("./datasets/fishing-report/15Reports.json", "marlin", "fishingReports") ~> {
        val reportsF = mongoFishingReportDao.findAll(from = 15, limit = 2)
        val reports = Await.result(reportsF, 10.second)

        reports should have size 0
      }
    }

    "find limited reports (from 10 and limit 1) when in DB 15" in new Scope {
      LoadFromResource("./datasets/fishing-report/15Reports.json", "marlin", "fishingReports") ~> {
        val reportsF = mongoFishingReportDao.findAll(from = 10, limit = 1)
        val reports = Await.result(reportsF, 10.second)

        reports should have size 1
      }
    }

    "delete single report from storage and return 1" in new Scope {
      LoadFromResource("./datasets/fishing-report/singleReport.json", "marlin", "fishingReports") ~> {
        val deletedF = mongoFishingReportDao.delete("111-111-111-111")
        val deletedNum = Await.result(deletedF, 10.second)

        deletedNum shouldBe 1
      }
    }

    "return 0 if report doesn't exist and nothing happens" in new Scope {
      ClearAfterTest("marlin", "fishingReports") ~> {
        val deletedF = mongoFishingReportDao.delete("111-111-111-111")
        val deletedNum = Await.result(deletedF, 10.second)

        deletedNum shouldBe 0
      }
    }
  }
}
