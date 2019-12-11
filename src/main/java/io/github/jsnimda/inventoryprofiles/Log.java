package io.github.jsnimda.inventoryprofiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;

/**
 * Log
 */
public class Log {

  public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

  public static void debugLogs(String message) {
    if (AdvancedOptions.DEBUG_LOGS.getBooleanValue()) {
      info(message);
    }
  }

  public static void info(String message) {
    LOGGER.info(message);
  }

  public static void info(String message, Object... params) {
    LOGGER.info(message, params);
  }

  public static void error(String message) {
    LOGGER.error(message);
  }

  public static void error(String message, Object... params) {
    LOGGER.error(message, params);
  }

  public static void warn(String message) {
    LOGGER.warn(message);
  }

  public static void warn(String message, Object... params) {
    LOGGER.warn(message, params);
  }

  

}