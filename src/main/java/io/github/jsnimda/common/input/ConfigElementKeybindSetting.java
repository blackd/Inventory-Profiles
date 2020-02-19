package io.github.jsnimda.common.input;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import io.github.jsnimda.common.config.IConfigElementResettableMultiple;
import io.github.jsnimda.common.config.IConfigOption;
import io.github.jsnimda.common.config.options.ConfigBoolean;
import io.github.jsnimda.common.config.options.ConfigEnum;
import io.github.jsnimda.common.input.KeybindSettings.Context;
import io.github.jsnimda.common.input.KeybindSettings.KeyAction;

public class ConfigElementKeybindSetting implements IConfigElementResettableMultiple {

  private final KeybindSettings defaultSettings;
  public final ConfigEnum<Context> context;
  public final ConfigEnum<KeyAction> activateOn;
  public final ConfigBoolean allowExtraKeys;
  public final ConfigBoolean orderSensitive;

  public ConfigElementKeybindSetting(KeybindSettings defaultSettings, KeybindSettings settings) {
    this.defaultSettings = defaultSettings;
    this.context = new ConfigEnum<>(defaultSettings.context);
    this.context.setKey("context");
    this.context.setValue(settings.context);
    this.activateOn = new ConfigEnum<>(defaultSettings.activateOn);
    this.activateOn.setKey("activateOn");
    this.activateOn.setValue(settings.activateOn);
    this.allowExtraKeys = new ConfigBoolean(defaultSettings.allowExtraKeys);
    this.allowExtraKeys.setKey("allowExtraKeys");
    this.allowExtraKeys.setValue(settings.allowExtraKeys);
    this.orderSensitive = new ConfigBoolean(defaultSettings.orderSensitive);
    this.orderSensitive.setKey("orderSensitive");
    this.orderSensitive.setValue(settings.orderSensitive);
  }

  public KeybindSettings getSettings() {
    return new KeybindSettings(context.getValue(), activateOn.getValue(), allowExtraKeys.getBooleanValue(), orderSensitive.getBooleanValue());
  }

  public KeybindSettings getDefaultSettings() {
    return defaultSettings;
  }

  @Override
  public Map<String, ? extends IConfigOption> getConfigOptionsMap() {
    return getConfigOptionsMapFromConfigOptionsCollection();
  }

  @Override
  public Collection<? extends IConfigOption> getConfigOptions() {
    return Arrays.asList(context, activateOn, allowExtraKeys, orderSensitive);
  }

}