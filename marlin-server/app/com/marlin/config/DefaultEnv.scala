package com.marlin.config

import com.marlin.model.User
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

/**
  * @author ntviet18@gmail.com
  */
trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}
