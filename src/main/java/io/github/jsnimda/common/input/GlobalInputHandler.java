package io.github.jsnimda.common.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import io.github.jsnimda.common.gui.DebugScreen.DebugInfos;

public class GlobalInputHandler {

  private static final GlobalInputHandler INSTANCE = new GlobalInputHandler();
  public static GlobalInputHandler getInstance() {
    return INSTANCE;
  }

  public final List<Integer> pressingKeys = new ArrayList<>();

  private GlobalInputHandler() {

  }

  public boolean onKeyPress(int key) {
    if (!pressingKeys.contains(key)) {
      pressingKeys.add(key);
    }
    return false;
  }

  public boolean onKeyRelease(int key) {
    pressingKeys.remove((Object)key);
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