package com.marlin.db.dao.fishingreport

import com.google.inject.ImplementedBy
import com.marlin.model.FishingReport

import scala.concurrent.Future


@ImplementedBy(classOf[MongoFishingReportDao])
trait FishingReportDao {

  def findAll(from: Int, limit: Int): Future[Seq[FishingReport]]

  def save(report: FishingReport): Future[FishingReport]

  def findById(id: String): Future[Option[FishingReport]]

}
