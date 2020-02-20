package io.github.jsnimda.common.config;

public interface IConfigOptionPrimitiveNumeric<T extends Number> extends IConfigOptionPrimitive<T> {

  void setValue(Number value);
  T getMinValue();
  T getMaxValue();

}