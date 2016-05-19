package com.marlin.service

import javax.inject.Inject

import com.marlin.db.dao.UserDao
import com.marlin.model.User
import com.mohiva.play.silhouette.api.LoginInfo
import play.api.Logger

import scala.concurrent.Future

/**
  * @author ntviet18@gmail.com
  */
class UserServiceImpl @Inject() (userDao: UserDao) extends UserService {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    Logger.debug(s"retrieving user with login info $loginInfo...")
    userDao.findByLoginInfo(loginInfo)
  }

  override def save(user: User): Future[User] = {
    Logger.debug(s"saving user $user")
    userDao.save(user)
  }
}
