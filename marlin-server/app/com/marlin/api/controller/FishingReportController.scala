package com.marlin.api.controller

import javax.inject.Singleton

import com.google.inject.Inject
import com.marlin.api.error.{ApiError, ApiValidationError}
import com.marlin.api.util.Pagination
import com.marlin.model.FishingReport
import com.marlin.service.FishingReportService
import com.wix.accord._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

@Singleton
class FishingReportController @Inject()(fishingReportService: FishingReportService) extends Controller {

  def getAll(from: Int, limit: Int) = Action.async {
    validate(Pagination(from, limit)) match {
      case Success =>
        fishingReportService.findAll(from, limit).map { reports =>
          Ok(Json.obj("fishingReports" -> reports))
        }
      case Failure(errors) =>
        val validationErrors = errors.map(violation => ApiValidationError(violation.description, violation.constraint))
        Future.successful(BadRequest(Json.obj("errors" -> validationErrors)))
    }
  }

  def getById(id: String) = Action.async {
    fishingReportService.findById(id).map {
      case Some(value) => Ok(Json.toJson(value))
      case None => NotFound
    }
  }


  def saveReport() = Action.async(parse.json) { request =>
    request.body.validate[FishingReport] match {
      case JsSuccess(report, _) =>
        fishingReportService.save(report).map { savedReport =>
          Created(Json.toJson(savedReport))
            .withHeaders(LOCATION -> routes.FishingReportController.getById(savedReport.id.getOrElse("")).url)
        }
      case JsError(_) => Future.successful(BadRequest(Json.toJson(ApiError("Invalid json: " + request.body.toString()))))
    }
  }


}
