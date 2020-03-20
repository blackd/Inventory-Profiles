@file:Suppress("unused")

package io.github.jsnimda.common.config.builder

import com.google.common.base.CaseFormat
import io.github.jsnimda.common.ReadOnlyPropertyProvider
import io.github.jsnimda.common.cachedReadOnlyProperty
import io.github.jsnimda.common.config.CategorizedConfigOptions
import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.options.*
import io.github.jsnimda.common.config.toConfigs
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.common.input.KeybindSettings.Companion.INGAME_DEFAULT
import io.github.jsnimda.common.readOnlyProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ConfigSingleton

object RegisteredConfigSingleton {
  internal val map = linkedMapOf<Class<ConfigSingleton>, Pair<ConfigSingleton, CategorizedConfigOptions>>()
  val objects: List<ConfigSingleton>
    get() = map.values.map { it.first }
  val configsList: List<CategorizedConfigOptions>
    get() = map.values.map { it.second }

  operator fun get(singleton: ConfigSingleton): CategorizedConfigOptions =
    map.getOrPut(
      singleton.javaClass,
      { singleton to CategorizedConfigOptions().apply { this.key = singleton.javaClass.simpleName } }
    ).second
}

val ConfigSingleton.configs: CategorizedConfigOptions
  get() = RegisteredConfigSingleton[this]

//val Class<out ConfigSingleton>.configs: CategorizedConfigOptions
//  get() = RegisteredConfigSingleton.map[this]?.second ?: throw NoSuchElementException() //warn: ExceptionInInitializerError might occur

fun List<ConfigSingleton>.toConfigs() =
  this.map { it.configs }.toConfigs()

//region Builder Syntax

val <T : ConfigSingleton> T.builder
  get() = ReadOnlyPropertyWithReceiver(this, cachedReadOnlyProperty { thisRef: T, _ -> thisRef.configs })

class ReadOnlyPropertyWithReceiver<R, T>(val receiver: R, delegate: ReadOnlyProperty<R, T>) :
  ReadOnlyProperty<R, T> by delegate

fun <T : ConfigSingleton> T.categoryStart(nameKey: String) { // runtime type
  this.configs.addCategory(nameKey)
}

@Suppress("FunctionName")
fun <T : ConfigSingleton> ReadOnlyPropertyWithReceiver<T, CategorizedConfigOptions>.CATEGORY(nameKey: String) =
  this.also { this.receiver.configs.addCategory(nameKey) }

@Suppress("FunctionName")
fun <T : IConfigOption> ConfigOptionPropertyProvider<T>.CATEGORY(nameKey: String) =
  this.also { thisRefTodoList += { thisRef -> thisRef.configs.addCategory(nameKey) } }

//endregion

//region Config Options

fun bool(defaultValue: Boolean) =
  ConfigBoolean(defaultValue).toProperty()

fun int(defaultValue: Int, minValue: Int, maxValue: Int) =
  ConfigInteger(defaultValue, minValue, maxValue).toProperty()

fun hotkey(defaultValue: String, defaultSettings: KeybindSettings = INGAME_DEFAULT) =
  ConfigHotkey(defaultValue, defaultSettings).toProperty()

fun hotkeyedBool(defaultValue: Boolean) =
  ConfigHotkeyedBoolean(defaultValue).toProperty()

fun <T : Enum<T>> enum(defaultValue: T) =
  ConfigEnum(defaultValue).toProperty()

private fun <T : IConfigOption> T.toProperty() =
  ConfigOptionPropertyProvider(this)

class ConfigOptionPropertyProvider<T : IConfigOption> internal constructor(private val configOption: T) :
  ReadOnlyPropertyProvider<ConfigSingleton, T> {
  internal val thisRefTodoList = mutableListOf<(thisRef: ConfigSingleton) -> Unit>()
  override fun provideDelegate(thisRef: ConfigSingleton, prop: KProperty<*>) = run {
    configOption.key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, prop.name)
    thisRef.configs.addConfigOption(configOption)
    thisRefTodoList.forEach { it(thisRef) }
    readOnlyProperty { _: ConfigSingleton, _ -> configOption }
  }
}

//endregion
