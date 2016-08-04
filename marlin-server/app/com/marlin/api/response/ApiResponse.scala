package com.marlin.api.response

/**
  * @author ntviet18@gmail.com
  */
case class ApiResponse[R](msg: String, success: Boolean = true, result: Option[R] = None)
