package com.marlin.api.controller

import com.marlin.api.form.{LogInForm, SignUpForm}
import com.marlin.helpers.{ApplicationBuilder, MockBusinessLayerApplicationComponentProvider}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.Application
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

/**
  * @author ntviet18@gmail.com
  */
class UserControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  "Sign up action" should {
    "return BAD_REQUEST if processing form failed" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.signUp())
          .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet18@gmail.com", password = "password", rememberMe = false)))
      )

      status(result) mustEqual BAD_REQUEST
    }
  }

  "Log in action" should {
    "return BAD_REQUEST if processing form failed" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password")))
      )

      status(result) mustEqual BAD_REQUEST
    }

    "return BAD_REQUEST if user does not exist" in {
      when(businessLayerMocks.userServiceMock.retrieve(LoginInfo(CredentialsProvider.ID, "ntviet18@gmail.com")))
        .thenReturn(Future.successful(None))

      val Some(result) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet18@gmail.com", password = "password", rememberMe = false)))
      )

      status(result) mustEqual BAD_REQUEST
    }
  }

  private val businessLayerMocks = new MockBusinessLayerApplicationComponentProvider
  override implicit lazy val app: Application = ApplicationBuilder.buildWith(businessLayerMocks)
}
