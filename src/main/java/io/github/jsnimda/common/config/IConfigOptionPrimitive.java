package io.github.jsnimda.common.config;

import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public interface IConfigOptionPrimitive<T> extends IConfigOption {

  T getValue();

  T getDefaultValue();

  void setValue(T value);

  @Override
  default boolean isModified() {
    return !Objects.equals(getValue(), getDefaultValue());
  }

  @Override
  default void resetToDefault() {
    setValue(getDefaultValue());
  }

  @Override
  default JsonElement toJsonElement() {
    T v = getValue();
    if (v instanceof Boolean) {
      return new JsonPrimitive((Boolean)v);
    }
    if (v instanceof Number) {
      return new JsonPrimitive((Number)v);
    }
    if (v instanceof String) {
      return new JsonPrimitive((String)v);
    }
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  default void fromJsonElement(JsonElement element) {
    if (element.isJsonPrimitive()) {
      JsonPrimitive p = element.getAsJsonPrimitive();
      T v = getDefaultValue();
      if (v instanceof Boolean) {
        if (p.isBoolean()) {
          @SuppressWarnings("unchecked") // TODO find other workarounds than @SuppressWarnings
          T o = (T)(Boolean)p.getAsBoolean();
          setValue(o);
          return;
        }
      } else if (v instanceof Number) {
        if (p.isNumber()) {
          if (v instanceof Integer) {
            @SuppressWarnings("unchecked")
            T o = (T)(Integer)p.getAsInt();
            setValue(o);
          } else if (v instanceof Double) {
            @SuppressWarnings("unchecked")
            T o = (T)(Double)p.getAsDouble();
            setValue(o);
          } else {
            throw new UnsupportedOperationException("Not implemented yet");
          }
          return;
        }
      } else if (v instanceof String) {
        if (p.isString()) {
          @SuppressWarnings("unchecked")
          T o = (T)p.getAsString();
          setValue(o);
          return;
        }
      } else {
        throw new UnsupportedOperationException("Not implemented yet");
      }
    }
    // TODO log fail
  }

}