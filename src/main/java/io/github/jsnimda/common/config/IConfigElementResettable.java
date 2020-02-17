package io.github.jsnimda.common.config;

public interface IConfigElementResettable extends IConfigElement {

  boolean isModified();
  void resetToDefault();

}