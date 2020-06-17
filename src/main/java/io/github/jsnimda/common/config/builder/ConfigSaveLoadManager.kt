package io.github.jsnimda.common.config.builder

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.IConfigElement
import io.github.jsnimda.common.util.exists
import io.github.jsnimda.common.util.readFileToString
import io.github.jsnimda.common.util.writeStringToFile
import io.github.jsnimda.common.vanilla.VanillaUtils
import java.io.IOException
import java.nio.file.Path

private val GSON = GsonBuilder().setPrettyPrinting().create()

interface Savable {
  fun save()
  fun load()
}

class ConfigSaveLoadManager(private val config: IConfigElement, private val path: String) : Savable {
  private val configFile: Path
    get() = VanillaUtils.configDirectory().resolve(path)

  override fun save() {
    try {
      GSON.toJson(config.toJsonElement()).apply {
        configFile.writeStringToFile(this)
      }
    } catch (e: IOException) {
      Log.error("Failed to save config file $path")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun load() {
    try {
      if (!configFile.exists()) return
      configFile.readFileToString().run {
        JsonParser().parse(this)
      }.apply {
        config.fromJsonElement(this)
      }
    } catch (e: IOException) {
      Log.error("Failed to load config file $path")
    } catch (e: JsonParseException) {
      Log.error("Failed to parse config file $path as Json")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

}