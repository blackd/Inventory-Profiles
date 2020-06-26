@file:Suppress("unused")

package io.github.jsnimda.common.config.builder

import io.github.jsnimda.common.config.CategorizedMultiConfig
import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.options.*
import io.github.jsnimda.common.config.toMultiConfig
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.common.input.KeybindSettings.Companion.INGAME_DEFAULT
import io.github.jsnimda.common.util.PropertyNameChecker

// ============
// api
// ============

// bool, int, hotkey, hotkeyedBool, enum
fun ConfigDeclaration.bool(defaultValue: Boolean) =
  ConfigBoolean(defaultValue).addTo(this)

fun ConfigDeclaration.int(defaultValue: Int, minValue: Int, maxValue: Int) =
  ConfigInteger(defaultValue, minValue, maxValue).addTo(this)

fun ConfigDeclaration.hotkey(defaultValue: String, defaultSettings: KeybindSettings = INGAME_DEFAULT) =
  ConfigHotkey(defaultValue, defaultSettings).addTo(this)

fun ConfigDeclaration.hotkeyedBool(defaultValue: Boolean) =
  ConfigHotkeyedBoolean(defaultValue).addTo(this)

fun <T : Enum<T>> ConfigDeclaration.enum(defaultValue: T) =
  ConfigEnum(defaultValue).addTo(this)

fun ConfigDeclaration.string(defaultValue: String) =
  ConfigString(defaultValue).addTo(this)

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
  return ConfigOptionDelegateProvider(this, declaration)
}

class ConfigOptionDelegateProvider<T : IConfigOption>(
  value: T,
  val declaration: ConfigDeclaration
) : PropertyNameChecker<T>(value) {
  override fun checkName(name: String) {
    value.key = name.toLowerCase()
  }
}

@Suppress("FunctionName")
fun <T : IConfigOption> ConfigOptionDelegateProvider<T>.CATEGORY(name: String) =
  this.also { declaration.builder.CATEGORY(name) }

// ============
// extensions
// ============

fun List<ConfigDeclaration>.toMultiConfig() = toMultiConfigList().toMultiConfig()
fun List<ConfigDeclaration>.toMultiConfigList(): List<CategorizedMultiConfig> =
  this.map { it.builder.innerConfig }
