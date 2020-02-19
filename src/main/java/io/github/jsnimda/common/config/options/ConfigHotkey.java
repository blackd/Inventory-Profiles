package io.github.jsnimda.common.config.options;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.input.Keybind;
import io.github.jsnimda.common.input.KeybindSettings;

public class ConfigHotkey extends ConfigOptionBase {

  private final Keybind mainKeybind;
  private List<Keybind> alternativeKeybinds = new ArrayList<>();

  public ConfigHotkey(String defaultStorageString, KeybindSettings defaultSettings) {
    mainKeybind = new Keybind(defaultStorageString, defaultSettings);
  }

  @Override
  public boolean isModified() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void resetToDefault() {
    // TODO Auto-generated method stub

  }

  @Override
  public JsonElement toJsonElement() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void fromJsonElement(JsonElement element) {
    // TODO Auto-generated method stub

  }

  
}