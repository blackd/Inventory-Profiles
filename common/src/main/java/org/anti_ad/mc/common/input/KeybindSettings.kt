/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.input

import org.anti_ad.mc.common.config.IConfigElementResettableMultiple
import org.anti_ad.mc.common.config.options.ConfigBoolean
import org.anti_ad.mc.common.config.options.ConfigEnum
import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.input.KeybindSettings.Context.*
import org.anti_ad.mc.common.input.KeybindSettings.KeyAction.PRESS
import org.anti_ad.mc.common.input.KeybindSettings.ModifierKey.NORMAL
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import java.util.*

data class KeybindSettings(val context: Context,
                           val activateOn: KeyAction,
                           val allowExtraKeys: Boolean,
                           val orderSensitive: Boolean,
                           val modifierKey: ModifierKey = NORMAL) {
    companion object {
        val INGAME_DEFAULT = KeybindSettings(INGAME,
                                             PRESS,
                                             allowExtraKeys = false,
                                             orderSensitive = true)
        val GUI_DEFAULT = KeybindSettings(GUI,
                                          PRESS,
                                          allowExtraKeys = false,
                                          orderSensitive = true)
        val ANY_DEFAULT = KeybindSettings(ANY,
                                          PRESS,
                                          allowExtraKeys = false,
                                          orderSensitive = true)

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
            return I18n.translate("inventoryprofiles.common.enum.key_action." + name.lowercase())
        }
    }

    enum class Context {
        INGAME, GUI, ANY;

        override fun toString(): String {
            return I18n.translate("inventoryprofiles.common.enum.context." + name.lowercase())
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
            return I18n.translate("inventoryprofiles.common.enum.modifier_key." + name.lowercase())
        }
    }

    // validate boolean values
    fun validates(pressedKeys: Set<Int>,
                  registeredKeys: List<Int>,
                  justPressed: Boolean = true): Boolean {
        // move from GlobalInputHandler.isActivated()
        if (registeredKeys.isEmpty()) return false
        return rawValidates(
            modifierKey.handleKeys(pressedKeys.toList()),
            modifierKey.handleKeys(registeredKeys),
            justPressed
        )
    }

    private fun rawValidates(pressedKeys: List<Int>,
                             registeredKeys: List<Int>,
                             justPressed: Boolean): Boolean {
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

@Suppress("CanBeParameter",
          "MemberVisibilityCanBePrivate")
class ConfigKeybindSettings(val defaultSettings: KeybindSettings,
                            settings: KeybindSettings) : IConfigElementResettableMultiple {

    val context = ConfigEnum(defaultSettings.context)
        .apply { key = "context"; value = settings.context }
    val activateOn = ConfigEnum(defaultSettings.activateOn).apply {
        key = "activate_on"; value = settings.activateOn
    }

    val allowExtraKeys = ConfigBoolean(defaultSettings.allowExtraKeys).apply {
        key = "allow_extra_keys"; value = settings.allowExtraKeys
    }

    val orderSensitive = ConfigBoolean(defaultSettings.orderSensitive).apply {
        key = "order_sensitive"; value = settings.orderSensitive
    }

    val modifierKey = ConfigEnum(defaultSettings.modifierKey).apply {
        key = "modifier_key"; value = settings.modifierKey
    }

    val settings: KeybindSettings
        get() = KeybindSettings(context.value,
                                activateOn.value,
                                allowExtraKeys.booleanValue,
                                orderSensitive.booleanValue,
                                modifierKey.value)

    override fun getConfigOptionMap() = getConfigOptionMapFromList()
    override fun getConfigOptionList() =
        listOf(activateOn,
               context,
               allowExtraKeys,
               orderSensitive,
               modifierKey) // gui display order
}
