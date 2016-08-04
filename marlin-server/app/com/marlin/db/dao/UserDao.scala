package com.marlin.db.dao

import com.marlin.model.User
import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future

/**
  * @author ntviet18@gmail.com
  */
trait UserDao extends CrudDao[User] {

  def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]]
}
