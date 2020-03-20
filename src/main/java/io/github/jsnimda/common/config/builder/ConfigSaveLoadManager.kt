package io.github.jsnimda.common.config.builder

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import io.github.jsnimda.common.config.IConfigElement
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.inventoryprofiles.Log
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

private val GSON = GsonBuilder().setPrettyPrinting().create()
private val configDirectory: File
  get() = File(Vanilla.runDirectory(), "config")
interface Savable {
  fun save()
  fun load()
}
class ConfigSaveLoadManager(private val config: IConfigElement, private val path: String) : Savable {
  private val configFile: File
    get() = configDirectory.toPath().resolve(path).toFile()

  override fun save() {
    try {
      GSON.toJson(config.toJsonElement()).apply {
        FileUtils.writeStringToFile(configFile, this, StandardCharsets.UTF_8)
      }
    } catch (e: IOException) {
      Log.error("Failed to save config file $path")
    }
  }

  override fun load() {
    if (!configFile.exists()) return
    try {
      FileUtils.readFileToString(configFile, StandardCharsets.UTF_8).run {
        JsonParser().parse(this)
      }.apply {
        config.fromJsonElement(this)
      }
    } catch (e: IOException) {
      Log.error("Failed to load config file $path")
    } catch (e: JsonParseException) {
      Log.error("Failed to parse config file $path as Json")
    }
  }

}