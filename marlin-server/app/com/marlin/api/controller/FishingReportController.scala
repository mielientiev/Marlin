package com.marlin.api.controller

import javax.inject.Singleton

import com.google.inject.Inject
import com.marlin.api.util.CrudController
import com.marlin.model.FishingReport
import com.marlin.service.FishingReportService
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action

@Singleton
class FishingReportController @Inject()(fishingReportService: FishingReportService) extends CrudController {

  def getAll(from: Int, limit: Int) = Action.async {
    validatePagination(from, limit) {
      fishingReportService.findAll(from, limit)
    }
  }

  def getById(id: String) = Action.async {
    fishingReportService.findById(id)
  }

  def saveReport() = Action.async(parse.json) { request =>
    to[FishingReport](request.body) { report =>
      fishingReportService.save(report).map { savedReport =>
        Created(Json.toJson(savedReport)).withHeaders(LOCATION -> routes.FishingReportController.getById(savedReport.id.getOrElse("")).url)
      }
    }
  }
}
