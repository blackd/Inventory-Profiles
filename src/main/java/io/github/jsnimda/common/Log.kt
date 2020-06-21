package io.github.jsnimda.common

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Log {
  private const val id = "inventoryprofiles"
  val innerLogger: Logger = LogManager.getLogger(id)

  var debugHandler: (() -> String) -> Unit = { }

  fun error(message: String) = innerLogger.error("[$id] $message")
  fun warn(message: String) = innerLogger.warn("[$id] $message")
  fun info(message: String) = innerLogger.info("[$id] $message")

  fun debug(message: String) = debugHandler { message }
  fun debug(message: () -> String) = debugHandler(message)

}
