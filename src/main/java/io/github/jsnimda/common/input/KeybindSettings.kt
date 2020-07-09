package io.github.jsnimda.common.input

import io.github.jsnimda.common.config.IConfigElementResettableMultiple
import io.github.jsnimda.common.config.options.ConfigBoolean
import io.github.jsnimda.common.config.options.ConfigEnum
import io.github.jsnimda.common.input.KeybindSettings.Context.*
import io.github.jsnimda.common.input.KeybindSettings.KeyAction.PRESS
import io.github.jsnimda.common.input.KeybindSettings.ModifierKey.NORMAL
import io.github.jsnimda.common.util.containsAny
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.Screen
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

data class KeybindSettings(
  val context: Context,
  val activateOn: KeyAction,
  val allowExtraKeys: Boolean,
  val orderSensitive: Boolean,
  val modifierKey: ModifierKey = NORMAL,
) {
  companion object {
    val INGAME_DEFAULT = KeybindSettings(INGAME, PRESS, allowExtraKeys = false, orderSensitive = true)
    val GUI_DEFAULT = KeybindSettings(GUI, PRESS, allowExtraKeys = false, orderSensitive = true)
    val ANY_DEFAULT = KeybindSettings(ANY, PRESS, allowExtraKeys = false, orderSensitive = true)

    val GUI_EXTRA = GUI_DEFAULT.copy(allowExtraKeys = true)
  }

  enum class KeyAction {
    PRESS, RELEASE, BOTH;

    fun isValid(action: Int) = when (this) {
      PRESS -> action == GLFW_PRESS
      RELEASE -> action == GLFW_RELEASE
      BOTH -> action == GLFW_PRESS || action == GLFW_RELEASE
    }

    override fun toString(): String {
      return I18n.translate("inventoryprofiles.common.enum.key_action." + name.toLowerCase())
    }
  }

  enum class Context {
    INGAME, GUI, ANY;

    fun isValid(currentScreen: Screen?) = when (this) {
      INGAME -> currentScreen == null
      GUI -> currentScreen != null
      ANY -> true
    }

    override fun toString(): String {
      return I18n.translate("inventoryprofiles.common.enum.context." + name.toLowerCase())
    }
  }

  enum class ModifierKey {
    DIFFERENTIATE, NORMAL;

    fun handleKeys(keys: List<Int>) = when (this) {
      DIFFERENTIATE -> keys
      NORMAL -> if (KeyCodes.modifiers.containsAny(keys))
        keys.map { KeyCodes.getModifierKeyCode(it) }.distinct() else keys
    }

    override fun toString(): String {
      return I18n.translate("inventoryprofiles.common.enum.modifier_key." + name.toLowerCase())
    }
  }

  // validate boolean values
  fun validates(pressedKeys: Set<Int>, registeredKeys: List<Int>, justPressed: Boolean = true): Boolean {
    // move from GlobalInputHandler.isActivated()
    if (registeredKeys.isEmpty()) return false
    return rawValidates(
      modifierKey.handleKeys(pressedKeys.toList()),
      modifierKey.handleKeys(registeredKeys),
      justPressed
    )
  }

  private fun rawValidates(pressedKeys: List<Int>, registeredKeys: List<Int>, justPressed: Boolean): Boolean {
    return pressedKeys.size >= registeredKeys.size && (allowExtraKeys || pressedKeys.size == registeredKeys.size) &&
        if (orderSensitive) {
          (if (justPressed) pressedKeys else pressedKeys.dropLastWhile { it != registeredKeys.last() })
            .takeLast(registeredKeys.size) == registeredKeys
        } else { // order insensitive
          (!justPressed || registeredKeys.contains(pressedKeys.last()))
              && pressedKeys.containsAll(registeredKeys)
        }
  }
}

// ============
// ConfigKeybindSettings
// ============

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class ConfigKeybindSettings(
  val defaultSettings: KeybindSettings,
  settings: KeybindSettings
) : IConfigElementResettableMultiple {
  val context = ConfigEnum(defaultSettings.context)
    .apply { key = "context"; value = settings.context }
  val activateOn = ConfigEnum(defaultSettings.activateOn)
    .apply { key = "activate_on"; value = settings.activateOn }
  val allowExtraKeys = ConfigBoolean(defaultSettings.allowExtraKeys)
    .apply { key = "allow_extra_keys"; value = settings.allowExtraKeys }
  val orderSensitive = ConfigBoolean(defaultSettings.orderSensitive)
    .apply { key = "order_sensitive"; value = settings.orderSensitive }
  val modifierKey = ConfigEnum(defaultSettings.modifierKey)
    .apply { key = "modifier_key"; value = settings.modifierKey }

  val settings: KeybindSettings
    get() = KeybindSettings(
      context.value,
      activateOn.value,
      allowExtraKeys.booleanValue,
      orderSensitive.booleanValue,
      modifierKey.value,
    )

  override fun getConfigOptionMap() = getConfigOptionMapFromList()
  override fun getConfigOptionList() =
    listOf(activateOn, context, allowExtraKeys, orderSensitive, modifierKey) // gui display order
}