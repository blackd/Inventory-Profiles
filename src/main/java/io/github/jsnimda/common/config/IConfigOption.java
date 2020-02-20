package io.github.jsnimda.common.config;

public interface IConfigOption extends IConfigElementResettable {

  String getKey();
  void setKey(String key);

}