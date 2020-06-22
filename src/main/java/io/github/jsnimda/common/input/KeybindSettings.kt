package io.github.jsnimda.common.input

import io.github.jsnimda.common.input.KeybindSettings.Context.*
import io.github.jsnimda.common.input.KeybindSettings.KeyAction.PRESS
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.Screen
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

data class KeybindSettings(
  val context: Context,
  val activateOn: KeyAction,
  val allowExtraKeys: Boolean,
  val orderSensitive: Boolean
) {
  companion object {
    val INGAME_DEFAULT = KeybindSettings(INGAME, PRESS, allowExtraKeys = false, orderSensitive = true)
    val GUI_DEFAULT = KeybindSettings(GUI, PRESS, allowExtraKeys = false, orderSensitive = true)
    val ANY_DEFAULT = KeybindSettings(ANY, PRESS, allowExtraKeys = false, orderSensitive = true)
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

}