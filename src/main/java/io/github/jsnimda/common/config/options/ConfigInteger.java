package io.github.jsnimda.common.config.options;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.config.IConfigOptionPrimitive;
import net.minecraft.util.math.MathHelper;

public class ConfigInteger extends ConfigOptionBase implements IConfigOptionPrimitive<Integer> {

  private final int defaultValue;
  private final int minValue;
  private final int maxValue;
  private int value;

  public ConfigInteger(int defaultValue, int minValue, int maxValue) {
    this.defaultValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    value = defaultValue;
  }

  public int getIntegerValue() {
    return value;
  }

  public int getDefaultIntegerValue() {
    return defaultValue;
  }

  public void setIntegerValue(int value) {
    this.value = MathHelper.clamp(value, minValue, maxValue);
  }

  public int getMinValue()
  {
      return minValue;
  }

  public int getMaxValue()
  {
      return maxValue;
  }

  @Override
  public Integer getValue() {
    return getIntegerValue();
  }

  @Override
  public Integer getDefaultValue() {
    return getDefaultIntegerValue();
  }

  @Override
  public void setValue(Integer value) {
    setIntegerValue(value);
  }

}