package io.github.jsnimda.common.input

import io.github.jsnimda.common.gui.DebugInfos
import io.github.jsnimda.common.vanilla.Vanilla
import org.lwjgl.glfw.GLFW.*

object GlobalInputHandler {

  val pressedKeys: MutableList<Int> = mutableListOf()
  var oldPressedKeys: List<Int> = pressedKeys.toList()
    private set
  var lastKey = -1
    private set
  var lastAction = -1
    private set

  fun isActivated(keyCodes: List<Int>, settings: KeybindSettings): Boolean {
    if (keyCodes.isEmpty()) return false
    if (!settings.activateOn.isValid(lastAction)) return false
    if (!settings.context.isValid(Vanilla.screen())) return false
    // checked: context, activateOn
    // ref: malilib KeybindMulti.updateIsPressed()
    val isPressElseRelease = lastAction == GLFW_PRESS
    val validateKeys = if (isPressElseRelease) pressedKeys else oldPressedKeys
    return validateKeys.size >= keyCodes.size && (settings.allowExtraKeys || validateKeys.size == keyCodes.size) &&
        if (settings.orderSensitive) {
          (keyCodes.asReversed() zip validateKeys.asReversed()).all { (a, b) -> a == b }
        } else { // order insensitive
          keyCodes.contains(lastKey) && validateKeys.containsAll(keyCodes)
        }
  }

  private fun onKey(key: Int, action: Int): Boolean {
    val isPressElseRelease = action == GLFW_PRESS
    if (isPressElseRelease == pressedKeys.contains(key)) // (PRESS && contain) || (RELEASE && !contain)
      return false // should err / cancelled by other mod
    oldPressedKeys = pressedKeys.toList()
    if (isPressElseRelease) pressedKeys.add(key) else pressedKeys.remove(key)
    lastKey = key
    lastAction = action
    return onInput()
  }

  private fun onInput(): Boolean {
    if (currentAssigningKeybind != null) {
      handleAssignKeybind()
      return true
    }
    registeredInputHandlers.forEach { it.onInput(lastKey, lastAction) }
    return false
  }

  // ============
  // Assign keybind
  // ============
  var currentAssigningKeybind: IKeybind? = null
    set(value) {
      pressedFirstKey = false
      field = value
    }
  private var pressedFirstKey = false

  private fun handleAssignKeybind() {
    if (lastAction == GLFW_PRESS) {
      pressedFirstKey = true
      if (lastKey == GLFW_KEY_ESCAPE) {
        currentAssigningKeybind?.keyCodes = listOf()
        currentAssigningKeybind = null
      } else {
        currentAssigningKeybind?.keyCodes = pressedKeys.toList()
      }
    } else if (lastAction == GLFW_RELEASE) {
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
  // Api
  // ============
  private val registeredInputHandlers: MutableSet<IInputHandler> = mutableSetOf()

  fun registerInputHandler(inputHandler: IInputHandler): Boolean = registeredInputHandlers.add(inputHandler)

  fun removeInputHandler(inputHandler: IInputHandler): Boolean = registeredInputHandlers.remove(inputHandler)

}