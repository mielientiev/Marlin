package com.marlin.api.error

import play.api.libs.json.Json


case class ApiError(errorMessage: String)

object ApiError {
  implicit val format = Json.format[ApiError]
}