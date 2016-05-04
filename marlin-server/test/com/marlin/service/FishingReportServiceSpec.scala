package com.marlin.service

import com.marlin.db.dao.fishingreport.FishingReportDao
import com.marlin.model.{Catch, FishingReport, Lure}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

import scala.concurrent.Future

class FishingReportServiceSpec extends WordSpec with Matchers with ScalaFutures with MockitoSugar {

  trait Scope {
    val catchs = Catch("zander", "24/04/2016 7:45", Lure("bait breath", "curly grab", "2.5", "R29"))
    val expectedReport = FishingReport("Kampiza", "24/04/2016 6:30", "24/04/2016 13:30", 2, Seq(catchs, catchs))
    val repository = mock[FishingReportDao]
    val fishingReportService = new FishingReportService(repository)
  }

  "Fish Report Service" should {
    "return fish report by id" in new Scope {
      when(repository.findById("123-456")).thenReturn(Future.successful(Some(expectedReport)))

      val result = fishingReportService.findById("123-456")
      whenReady(result) { res =>
        res shouldBe Some(expectedReport)
      }
      verify(repository).findById("123-456")
    }

    "not return fish report by id if it doesn't exist" in new Scope {
      when(repository.findById("123-456")).thenReturn(Future.successful(None))

      val result = fishingReportService.findById("123-456")
      whenReady(result) { res =>
        res shouldBe None
      }
      verify(repository).findById("123-456")
    }

    "save fishing report and return it with id" in new Scope {
      when(repository.save(expectedReport)).thenReturn(Future.successful(expectedReport.copy(id = Some("1111"))))

      val result = fishingReportService.save(expectedReport)
      whenReady(result) { res =>
        res shouldBe expectedReport.copy(id = Some("1111"))
      }
      verify(repository).save(expectedReport)
    }

    "return list of fishing reports" in new Scope {
      val listReports = List.fill(10)(expectedReport)
      when(repository.findAll(from = 0, limit = 10)).thenReturn(Future.successful(listReports))

      val result = fishingReportService.findAll(0, 10)
      whenReady(result) { res =>
        res shouldBe listReports
      }
      verify(repository).findAll(0, 10)
    }

  }

}
