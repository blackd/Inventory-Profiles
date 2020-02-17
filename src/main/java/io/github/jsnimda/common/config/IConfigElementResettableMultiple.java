package io.github.jsnimda.common.config;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IConfigElementResettableMultiple extends IConfigElementResettable {

  Map<String, ? extends IConfigOption> getConfigOptionsMap();

  default Collection<? extends IConfigOption> getConfigOptions() {
    return getConfigOptionsMap().values();
  };

  @Override
  default JsonElement toJsonElement() {
    JsonObject obj = new JsonObject();
    for (IConfigOption configOption : getConfigOptions()) {
      if (configOption.isModified()) {
        obj.add(configOption.getKey(), configOption.toJsonElement());
      }
    }
    return obj;
  }

  @Override
  default void fromJsonElement(JsonElement element) {
    // reset to default first
    resetToDefault();
    if (element.isJsonObject()) {
      for (Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
        Map<String, ? extends IConfigOption> configOptionsMap = getConfigOptionsMap();
        if (configOptionsMap.containsKey(entry.getKey())) {
          configOptionsMap.get(entry.getKey()).fromJsonElement(entry.getValue());
        } else {
          // TODO fail handle
        }
      }
    } else {
      // TODO fail handle
    }
  }
  
  @Override
  default boolean isModified() {
    return getConfigOptions().stream().anyMatch(x -> x.isModified());
  }

  @Override
  default void resetToDefault() {
    getConfigOptions().forEach(x -> x.resetToDefault());
  }

}