package com.marlin.api.controller

import com.marlin.api.form.{LogInForm, SignUpForm}
import com.marlin.config.DefaultEnv
import com.marlin.helper.FakeSecurityEnvironmentApplicationComponentProvider._
import com.marlin.helper.{FakePersistenceLayerApplicationComponentProvider, FakeSecurityEnvironmentApplicationComponentProvider}
import com.marlin.helpers.ApplicationBuilder
import com.marlin.helpers.mongodb.MongoScalaTest
import com.mohiva.play.silhouette.test._
import org.scalatestplus.play._
import play.api.Application
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * @author ntviet18@gmail.com
 */
class UserControllerSpec extends PlaySpec with OneAppPerSuite with MongoScalaTest {

  //-- authorized action
  "Authorized Action" should {
    "return OK if user is authorized" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.authorized())
        .withAuthenticator[DefaultEnv](fakeIdentity.loginInfo))

      status(result) mustEqual OK
    }

    "return UNAUTHORIZED if user did not log in" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.authorized()))

      status(result) mustEqual UNAUTHORIZED
    }
  }

  //-- signup action
  "Sign Up Action" should {
    "register new user" in {
      val Some(register) = route(app, FakeRequest(routes.UserController.signUp())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password"))))

      status(register) mustEqual OK
      cookies(register).find(_.name == "id") mustBe defined
      cookies(register).find(_.name == "id").map(_.secure) mustBe Some(true)

      val Some(logIn) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet18@gmail.com", password = "password", rememberMe = false))))

      status(logIn) mustEqual OK
    }

    "return BAD_REQUEST if user exists" in {
      route(app, FakeRequest(routes.UserController.signUp())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password"))))

      val Some(result) = route(app, FakeRequest(routes.UserController.signUp())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password"))))

      status(result) mustEqual BAD_REQUEST
    }
  }

  //-- login action
  "Log In Action" should {
    "return authentication cookie" in {
      route(app, FakeRequest(routes.UserController.signUp())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password"))))

      val Some(result) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet18@gmail.com", password = "password", rememberMe = false))))

      status(result) mustEqual OK
      cookies(result).find(_.name == "id") mustBe defined
      cookies(result).find(_.name == "id").map(_.secure) mustBe Some(true)
    }

    "return authentication cookie with predefined expiration date on remberme" in {
      route(app, FakeRequest(routes.UserController.signUp())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password"))))

      val Some(result) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet18@gmail.com", password = "password", rememberMe = true))))

      status(result) mustEqual OK
      cookies(result).find(_.name == "id").map(_.secure) mustBe Some(true)
      cookies(result).find(_.name == "id").map(_.maxAge) mustBe defined
    }

    "return BAD_REQUEST if credentials are invalid" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet18@gmail.com", password = "invalid", rememberMe = false))))

      status(result) mustEqual BAD_REQUEST
    }

    "return BAD_REQUEST if user does not exist" in {
      route(app, FakeRequest(routes.UserController.signUp())
        .withJsonBody(Json.toJson(SignUpForm.Data(fullName = "The Viet Nguyen", email = "ntviet18@gmail.com", password = "password"))))

      val Some(result) = route(app, FakeRequest(routes.UserController.logIn())
        .withJsonBody(Json.toJson(LogInForm.Data(email = "ntviet19@gmail.com", password = "password", rememberMe = false))))

      status(result) mustEqual BAD_REQUEST
    }
  }

  //-- logout action
  "Log Out Action" should {
    "clean up authentication cookie" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.logOut())
        .withAuthenticator[DefaultEnv](fakeIdentity.loginInfo))

      status(result) mustEqual OK
    }

    "return UNAUTHORIZED if user is not authorized" in {
      val Some(result) = route(app, FakeRequest(routes.UserController.logOut()))

      status(result) mustEqual UNAUTHORIZED
    }
  }

  override implicit lazy val app: Application = ApplicationBuilder.buildWith(
    new FakePersistenceLayerApplicationComponentProvider(mongoConnectionPort),
    new FakeSecurityEnvironmentApplicationComponentProvider
  )
}