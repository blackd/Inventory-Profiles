package org.anti_ad.mc.common.config.options

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.config.ConfigOptionBase
import org.anti_ad.mc.common.config.IConfigElementObject
import org.anti_ad.mc.common.input.AlternativeKeybind
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.MainKeybind

class ConfigHotkey(defaultStorageString: String,
                   defaultSettings: KeybindSettings) : ConfigOptionBase(), IConfigElementObject {
    val mainKeybind = MainKeybind(defaultStorageString,
                                  defaultSettings)
    val alternativeKeybinds = mutableListOf<AlternativeKeybind>()

    fun isActivated(): Boolean =
        mainKeybind.isActivated() || alternativeKeybinds.any { it.isActivated() }

    fun isPressing(): Boolean =
        mainKeybind.isPressing() || alternativeKeybinds.any { it.isPressing() }

    override val isModified
        get() = alternativeKeybinds.isNotEmpty() || mainKeybind.isModified

    override fun resetToDefault() {
        alternativeKeybinds.clear()
        mainKeybind.resetToDefault()
    }

    override fun toJsonElement() = JsonObject(mutableMapOf<String, JsonElement>().apply {
        if (mainKeybind.isModified) {
            this["main"] = mainKeybind.toJsonElement()
        }
        if (alternativeKeybinds.isNotEmpty()) {
            this["alternatives"] = buildJsonArray {
                alternativeKeybinds.forEach {
                    add(it.toJsonElement())
                }
            }
        }
    })

    override fun fromJsonObject(obj: JsonObject) {
        try {
            obj["main"]?.let { mainKeybind.fromJsonElement(it) }
            obj["alternatives"]?.jsonArray?.forEach {
                val alt = AlternativeKeybind(mainKeybind).apply { fromJsonElement(it) }
                if (alt.isModified) alternativeKeybinds.add(alt)
            }
        } catch (e: Exception) {
            Log.warn("Failed to read JSON element '${obj["alternatives"]}' as a JSON array")
        }
    }


}