package io.github.jsnimda.common.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import io.github.jsnimda.common.gui.DebugScreen.DebugInfos;
import io.github.jsnimda.common.input.KeybindSettings.Context;
import io.github.jsnimda.common.input.KeybindSettings.KeyAction;
import net.minecraft.client.MinecraftClient;

public class GlobalInputHandler {

  private static final GlobalInputHandler INSTANCE = new GlobalInputHandler();
  public static GlobalInputHandler getInstance() {
    return INSTANCE;
  }

  public final List<Integer> pressingKeys = new ArrayList<>();
  public List<Integer> beforePressingKeys = new ArrayList<>();
  public int lastKey = -1;
  public int lastAction = -1;

  private GlobalInputHandler() {

  }

  public boolean isActivated(List<Integer> keyCodes, KeybindSettings settings) {
    if (settings.activateOn == KeyAction.PRESS && lastAction == GLFW.GLFW_RELEASE)
      return false;
    if (settings.activateOn == KeyAction.RELEASE && lastAction == GLFW.GLFW_PRESS)
      return false;
    if (settings.context != Context.ANY && MinecraftClient.getInstance().currentScreen != null)
      return false;
    if (settings.context == Context.GUI && MinecraftClient.getInstance().currentScreen == null)
      return false;
    // checked: context, activateOn
    if (keyCodes.isEmpty())
      return false;
    // ref: malilib KeybindMulti.updateIsPressed()
    List<Integer> pressedKeys = lastAction == GLFW.GLFW_PRESS ? pressingKeys : beforePressingKeys;
    if (pressedKeys.size() >= keyCodes.size() && (settings.allowExtraKeys || pressedKeys.size() == keyCodes.size())) {
      if (settings.orderSensitive) {
        for (int i = 0; i < keyCodes.size(); i++) {
          if (keyCodes.get(keyCodes.size() - 1 - i) != pressedKeys.get(pressedKeys.size() - 1 - i)) {
            return false;
          }
        }
        return true;
      } else { // order insensitive
        return keyCodes.contains(lastKey) && pressedKeys.containsAll(keyCodes);
      }
    } else {
      return false;
    }
  }

  public boolean onKeyPress(int key) {
    if (pressingKeys.contains(key)) return false; // should err
    beforePressingKeys = new ArrayList<>(pressingKeys);
    pressingKeys.add(key);
    lastKey = key;
    lastAction = GLFW.GLFW_PRESS;
    return false;
  }

  public boolean onKeyRelease(int key) {
    if (!pressingKeys.contains(key)) return false; // should err
    beforePressingKeys = new ArrayList<>(pressingKeys);
    pressingKeys.remove((Object)key);
    lastKey = key;
    lastAction = GLFW.GLFW_RELEASE;
    return false;
  }


  public boolean onKey(int key, int scanCode, int action, int modifiers) {
    DebugInfos.onKey(key, scanCode, action, modifiers);
    if (action == GLFW.GLFW_PRESS) {
      return onKeyPress(key);
    }
    if (action == GLFW.GLFW_RELEASE) {
      return onKeyRelease(key);
    }
    return false;
  }
  public boolean onMouseButton(int button, int action, int mods) {
    DebugInfos.onMouseButton(button, action, mods);
    if (action == GLFW.GLFW_PRESS) {
      return onKeyPress(button - 100);
    }
    if (action == GLFW.GLFW_RELEASE) {
      return onKeyRelease(button - 100);
    }
    return false;
  }


}