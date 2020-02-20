package io.github.jsnimda.common.config.options;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.config.IConfigOptionPrimitive;
import io.github.jsnimda.common.config.IConfigOptionToggleable;

public class ConfigBoolean extends ConfigOptionBase implements IConfigOptionPrimitive<Boolean>, IConfigOptionToggleable {

  private final boolean defaultValue;
  private boolean value;

  public ConfigBoolean(boolean defaultValue) {
    this.defaultValue = defaultValue;
    value = defaultValue;
  }

  public boolean getBooleanValue() {
    return value;
  }

  public boolean getDefaultBooleanValue()
  {
      return this.defaultValue;
  }

  public void setBooleanValue(boolean value) {
    this.value = value;
  }

  @Override
  public Boolean getValue() {
    return getBooleanValue();
  }

  @Override
  public Boolean getDefaultValue() {
    return getDefaultBooleanValue();
  }

  @Override
  public void setValue(Boolean value) {
    setBooleanValue(value);
  }

  @Override
  public void toggleNext() {
    value = !value;
  }

  @Override
  public void togglePrevious() {
    value = !value;
  }

}