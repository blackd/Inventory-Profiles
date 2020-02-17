package io.github.jsnimda.common.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.config.IConfigOptionPrimitive;
import io.github.jsnimda.common.config.IConfigOptionToggleable;

public class ConfigEnum<E extends Enum<E>> extends ConfigOptionBase implements IConfigOptionPrimitive<E>, IConfigOptionToggleable {

  private final E defaultValue;
  private E value;

  public ConfigEnum(E defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public void toggleNext() {
    E[] values = value.getDeclaringClass().getEnumConstants();
    value = values[(value.ordinal() + 1) % values.length];
  }

  @Override
  public void togglePrevious() {
    E[] values = value.getDeclaringClass().getEnumConstants();
    value = values[(value.ordinal() + values.length - 1) % values.length];
  }

  @Override
  public E getValue() {
    return value;
  }

  @Override
  public E getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setValue(E value) {
    this.value = value;
  }

  @Override
  public JsonElement toJsonElement() {
    return new JsonPrimitive(value.name());
  }

  @Override
  public void fromJsonElement(JsonElement element) {
    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
      value = Enum.valueOf(value.getDeclaringClass(), element.getAsString());
    } else {
      // TODO fail log
    }
  }

}