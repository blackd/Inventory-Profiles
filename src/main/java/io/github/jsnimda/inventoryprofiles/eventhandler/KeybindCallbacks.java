package io.github.jsnimda.inventoryprofiles.eventhandler;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.gui.GuiConfigs;
import io.github.jsnimda.inventoryprofiles.sorter.SorterEventPort;
import net.minecraft.client.MinecraftClient;

/**
 * KeybindCallbacks
 */
public class KeybindCallbacks {

  public static void init(MinecraftClient mc){
    KeyCallbackHotkeysGeneric kc = new KeyCallbackHotkeysGeneric();
    Generic.HOTKEY_LIST.forEach(x->{
      x.getKeybind().setCallback(kc);
    });
  }
  static class KeyCallbackHotkeysGeneric implements IHotkeyCallback {

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
      if (key == Generic.OPEN_CONFIG_GUI.getKeybind()) {
        GuiBase.openGui(new GuiConfigs());
        return true;
      }

      if (SorterEventPort.shouldHandle(key)){
        return SorterEventPort.handleKey(action, key);
      }
      
      return false;
    }

  }
}