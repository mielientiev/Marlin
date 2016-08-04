package com.marlin.model

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.Json

/**
 * @author ntviet18@gmail.com
 */
case class User(
  id: Option[String],
  loginInfo: LoginInfo,
  fullName: Option[String],
  email: Option[String])
  extends Identity {
}

object User {
  implicit val jsonFormat = Json.format[User]
}

