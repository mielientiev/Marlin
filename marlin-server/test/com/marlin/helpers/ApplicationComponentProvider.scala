package com.marlin.helpers

import net.codingwell.scalaguice.ScalaModule

/**
  * @author ntviet18@gmail.com
  */
abstract class ApplicationComponentProvider {

  def modules: Seq[ScalaModule]
}
