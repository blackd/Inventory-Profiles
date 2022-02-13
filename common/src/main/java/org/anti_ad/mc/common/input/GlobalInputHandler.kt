package org.anti_ad.mc.common.input

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.ifFalse
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.gui.debug.DebugInfos
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.lwjgl.glfw.GLFW.*

object GlobalInputHandler {

    val pressedKeys = mutableSetOf<Int>()
    var previousPressedKeys = pressedKeys.toSet()
        private set
    var lastKey = -1
        private set
    var lastAction = -1 // only GLFW_PRESS or GLFW_RELEASE
        private set

    fun isWaitingForRelease(key: Int): Boolean {
        return pressedKeys.contains(key)
    }

    fun isActivated(keyCodes: List<Int>,
                    settings: KeybindSettings): Boolean {
        if (keyCodes.isEmpty()) return false
        if (!settings.activateOn.isValid(lastAction)) return false
        if (!VanillaUtil.isValidScreen(settings.context)) return false
        //if (!settings.context.isValid(Vanilla.screen())) return false
        // checked: context, activateOn
        // ref: malilib KeybindMulti.updateIsPressed()
        val validateKeys = if (lastAction == GLFW_PRESS) pressedKeys else previousPressedKeys
        return settings.validates(validateKeys,
                                  keyCodes)
    }

    fun isPressing(keyCodes: List<Int>,
                   settings: KeybindSettings): Boolean {
        if (keyCodes.isEmpty()) return false
        if (!VanillaUtil.isValidScreen(settings.context)) return false
        //if (!settings.context.isValid(Vanilla.screen())) return false
        return settings.validates(pressedKeys,
                                  keyCodes,
                                  justPressed = false)
    }

    private fun onKey(key: Int,
                      action: Int): Boolean { // action: only GLFW_PRESS or GLFW_RELEASE

        val isPress = action == GLFW_PRESS
        if (isPress == pressedKeys.contains(key)) { // (PRESS && contain) || (RELEASE && !contain)
            return false // should err / cancelled by other mod
        }

        previousPressedKeys = pressedKeys.toSet()
        if (isPress) {
            pressedKeys.add(key)
        } else {
            pressedKeys.remove(key)
        }
        lastKey = key
        lastAction = action
        return onInput()
    }

    private fun onInput(): Boolean {
        if (currentAssigningKeybind != null) {
            handleAssignKeybind()
            return true
        }
        if (registeredCancellable.any { it.onInput(lastKey,
                                                   lastAction) }) {
            return true
        }
        registered.forEach {
            it.onInput(lastKey,
                       lastAction)
        }
        return false
    }

    // ============
    // Assign keybind
    // ============
    var currentAssigningKeybind: IKeybind? = null
        set(value) {
            pressedFirstKey = false
            ignoreLeftClick = true // left down -> ignore, left up -> set false
            field = value
        }
    private var pressedFirstKey = false
    private var ignoreLeftClick = false // fix forge version while compatible with fabric version

    private fun handleAssignKeybind() {
        val pressedKeys: List<Int> = currentAssigningKeybind?.run { settings.modifierKey.handleKeys(pressedKeys.toList()) } ?: pressedKeys.toList()
        if (lastAction == GLFW_PRESS) {
            if (lastKey == KeyCodes.MOUSE_BUTTON_1 && ignoreLeftClick) { // GLFW_MOUSE_BUTTON_1 - 100
                return
            }
            pressedFirstKey = true
            if (lastKey == GLFW_KEY_ESCAPE) { // clear keybind
                currentAssigningKeybind?.keyCodes = listOf()
                currentAssigningKeybind = null
            } else {
                currentAssigningKeybind?.keyCodes = pressedKeys
            }
        } else { // lastAction == GLFW_RELEASE
            if (lastKey == KeyCodes.MOUSE_BUTTON_1) {
                ignoreLeftClick = false
            }
            if (pressedKeys.isEmpty() && pressedFirstKey) {
                currentAssigningKeybind = null // all key released, assignment end
            }
        }
    }

    fun isKeyDown(keyCode: Int, window: Long): Boolean {
        var keyCode = keyCode
        if (keyCode >= 0) {
            return glfwGetKey(window, keyCode) == GLFW_PRESS
        }
        keyCode += 100
        return keyCode >= 0 && glfwGetMouseButton(window, keyCode) == GLFW_PRESS
    }
    // ============
    // Vanilla key hook handler
    // ============
    fun onKey(key: Int,
              scanCode: Int,
              action: Int,
              modifiers: Int,
              checkPressing: Boolean,
              handle: Long): Boolean {
        DebugInfos.onKey(key,
                         scanCode,
                         action,
                         modifiers)
        if (handle != 0L && checkPressing && pressedKeys.isNotEmpty()) {
            val pressed = pressedKeys.toSet()
            pressedKeys.clear()
            pressed.forEach {
                isKeyDown(it, handle).ifTrue {
                    pressedKeys.add(it)
                }
            }
        }
        return when (action) {
            GLFW_PRESS, GLFW_RELEASE -> onKey(key,
                                              action)
            else -> false
        }
    }

    fun onMouseButton(button: Int,
                      action: Int,
                      mods: Int): Boolean {
        DebugInfos.onMouseButton(button,
                                 action,
                                 mods)
        return when (action) {
            GLFW_PRESS, GLFW_RELEASE -> onKey(button - 100,
                                              action)
            else -> false
        }
    }

    // ============
    // api
    // ============
    private val registered: MutableSet<IInputHandler> = mutableSetOf()
    private val registeredCancellable: MutableSet<IInputHandler> = mutableSetOf() // screen only

    fun register(inputHandler: IInputHandler): Boolean =
        registered.add(inputHandler)

    fun unregister(inputHandler: IInputHandler): Boolean =
        registered.remove(inputHandler)

    fun registerCancellable(inputHandler: IInputHandler): Boolean =
        registeredCancellable.add(inputHandler)

    fun unregisterCancellable(inputHandler: IInputHandler): Boolean =
        registeredCancellable.remove(inputHandler)
}