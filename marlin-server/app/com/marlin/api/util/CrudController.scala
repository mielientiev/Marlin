package com.marlin.api.util

import com.marlin.api.error.{ApiError, ApiValidationError}
import com.wix.accord.{Failure, Success, _}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Controller, Result}

import scala.concurrent.Future
import scala.language.implicitConversions


trait CrudController extends Controller {

  def validatePagination[A](from: Int, limit: Int)(block: => Future[Seq[A]])(implicit writes: Writes[A]): Future[Result] = {
    validate(Pagination(from, limit)) match {
      case Success => block.map(reports => Ok(Json.toJson(reports)))
      case Failure(errors) =>
        val validationErrors = errors.map(violation => ApiValidationError(violation.description, violation.constraint))
        Future.successful(BadRequest(Json.obj("errors" -> validationErrors)))
    }
  }

  def to[A](jsonBody: JsValue)(block: A => Future[Result])(implicit writes: Reads[A]): Future[Result] = {
    jsonBody.validate[A].fold(
      error => Future.successful(BadRequest(Json.toJson(ApiError("Invalid json: " + jsonBody.toString)))),
      report => block(report)
    )
  }

  implicit def optionToResult[A](future: Future[Option[A]])(implicit writes: Writes[A]): Future[Result] = {
    future.map {
      case Some(v) => Ok(Json.toJson(v))
      case None => NotFound
    }
  }

}
