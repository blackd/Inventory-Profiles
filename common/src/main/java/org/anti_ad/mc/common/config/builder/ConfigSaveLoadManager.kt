package org.anti_ad.mc.common.config.builder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.json.jsonObject
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
            var saveAfterLoad = false
            var j = configFile.readText().toJsonElement()
            val jo = j.jsonObject
            if (jo["LockedSlotsSettings"] == null || jo["AutoRefillSettings"] == null) {
                val ms = jo["ModSettings"]
                if (ms != null) {
                    val converted: MutableMap<String, JsonElement> = jo.toMutableMap()
                    if (jo["LockedSlotsSettings"] == null) {
                        saveAfterLoad = true
                        converted["LockedSlotsSettings"] = ms
                    }
                    if (jo["AutoRefillSettings"] == null) {
                        saveAfterLoad = true
                        converted["AutoRefillSettings"] = ms
                    }
                    if (saveAfterLoad) {
                        j = JsonObject(converted)
                    }
                }

            }

            //config.oldFromJsonElement(it)
            config.fromJsonElement(j)
            if(saveAfterLoad) {
                save()
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