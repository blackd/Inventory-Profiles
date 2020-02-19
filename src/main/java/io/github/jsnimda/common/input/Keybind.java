package io.github.jsnimda.common.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.jsnimda.common.config.IConfigElementResettable;

public class Keybind implements IConfigElementResettable {

  private final List<Integer> defaultKeyCodes;
  private List<Integer> keyCodes;
  private final Optional<KeybindSettings> defaultSettings; // empty if inherit parent
  private Optional<KeybindSettings> settings;

  private final Optional<Keybind> parent; // (main keybind)

  public Keybind(String defaultStorageString, KeybindSettings defaultSettings) {
    this.defaultKeyCodes = ImmutableList.copyOf(storageStringToKeyCodes(defaultStorageString));
    this.keyCodes = new ArrayList<>(defaultKeyCodes);
    this.defaultSettings = Optional.of(defaultSettings);
    this.settings = Optional.of(defaultSettings);
    this.parent = Optional.empty();
  }

  // inherit key settings of parent (default = inherit) (alternative keys)
  public Keybind(Keybind parent) {
    this.defaultKeyCodes = ImmutableList.of();
    this.keyCodes = new ArrayList<>();
    this.defaultSettings = Optional.empty();
    this.settings = Optional.empty();
    this.parent = Optional.of(parent);
  }

  public KeybindSettings getSettings() {
    return settings.isPresent() ? settings.get() : parent.get().getSettings();
  }

  public KeybindSettings getDefaultSettings() {
    return defaultSettings.isPresent() ? defaultSettings.get() : parent.get().getSettings();
  }

  public void setSettings(KeybindSettings settings) {
    this.settings = Optional.of(settings);
  }

  public boolean isActivated() {
    return GlobalInputHandler.getInstance().isActivated(keyCodes, getSettings());
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
    if (parent.isPresent()) {
      obj.addProperty("inherit", !settings.isPresent()); // inherit, for no settings property and not inherit
    }
    if (isSettingsModified()) {
      obj.add("settings", new ConfigElementKeybindSetting(getDefaultSettings(), getSettings()).toJsonElement());
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
      if (obj.has("settings")) { // ignore inherit
        ConfigElementKeybindSetting configEle = new ConfigElementKeybindSetting(getDefaultSettings(), getSettings());
        configEle.fromJsonElement(obj.get("settings"));
        settings = Optional.of(configEle.getSettings());
      } else if (parent.isPresent()) {
        boolean inherit = true;
        if (obj.has("inherit")) {
          JsonElement inheritEle = obj.get("inherit");
          if (inheritEle.isJsonPrimitive() && inheritEle.getAsJsonPrimitive().isBoolean()) {
            inherit = inheritEle.getAsBoolean();
          } else {
            // TODO fail log
          }
        }
        if (!inherit) { // then settings should not empty
          settings = Optional.of(getDefaultSettings());
        }
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