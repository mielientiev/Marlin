package com.marlin.model

import play.api.libs.json.Json

case class Lure(brand: String, model: String, size: String, color: String)

object Lure {
  implicit val jsonFormat = Json.format[Lure]
}