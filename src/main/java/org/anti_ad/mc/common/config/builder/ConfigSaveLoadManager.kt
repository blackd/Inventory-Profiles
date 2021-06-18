package org.anti_ad.mc.common.config.builder

import com.google.gson.JsonParseException
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.config.IConfigElement
import org.anti_ad.mc.common.extensions.*
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.loggingPath
import java.io.IOException
import java.nio.file.Path

class ConfigSaveLoadManager(private val config: IConfigElement, path: String) : Savable {
  private val configFile: Path = VanillaUtil.configDirectory() / path
  private val path = configFile.loggingPath

  override fun save() {
    try {
      config.toJsonElement().toJsonString().writeToFile(configFile)
    } catch (e: IOException) {
      Log.error("Failed to write config file $path")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun load() {
    try {
      if (!configFile.exists()) return
      configFile.readToString().parseAsJson()
        .let { config.fromJsonElement(it) }
    } catch (e: IOException) {
      Log.error("Failed to read config file $path")
    } catch (e: JsonParseException) {
      Log.error("Failed to parse config file $path as JSON")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

}