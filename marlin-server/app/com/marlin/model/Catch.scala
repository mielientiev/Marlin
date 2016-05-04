package com.marlin.model

import play.api.libs.json.Json


case class Catch(fish: String, time: String, lure: Lure)

object Catch {
  implicit val jsonFormat = Json.format[Catch]
}