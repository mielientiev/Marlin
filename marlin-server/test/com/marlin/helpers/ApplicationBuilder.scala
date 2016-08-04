package com.marlin.helpers

import play.api.Mode
import play.api.inject.guice.GuiceApplicationBuilder

/**
  * @author ntviet18@gmail.com
  */
object ApplicationBuilder {
  def buildWith(componentProviders: ApplicationComponentProvider*) =
    new GuiceApplicationBuilder().overrides(componentProviders.flatMap(_.modules)).in(Mode.Test).build
}
