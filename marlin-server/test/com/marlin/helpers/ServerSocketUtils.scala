package com.marlin.helpers

import java.io.IOException
import java.net.ServerSocket

/**
  * @author ntviet18@gmail.com
  */
object ServerSocketUtils {

  def availablePort: Int = {
    try {
      val socket = new ServerSocket(0)
      val port = socket.getLocalPort
      socket.close()
      port
    } catch {
      case _ : IOException => availablePort
    }
  }
}
