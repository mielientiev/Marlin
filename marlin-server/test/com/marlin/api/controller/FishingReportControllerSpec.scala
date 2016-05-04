package com.marlin.api.controller

import akka.stream.Materializer
import com.marlin.api.error.{ApiError, ApiValidationError}
import com.marlin.model.{Catch, FishingReport, Lure}
import com.marlin.service.FishingReportService
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class FishingReportControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  implicit lazy val materializer: Materializer = app.materializer

  trait Scope {
    val catchs = Catch("zander", "24/04/2016 7:45", Lure("bait breath", "curly grab", "2.5", "R29"))
    val report = FishingReport("Kampiza", "24/04/2016 6:30", "24/04/2016 13:30", 2, Seq(catchs, catchs))
    val fishService = mock[FishingReportService]
    val controller = new FishingReportController(fishService)
  }

  "Fishing Report Controller" should {
    "return fish report by id" in new Scope {
      when(fishService.findById("123")).thenReturn(Future.successful(Some(report)))
      val result = call(controller.getById("123"), FakeRequest(GET, "/api/fishreport/123"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(report)

      verify(fishService).findById("123")
    }

    "return 404 if fish report doesn't exist" in new Scope {
      when(fishService.findById("123")).thenReturn(Future.successful(None))
      val result = call(controller.getById("123"), FakeRequest(GET, "/api/fishreport/123"))

      status(result) mustBe NOT_FOUND
      verify(fishService).findById("123")
    }

    "save fish report and returns 201 with saved json and Location header" in new Scope {
      when(fishService.save(report)).thenReturn(Future.successful(report.copy(id = Some("11111-1111-1111"))))
      val request = FakeRequest(POST, "/api/fishreport").withJsonBody(Json.toJson(report))
      val result = call(controller.saveReport(), request)

      status(result) mustEqual CREATED
      header("Location", result) mustEqual Some("/api/fishreport/11111-1111-1111")
      contentAsJson(result) mustBe Json.toJson(report.copy(id = Some("11111-1111-1111")))
      verify(fishService).save(report)
    }

    "fails (BadRequest 400) while saving fish report if json is invalid" in new Scope {
      val request = FakeRequest(POST, "/api/fishreport").withJsonBody(Json.toJson("{abc: 1}"))
      val result = call(controller.saveReport(), request)

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustBe Json.toJson(ApiError("Invalid json: \"{abc: 1}\""))
      verifyZeroInteractions(fishService)
    }

    "return latest 10 reports" in new Scope {
      val reports = List.fill(10)(report)
      when(fishService.findAll(0, 10)).thenReturn(Future.successful(reports))
      val result = call(controller.getAll(from = 0, limit = 10), FakeRequest(GET, "/api/fishreport?from=0&limit=10"))

      status(result) mustEqual OK
      contentAsJson(result) mustBe Json.obj("fishingReports" -> reports)
      verify(fishService).findAll(0, 10)
    }

    "return BadRequest if 'limit' > 100" in new Scope {
      val result = call(controller.getAll(from = 0, limit = 101), FakeRequest(GET, "/api/fishreport?from=0&limit=101"))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("errors" -> Seq(ApiValidationError(Some("limit"), "got 101, expected less than 100")))
      verifyZeroInteractions(fishService)
    }

    "return BadRequest if 'limit' == 0" in new Scope {
      val result = call(controller.getAll(from = 0, limit = 0), FakeRequest(GET, "/api/fishreport?from=0&limit=0"))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("errors" -> Seq(ApiValidationError(Some("limit"), "got 0, expected more than 0")))
      verifyZeroInteractions(fishService)
    }

    "return BadRequest if 'limit' < 0" in new Scope {
      val result = call(controller.getAll(from = 0, limit = -1), FakeRequest(GET, "/api/fishreport?from=0&limit=-1"))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("errors" -> Seq(ApiValidationError(Some("limit"), "got -1, expected more than 0")))
      verifyZeroInteractions(fishService)
    }

    "return BadRequest if 'from' < 0" in new Scope {
      val result = call(controller.getAll(from = -1, limit = 10), FakeRequest(GET, "/api/fishreport?from=-1&limit=101"))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("errors" -> Seq(ApiValidationError(Some("from"), "got -1, expected 0 or more")))
      verifyZeroInteractions(fishService)
    }

    "return BadRequest if 'from' < 0 and limit < 0" in new Scope {
      val result = call(controller.getAll(from = -1, limit = -2), FakeRequest(GET, "/api/fishreport?from=-1&limit=-2"))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("errors" ->
        Seq(ApiValidationError(Some("limit"), "got -2, expected more than 0"), ApiValidationError(Some("from"), "got -1, expected 0 or more")))
      verifyZeroInteractions(fishService)
    }
  }

}
