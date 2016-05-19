package com.marlin.service

import com.marlin.db.dao.UserDao
import com.marlin.model.User
import com.mohiva.play.silhouette.api.LoginInfo
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

/**
  * @author ntviet18@gmail.com
  */
class UserServiceImplSpec extends WordSpec with Matchers with ScalaFutures with MockitoSugar {

  trait Scope {
    val userDaoMock = mock[UserDao]
    val userServiceImpl = new UserServiceImpl(userDaoMock)

    val userMock = mock[User]
  }

  "User Service Impl" should {
    "retrieve user by login info" in new Scope {
      val loginInfoMock = mock[LoginInfo]

      when(userDaoMock.findByLoginInfo(loginInfoMock)).thenReturn(Future.successful(Some(userMock)))

      whenReady(userServiceImpl.retrieve(loginInfoMock)) { res =>
        res shouldBe Some(userMock)
      }

      verify(userDaoMock).findByLoginInfo(loginInfoMock)
    }

    "save user" in new Scope {
      when(userDaoMock.save(userMock)).thenReturn(Future.successful(userMock))

      whenReady(userServiceImpl.save(userMock)) { res =>
        res shouldBe userMock
      }

      verify(userDaoMock).save(userMock)
    }
  }
}
