package com.marlin.helpers.mongodb


trait MongoTestFrameworkInterface {
  def setup()
  def cleanUp()
}

object MongoTestFrameworkInterface {

  import org.scalatest.{BeforeAndAfterAll, Suite}

  trait ScalaTest extends MongoTestFrameworkInterface with BeforeAndAfterAll {
    this: Suite =>

    abstract override protected def beforeAll(): Unit = {
      setup()
      super.beforeAll()
    }

    abstract override protected def afterAll(): Unit = {
      cleanUp()
      super.afterAll()
    }
  }

}
