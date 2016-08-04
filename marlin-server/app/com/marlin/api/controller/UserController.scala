package com.marlin.api.controller

import javax.inject.{Inject, Singleton}

import com.marlin.api.form.{LogInForm, SignUpForm}
import com.marlin.config.DefaultEnv
import com.marlin.model.User
import com.marlin.service.UserService
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, _}
import net.ceedubs.ficus.Ficus._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

/**
 * @author ntviet18@gmail.com
 */
@Singleton
class UserController @Inject() (
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  passwordHasher: PasswordHasher,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock) extends Controller {

  def authorized = silhouette.SecuredAction.async(Future.successful(Ok("accessible")))

  def signUp = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    def onSuccess(data: SignUpForm.Data): Future[Result] = {
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(BadRequest("user exists"))
        case None =>
          for {
            user <- userService.save(User(None, loginInfo, Some(data.email), Some(data.fullName)))
            authInfo <- authInfoRepository.add(loginInfo, passwordHasher.hash(data.password))
            authenticator <- silhouette.env.authenticatorService.create(loginInfo)
            value <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(value, Ok("user registered"))
          } yield {
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            result
          }
      }
    }

    SignUpForm.form.bindFromRequest.fold(form => Future.successful(BadRequest("signup failed")), onSuccess)
  }

  def logIn = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    def onSuccess(data: LogInForm.Data): Future[Result] = {
      val credentials = Credentials(data.email, data.password)
      credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
        val result = Ok("login success")
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            val conf = configuration.underlying
            silhouette.env.authenticatorService.create(loginInfo).map {
              case authenticator if data.rememberMe =>
                Logger.debug("remember me is chosen")
                val authenticationExpiry = conf.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry")
                authenticator.copy(
                  expirationDateTime = clock.now.plus(authenticationExpiry.toMillis),
                  idleTimeout = conf.getAs[FiniteDuration]("silhouette.authenticator.remeberMe.authenticatorIdleTimeout"),
                  cookieMaxAge = conf.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge"))
              case authenticator => authenticator
            }.flatMap { authenticator =>
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              silhouette.env.authenticatorService.init(authenticator).flatMap { value =>
                silhouette.env.authenticatorService.embed(value, result)
              }
            }
          case None => Future.failed(new IdentityNotFoundException("user not exists"))
        }
      }.recover {
        case e: ProviderException => BadRequest("invalid credentials")
      }
    }

    LogInForm.form.bindFromRequest.fold(form => Future.successful(BadRequest("login failed")), onSuccess)
  }

  def logOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok("logout success"))
  }
}
