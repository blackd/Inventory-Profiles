package io.github.jsnimda.common.config.options;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric;
import net.minecraft.util.math.MathHelper;

public class ConfigInteger extends ConfigOptionBase implements IConfigOptionPrimitiveNumeric<Integer> {

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

  public Integer getMinValue()
  {
      return minValue;
  }

  public Integer getMaxValue()
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

  @Override
  public void setNumericValue(Number value) {
    setIntegerValue(value.intValue());
  }

}