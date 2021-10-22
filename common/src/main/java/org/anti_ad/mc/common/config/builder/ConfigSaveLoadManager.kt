package org.anti_ad.mc.common.config.builder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToStream
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.config.IConfigElement
import org.anti_ad.mc.common.extensions.*
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.glue.loggingPath
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.outputStream
import kotlin.io.path.readText

class ConfigSaveLoadManager(private val config: IConfigElement,
                            path: String) : Savable {
    private val configFile: Path = VanillaUtil.configDirectory() / path
    private val path = configFile.loggingPath

    @OptIn(ExperimentalSerializationApi::class)
    private val encoder = Json {
        prettyPrintIndent = "    "
        prettyPrint = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun save() {
        try {
            //config.oldToJsonElement().toJsonString().writeToFile(configFile)
            val el = config.toJsonElement()
            encoder.encodeToStream(JsonElement.serializer(), el, configFile.outputStream())
            //config.toJsonElement().toString().writeToFile(configFile)
        } catch (e: IOException) {
            Log.error("Failed to write config file $path")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun load(clientWorld: Any?) {
        try {
            if (!configFile.exists()) return
            configFile.readText().toJsonElement()
                .let {
                    //config.oldFromJsonElement(it)
                    config.fromJsonElement(it)
                }
        } catch (e: IOException) {
            Log.error("Failed to read config file $path")
        } catch (e: SerializationException) {
            Log.error("Failed to parse config file $path as JSON")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}