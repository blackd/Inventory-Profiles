package io.github.jsnimda.inventoryprofiles.eventhandler;

import java.util.List;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;

/**
 * KeybindProvider
 */
public class KeybindProvider implements IKeybindProvider {

  public static final KeybindProvider INSTANCE = new KeybindProvider();

  @Override
  public void addKeysToMap(IKeybindManager manager) {
    addKeysToMap(manager, Generic.HOTKEY_LIST, Tweaks.LIST);
  }

  private final void addKeysToMap(IKeybindManager manager, List<?>... lists) {
    for (List<?> list : lists) {
      list.forEach(x -> {
        if (x instanceof IHotkey) {
          manager.addKeybindToMap(((IHotkey)x).getKeybind());
        }
      });
    }
  }

  @Override
  public void addHotkeys(IKeybindManager manager) {
    manager.addHotkeysForCategory(ModInfo.MOD_NAME, "inventoryprofiles.hotkeys.category.generic_hotkeys",
        Generic.HOTKEY_LIST);
    manager.addHotkeysForCategory(ModInfo.MOD_NAME, "inventoryprofiles.hotkeys.category.tweak_toggle_hotkeys",
        Tweaks.LIST);
  }

}