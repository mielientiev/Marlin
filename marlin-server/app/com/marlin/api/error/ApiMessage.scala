package com.marlin.api.error

import play.api.libs.json.Json


case class ApiMessage(msg: String)

object ApiMessage {
  implicit val format = Json.format[ApiMessage]
}