@file:Suppress("unused")

package org.anti_ad.mc.common.config.builder

import org.anti_ad.mc.common.config.CategorizedMultiConfig
import org.anti_ad.mc.common.config.IConfigOption
import org.anti_ad.mc.common.config.toMultiConfig
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.KeybindSettings.Companion.INGAME_DEFAULT
import org.anti_ad.mc.common.config.options.*
import org.anti_ad.mc.common.util.ByPropertyName

// ============
// api
// ============

// bool, int, hotkey, hotkeyedBool, enum
fun ConfigDeclaration.bool(defaultValue: Boolean) =
    ConfigBoolean(defaultValue).addTo(this)

fun ConfigDeclaration.int(defaultValue: Int,
                          minValue: Int,
                          maxValue: Int) =
    ConfigInteger(defaultValue,
                  minValue,
                  maxValue).addTo(this)

fun ConfigDeclaration.hotkey(defaultValue: String,
                             defaultSettings: KeybindSettings = INGAME_DEFAULT) =
    ConfigHotkey(defaultValue,
                 defaultSettings).addTo(this)

fun ConfigDeclaration.hotkeyedBool(defaultValue: Boolean) =
    ConfigHotkeyedBoolean(defaultValue).addTo(this)

fun <T : Enum<T>> ConfigDeclaration.enum(defaultValue: T) =
    ConfigEnum(defaultValue).addTo(this)

fun ConfigDeclaration.string(defaultValue: String) =
    ConfigString(defaultValue).addTo(this)

fun ConfigDeclaration.button(info: ConfigButtonInfo) =
    ConfigButton(info).addTo(this)

// createBuilder()
fun ConfigDeclaration.createBuilder() = ConfigDeclarationBuilder().apply {
    innerConfig.key = this@createBuilder.javaClass.simpleName
}

interface ConfigDeclaration {
    val builder: ConfigDeclarationBuilder
}

// .CATEGORY()
@Suppress("FunctionName")
fun ConfigDeclarationBuilder.CATEGORY(name: String) =
    this.also { innerConfig.addCategory(name) }

class ConfigDeclarationBuilder {
    val innerConfig = CategorizedMultiConfig()
}

// ============
// internal
// ============

fun <T : IConfigOption> T.addTo(declaration: ConfigDeclaration): ConfigOptionDelegateProvider<T> {
    declaration.builder.innerConfig.addConfigOption(this)
    return ConfigOptionDelegateProvider(this,
                                        declaration)
}

class ConfigOptionDelegateProvider<T : IConfigOption>(
    value: T,
    val declaration: ConfigDeclaration
) : ByPropertyName<T>({ name ->
                          value.apply { key = name.toLowerCase() }
                      })

@Suppress("FunctionName")
fun <T : IConfigOption> ConfigOptionDelegateProvider<T>.CATEGORY(name: String) =
    this.also { declaration.builder.CATEGORY(name) }

// ============
// extensions
// ============

fun List<ConfigDeclaration>.toMultiConfig() = toMultiConfigList().toMultiConfig()
fun List<ConfigDeclaration>.toMultiConfigList(): List<CategorizedMultiConfig> =
    this.map { it.builder.innerConfig }
