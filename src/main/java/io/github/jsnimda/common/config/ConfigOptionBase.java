package io.github.jsnimda.common.config;

public abstract class ConfigOptionBase implements IConfigOption {

  private String key;

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void setKey(String key) {
    this.key = key;
  }

}