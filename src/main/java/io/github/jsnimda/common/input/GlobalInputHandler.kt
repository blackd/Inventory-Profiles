package io.github.jsnimda.common.input

import io.github.jsnimda.common.IInputHandler
import io.github.jsnimda.common.gui.debug.DebugInfos
import io.github.jsnimda.common.vanilla.Vanilla
import org.lwjgl.glfw.GLFW.*

object GlobalInputHandler {

  val pressedKeys = mutableSetOf<Int>()
  var previousPressedKeys = pressedKeys.toSet()
    private set
  var lastKey = -1
    private set
  var lastAction = -1 // only GLFW_PRESS or GLFW_RELEASE
    private set

  fun isActivated(keyCodes: List<Int>, settings: KeybindSettings): Boolean {
    if (keyCodes.isEmpty()) return false
    if (!settings.activateOn.isValid(lastAction)) return false
    if (!settings.context.isValid(Vanilla.screen())) return false
    // checked: context, activateOn
    // ref: malilib KeybindMulti.updateIsPressed()
    val validateKeys = if (lastAction == GLFW_PRESS) pressedKeys else previousPressedKeys
    return settings.validates(validateKeys, keyCodes)
  }

  private fun onKey(key: Int, action: Int): Boolean { // action: only GLFW_PRESS or GLFW_RELEASE
    val isPress = action == GLFW_PRESS
    if (isPress == pressedKeys.contains(key)) // (PRESS && contain) || (RELEASE && !contain)
      return false // should err / cancelled by other mod
    previousPressedKeys = pressedKeys.toSet()
    if (isPress)
      pressedKeys.add(key)
    else
      pressedKeys.remove(key)
    lastKey = key
    lastAction = action
    return onInput()
  }

  private fun onInput(): Boolean {
    if (currentAssigningKeybind != null) {
      handleAssignKeybind()
      return true
    }
    registered.forEach { it.onInput(lastKey, lastAction) }
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
    val pressedKeys: List<Int> = currentAssigningKeybind
      ?.run { settings.modifierKey.handleKeys(pressedKeys.toList()) }
      ?: pressedKeys.toList()
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

  // ============
  // Vanilla key hook handler
  // ============
  fun onKey(key: Int, scanCode: Int, action: Int, modifiers: Int): Boolean {
    DebugInfos.onKey(key, scanCode, action, modifiers)
    return when (action) {
      GLFW_PRESS, GLFW_RELEASE -> onKey(key, action)
      else -> false
    }
  }

  fun onMouseButton(button: Int, action: Int, mods: Int): Boolean {
    DebugInfos.onMouseButton(button, action, mods)
    return when (action) {
      GLFW_PRESS, GLFW_RELEASE -> onKey(button - 100, action)
      else -> false
    }
  }

  // ============
  // api
  // ============
  private val registered: MutableSet<IInputHandler> = mutableSetOf()

  fun register(inputHandler: IInputHandler): Boolean =
    registered.add(inputHandler)

  fun unregister(inputHandler: IInputHandler): Boolean =
    registered.remove(inputHandler)
}