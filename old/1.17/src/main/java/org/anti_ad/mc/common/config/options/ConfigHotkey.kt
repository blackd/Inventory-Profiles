package org.anti_ad.mc.common.config.options

import com.google.gson.JsonObject
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.config.ConfigOptionBase
import org.anti_ad.mc.common.config.IConfigElementObject
import org.anti_ad.mc.common.config.toJsonArray
import org.anti_ad.mc.common.input.AlternativeKeybind
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.MainKeybind

class ConfigHotkey(defaultStorageString: String,
                   defaultSettings: KeybindSettings) :
    ConfigOptionBase(), IConfigElementObject {
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

    override fun toJsonElement() = JsonObject().apply {
        if (mainKeybind.isModified)
            this.add("main",
                     mainKeybind.toJsonElement())
        if (alternativeKeybinds.isNotEmpty())
            this.add("alternatives",
                     alternativeKeybinds.toJsonArray())
    }

    override fun fromJsonObject(obj: JsonObject) {
        try {
            obj["main"]
                ?.let { mainKeybind.fromJsonElement(it) }
            obj["alternatives"]
                ?.asJsonArray?.forEach {
                    val alt = AlternativeKeybind(mainKeybind).apply { fromJsonElement(it) }
                    if (alt.isModified) alternativeKeybinds.add(alt)
                }
        } catch (e: Exception) {
            Log.warn("Failed to read JSON element '${obj["alternatives"]}' as a JSON array")
        }
    }

}