package io.github.jsnimda.common.input;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.jsnimda.common.config.IConfigElementResettable;

public class Keybind implements IConfigElementResettable {

  private final List<Integer> defaultKeyCodes;
  private List<Integer> keyCodes;
  private final KeybindSettings defaultSettings;
  private KeybindSettings settings;

  public Keybind(String defaultStorageString, KeybindSettings defaultSettings) {
    this.defaultKeyCodes = ImmutableList.copyOf(storageStringToKeyCodes(defaultStorageString));
    this.keyCodes = new ArrayList<>(defaultKeyCodes);
    this.defaultSettings = defaultSettings;
    this.settings = defaultSettings;
  }

  public KeybindSettings getSettings() {
    return settings;
  }

  public KeybindSettings getDefaultSettings() {
    return defaultSettings;
  }

  public void setSettings(KeybindSettings settings) {
    this.settings = settings;
  }

  public boolean isActivated() {
    return GlobalInputHandler.getInstance().isActivated(keyCodes, settings);
  }

  private static List<Integer> storageStringToKeyCodes(String storageString) {
    List<Integer> result = new ArrayList<>();
    for (String s : storageString.split(",")) {
      s = s.trim();
      if (s.isEmpty()) {
        continue;
      }
      result.add(KeyCodes.getKeyFromName(s));
    }
    return result;
  }
  private static String keyCodesToStorageString(List<Integer> keyCodes) {
    return keyCodes.stream().map(x -> KeyCodes.getKeyName(x)).collect(Collectors.joining(","));
  }

  public String toStorageString() {
    return keyCodesToStorageString(keyCodes);
  }

  public void fromStorageString(String storageString) {
    keyCodes = storageStringToKeyCodes(storageString);
  }

  public boolean isKeyCodesModified() {
    return !defaultKeyCodes.equals(keyCodes);
  }

  public boolean isSettingsModified() {
    return !defaultSettings.equals(settings);
  }

  public void resetKeyCodesToDefault() {
    keyCodes = new ArrayList<>(defaultKeyCodes);
  }

  public void resetSettingsToDefault() {
    settings = defaultSettings;
  }

  @Override
  public JsonElement toJsonElement() {
    JsonObject obj = new JsonObject();
    if (isKeyCodesModified()) {
      obj.addProperty("keys", toStorageString());
    }
    if (isSettingsModified()) {
      obj.add("settings", new ConfigElementKeybindSetting(defaultSettings, settings).toJsonElement());
    }
    return obj;
  }

  @Override
  public void fromJsonElement(JsonElement element) {
    resetToDefault();
    if (element.isJsonObject()) {
      JsonObject obj = element.getAsJsonObject();
      if (obj.has("keys")) {
        JsonElement keysEle = obj.get("keys");
        if (keysEle.isJsonPrimitive() && keysEle.getAsJsonPrimitive().isString()) {
          fromStorageString(keysEle.getAsString());
        } else {
          // TODO fail log
        }
      }
      if (obj.has("settings")) {
        ConfigElementKeybindSetting configEle = new ConfigElementKeybindSetting(defaultSettings, settings);
        configEle.fromJsonElement(obj.get("settings"));
        settings = configEle.getSettings();
      }
    } else {
      // TODO fail log
    }
  }

  @Override
  public boolean isModified() {
    return isKeyCodesModified() || isSettingsModified();
  }

  @Override
  public void resetToDefault() {
    resetKeyCodesToDefault();
    resetSettingsToDefault();
  }

}