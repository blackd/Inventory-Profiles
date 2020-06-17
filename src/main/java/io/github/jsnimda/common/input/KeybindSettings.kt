package io.github.jsnimda.common.input

import io.github.jsnimda.common.input.KeybindSettings.Context.*
import io.github.jsnimda.common.input.KeybindSettings.KeyAction.PRESS
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.Screen
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

data class KeybindSettings(
    @JvmField val context: Context,
    @JvmField val activateOn: KeyAction,
    @JvmField val allowExtraKeys: Boolean,
    @JvmField val orderSensitive: Boolean
) {
  companion object {
    @JvmField val INGAME_DEFAULT = KeybindSettings(INGAME, PRESS, allowExtraKeys = false, orderSensitive = true)
    @JvmField val GUI_DEFAULT    = KeybindSettings(   GUI, PRESS, allowExtraKeys = false, orderSensitive = true)
    @JvmField val ANY_DEFAULT    = KeybindSettings(   ANY, PRESS, allowExtraKeys = false, orderSensitive = true)
  }

  enum class KeyAction {
    PRESS, RELEASE, BOTH;

    fun isValid(action: Int): Boolean = when (this) {
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

}