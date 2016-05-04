package com.marlin.api.util

import com.wix.accord.dsl._


case class Pagination(from: Int, limit: Int)


object Pagination {

  private val Limit = 100

  implicit val pageValidator = validator[Pagination] { page =>
    page.limit should be > 0
    page.limit should be < Limit
    page.from should be >= 0
  }

}
