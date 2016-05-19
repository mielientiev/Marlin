package com.marlin.api.form

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

/**
  * @author ntviet18@gmail.com
  */
object LogInForm {

  val form = Form(
    mapping("email" -> email,
      "password" -> nonEmptyText,
      "rememberMe" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    email: String,
    password: String,
    rememberMe: Boolean
  )

  object Data {
    implicit val format = Json.format[Data]
  }

}
