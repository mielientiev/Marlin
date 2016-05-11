package com.marlin.service

import com.google.inject.Inject
import com.marlin.db.dao.fishingreport.FishingReportDao
import com.marlin.model.FishingReport

import scala.concurrent.Future

class FishingReportService @Inject() (fishingReportDao: FishingReportDao) {

  def delete(id: String): Future[Long] = {
    fishingReportDao.delete(id)
  }

  def findAll(from: Int, limit: Int): Future[Seq[FishingReport]] = {
    fishingReportDao.findAll(from, limit)
  }

  def save(report: FishingReport): Future[FishingReport] = {
    fishingReportDao.save(report)
  }

  def findById(id: String): Future[Option[FishingReport]] = {
    fishingReportDao.findById(id)
  }

}
