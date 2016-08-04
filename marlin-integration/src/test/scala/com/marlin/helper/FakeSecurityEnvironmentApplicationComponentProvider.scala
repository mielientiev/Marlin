package com.marlin.helper

import com.google.inject.AbstractModule
import com.marlin.config.DefaultEnv
import com.marlin.helper.FakeSecurityEnvironmentApplicationComponentProvider._
import com.marlin.helpers.ApplicationComponentProvider
import com.marlin.model.User
import com.mohiva.play.silhouette.api.{Environment, LoginInfo}
import com.mohiva.play.silhouette.test._
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.Execution.Implicits._

/**
  * @author ntviet18@gmail.com
  */
class FakeSecurityEnvironmentApplicationComponentProvider extends ApplicationComponentProvider {

  override def modules: Seq[ScalaModule] = Seq(new SecurityEnvironmentModule)

  class SecurityEnvironmentModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
      bind[Environment[DefaultEnv]].toInstance(env)
    }
  }
}

object FakeSecurityEnvironmentApplicationComponentProvider {

  final val fakeIdentity = User(
    id = Some("70dbb86d-7711-4904-8d45-6ed41b3aa18f"),
    loginInfo = LoginInfo("credentials", "ntviet18@gmail.com"),
    fullName = Some("The Viet Nguyen"),
    email = Some("ntviet18@gmail.com"))

  implicit val env: Environment[DefaultEnv] =
    FakeEnvironment[DefaultEnv](Seq(fakeIdentity.loginInfo -> fakeIdentity))
}
