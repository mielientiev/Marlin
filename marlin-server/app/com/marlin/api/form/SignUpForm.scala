package com.marlin.api.form

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

/**
  * @author ntviet18@gmail.com
  */
object SignUpForm {

  val form = Form(
    mapping(
      "fullName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    fullName: String,
    email: String,
    password: String
  )

  object Data {
    implicit val format = Json.format[Data]
  }
}