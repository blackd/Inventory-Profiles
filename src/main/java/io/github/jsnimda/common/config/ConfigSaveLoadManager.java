package io.github.jsnimda.common.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;

import net.minecraft.client.Minecraft;


public class ConfigSaveLoadManager {

  public static File getConfigDirectory() {
    return new File(Minecraft.getInstance().gameDir, "config");
  }

  private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final IConfigElement config;
  private final String path;

  public ConfigSaveLoadManager(IConfigElement config, String path) {
    this.config = config;
    this.path = path;
  }

  private File getConfigFile() {
    return getConfigDirectory().toPath().resolve(path).toFile();
  }

  public void save() {
    String val = GSON.toJson(config.toJsonElement());
    try {
      FileUtils.writeStringToFile(getConfigFile(), val, StandardCharsets.UTF_8);
    } catch (IOException e) {
      // TODO handle io err
      e.printStackTrace();
    }
  }

  public void load() {
    if (!getConfigFile().exists()) {
      return;
    }
    try {
      String val = FileUtils.readFileToString(getConfigFile(), StandardCharsets.UTF_8);
      JsonElement element = new JsonParser().parse(val);
      config.fromJsonElement(element);
    } catch (IOException e) {
      // TODO handle io err
      e.printStackTrace();
    } catch (JsonParseException e) {
      // TODO handle json parse err
      e.printStackTrace();
    }
  }
}