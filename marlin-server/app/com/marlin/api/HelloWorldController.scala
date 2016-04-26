package com.marlin.api

import play.api.mvc.{Action, Controller}


class HelloWorldController extends Controller {

  def helloWorld() = Action {
    Ok("Hello World")
  }

}
