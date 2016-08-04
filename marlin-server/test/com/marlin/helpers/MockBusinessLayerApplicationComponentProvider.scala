package com.marlin.helpers

import com.google.inject.AbstractModule
import com.marlin.service.UserService
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.mock.MockitoSugar

/**
  * @author ntviet18@gmail.com
  */
class MockBusinessLayerApplicationComponentProvider extends ApplicationComponentProvider with MockitoSugar {

  override def modules: Seq[ScalaModule] = Seq(new MockBusinessModule)

  final val userServiceMock = mock[UserService]

  class MockBusinessModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
      bind[UserService].toInstance(userServiceMock)
    }
  }

}
