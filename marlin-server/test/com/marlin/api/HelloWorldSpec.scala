package com.marlin.api


import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

/**
  * Created by ihor_mielientiev on 4/26/16.
  */
class HelloWorldSpec extends PlaySpec with OneAppPerTest {

  "HelloWorld controller" should {
    "return 'Hello World' string" in {
      val page = route(app, FakeRequest(GET, "/")).get
      status(page) mustBe OK
      contentAsString(page) mustBe "Hello World"
    }
  }

}
