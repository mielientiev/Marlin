package com.marlin.service

import com.marlin.model.User
import com.mohiva.play.silhouette.api.services.IdentityService

import scala.concurrent.Future

/**
  * @author ntviet18@gmail.com
  */
trait UserService extends IdentityService[User]{

  def save(user: User): Future[User]
}
