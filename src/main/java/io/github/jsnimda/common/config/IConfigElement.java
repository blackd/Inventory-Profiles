package io.github.jsnimda.common.config;

import com.google.gson.JsonElement;

public interface IConfigElement {

  JsonElement toJsonElement();
  void fromJsonElement(JsonElement element);

}