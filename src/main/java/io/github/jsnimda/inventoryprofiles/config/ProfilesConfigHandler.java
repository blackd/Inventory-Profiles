package io.github.jsnimda.inventoryprofiles.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.util.Identifier;

/**
 * ProfilesConfigHandler
 */
public class ProfilesConfigHandler {

  private static final String PROFILES_FILE_NAME = "Profiles." + ModInfo.MOD_VERSION + ".txt";
  private static final String PROFILES_DEFAULT_FILE_NAME = "Profiles." + ModInfo.MOD_VERSION + ".default.txt";
  private static final String PROFILES_DEFAULT_CLASSPATH = "configs/profiles.txt";
  private static Identifier IDENT;
  private static String defaultProfilesString;

  private static File dir;

  public static void init() {
    InputStream inputStream;
    IDENT = new Identifier(ModInfo.MOD_ID, PROFILES_DEFAULT_CLASSPATH);
    try {
      inputStream = Current.resourceManager().getResource(IDENT).getInputStream();
      defaultProfilesString = IOUtils.toString(inputStream, "UTF-8");
      ProfilesConfig.defaultProfiles = ProfilesConfig.parse(defaultProfilesString);

      dir = ConfigHandler.getConfigDirectory();
  
      if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
        File def = new File(dir, PROFILES_DEFAULT_FILE_NAME);
        if (!def.exists()) {
          FileUtils.writeStringToFile(def, defaultProfilesString, "UTF-8");
        }
        File cfg = new File(dir, PROFILES_FILE_NAME);
        if (!cfg.exists()) {
          FileUtils.writeStringToFile(cfg, defaultProfilesString, "UTF-8");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static ProfilesConfig loadCfg() {
    File configFile = new File(dir, PROFILES_FILE_NAME);
    if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
      try {
        return ProfilesConfig.parse(FileUtils.readFileToString(configFile, "UTF-8"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println("[inventoryprofiles] Cannot load profiles file, use default instead.");
    return ProfilesConfig.defaultProfiles;
  }




}