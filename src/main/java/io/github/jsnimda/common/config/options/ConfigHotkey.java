package io.github.jsnimda.common.config.options;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.input.Keybind;
import io.github.jsnimda.common.input.KeybindSettings;

public class ConfigHotkey extends ConfigOptionBase {

  private final Keybind mainKeybind;
  private List<Keybind> alternativeKeybinds = new ArrayList<>();

  public ConfigHotkey(String defaultStorageString, KeybindSettings defaultSettings) {
    mainKeybind = new Keybind(defaultStorageString, defaultSettings);
  }

  public boolean isActivated() {
    return mainKeybind.isActivated() || alternativeKeybinds.stream().anyMatch(x -> x.isActivated());
  }

  public Keybind getMainKeybind() {
    return mainKeybind;
  }

  public List<Keybind> getAlternativeKeybinds() {
    return alternativeKeybinds;
  }

  @Override
  public boolean isModified() {
    return !alternativeKeybinds.isEmpty() || mainKeybind.isModified();
  }

  @Override
  public void resetToDefault() {
    alternativeKeybinds.clear();
    mainKeybind.resetToDefault();
  }

  @Override
  public JsonElement toJsonElement() {
    JsonObject obj = new JsonObject();
    if (mainKeybind.isModified()) {
      obj.add("main", mainKeybind.toJsonElement());
    }
    if (!alternativeKeybinds.isEmpty()) {
      JsonArray arr = new JsonArray();
      alternativeKeybinds.forEach(x -> arr.add(x.toJsonElement()));
      obj.add("alternatives", arr);
    }
    return obj;
  }

  @Override
  public void fromJsonElement(JsonElement element) {
    resetToDefault();
    if (element.isJsonObject()) {
      JsonObject obj = element.getAsJsonObject();
      if (obj.has("main")) {
        mainKeybind.fromJsonElement(obj.get("main"));
      }
      if (obj.has("alternatives")) {
        JsonElement alternativesEle = obj.get("alternatives");
        if (alternativesEle.isJsonArray()) {
          JsonArray arr = alternativesEle.getAsJsonArray();
          arr.forEach(x -> {
            Keybind alt = new Keybind(mainKeybind);
            alt.fromJsonElement(x);
            if (alt.isModified()) {
              alternativeKeybinds.add(alt);
            }
          });
        } else {
          // TODO fail log
        }
      }
    } else {
      // TODO fail log
    }
  }

  
}