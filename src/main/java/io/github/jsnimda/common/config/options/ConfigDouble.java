package io.github.jsnimda.common.config.options;

import io.github.jsnimda.common.config.ConfigOptionBase;
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric;
import net.minecraft.util.math.MathHelper;

public class ConfigDouble extends ConfigOptionBase implements IConfigOptionPrimitiveNumeric<Double> {

  private final double defaultValue;
  private final double minValue;
  private final double maxValue;
  private double value;

  public ConfigDouble(double defaultValue, double minValue, double maxValue) {
    this.defaultValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    value = defaultValue;
  }

  public double getDoubleValue() {
    return value;
  }

  public double getDefaultDoubleValue() {
    return defaultValue;
  }

  public void setDoubleValue(double value) {
    this.value = MathHelper.clamp(value, minValue, maxValue);
  }

  public Double getMinValue()
  {
      return minValue;
  }

  public Double getMaxValue()
  {
      return maxValue;
  }

  @Override
  public Double getValue() {
    return getDoubleValue();
  }

  @Override
  public Double getDefaultValue() {
    return getDefaultDoubleValue();
  }

  @Override
  public void setValue(Double value) {
    setDoubleValue(value);
  }

  @Override
  public void setValue(Number value) {
    setDoubleValue(value.doubleValue());
  }

}