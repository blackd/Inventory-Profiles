package io.github.jsnimda.common.config;

public interface IConfigOptionNumeric<E extends Number> extends IConfigOption {

  E getMinValue();
  E getMaxValue();

}