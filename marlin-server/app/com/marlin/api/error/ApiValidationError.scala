package com.marlin.api.error

import play.api.libs.json.Json


case class ApiValidationError(param: Option[String], description: String)

object ApiValidationError {
  implicit val format = Json.format[ApiValidationError]
}
