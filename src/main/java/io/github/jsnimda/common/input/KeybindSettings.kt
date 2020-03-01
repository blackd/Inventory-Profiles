package io.github.jsnimda.common.input

import net.minecraft.client.resource.language.I18n
import io.github.jsnimda.common.input.KeybindSettings.Context.*;
import io.github.jsnimda.common.input.KeybindSettings.KeyAction.*;

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

    override fun toString(): String {
      return I18n.translate("inventoryprofiles.common.enum.key_action." + name.toLowerCase())
    }
  }

  enum class Context {
    INGAME, GUI, ANY;

    override fun toString(): String {
      return I18n.translate("inventoryprofiles.common.enum.context." + name.toLowerCase())
    }
  }

}