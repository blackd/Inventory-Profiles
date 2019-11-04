package io.github.jsnimda.inventoryprofiles.config;

import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;

/**
 * ConfigHandler
 */
public class ConfigHandler implements IConfigHandler {
  // TODO need review
  private static final String CONFIG_FILE_NAME = ModInfo.MOD_ID + ".json";

  public static File getConfigDirectory() {
    return new File(FileUtils.getConfigDirectory(), ModInfo.MOD_ID);
  }

  public static void loadFromFile() {
    File configFile = new File(getConfigDirectory(), CONFIG_FILE_NAME);

    if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
      JsonElement element = JsonUtils.parseJsonFile(configFile);

      if (element != null && element.isJsonObject()) {
        JsonObject root = element.getAsJsonObject();

        ConfigUtils.readConfigBase(root, "Generic", Generic.LIST);
        ConfigUtils.readHotkeyToggleOptions(root, "TweaksHotkeys", "TweaksValues", Tweaks.LIST);
        ConfigUtils.readConfigBase(root, "AdvancedOptions", AdvancedOptions.LIST);

      }
    }
  }

  public static void saveToFile() {
    File dir = getConfigDirectory();

    if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
      JsonObject root = new JsonObject();

      ConfigUtils.writeConfigBase(root, "Generic", Generic.LIST);
      ConfigUtils.writeHotkeyToggleOptions(root, "TweaksHotkeys", "TweaksValues", Tweaks.LIST);
      ConfigUtils.writeConfigBase(root, "AdvancedOptions", AdvancedOptions.LIST);

      JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
    }
  }

  @Override
  public void load() {
    loadFromFile();
  }

  @Override
  public void save() {
    saveToFile();
  }
}