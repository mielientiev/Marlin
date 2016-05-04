package com.marlin.model

import play.api.libs.json.Json

case class FishingReport(location: String, timeStart: String, timeEnd: String, rating: Byte, catches: Seq[Catch] = Seq(), id: Option[String] = None)

object FishingReport {
  implicit val jsonFormat = Json.format[FishingReport]
}