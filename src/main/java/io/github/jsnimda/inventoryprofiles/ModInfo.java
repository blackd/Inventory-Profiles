package io.github.jsnimda.inventoryprofiles;

import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.ModList;

import java.util.concurrent.atomic.AtomicReference;

/**
 * ModInfo
 */
public class ModInfo {

  public static final String MOD_ID = "inventoryprofiles";
  public static final String MOD_NAME = "Inventory Profiles";
  public static String MOD_VERSION = "null";

  public static String getModVersion() {
    // see net.minecraftforge.fml.client.gui.GuiModList
    AtomicReference<String> version = new AtomicReference<>("?");
    ModList.get().getMods().forEach(x -> {
      if (x.getModId().equals(MOD_ID)) {
        version.set(MavenVersionStringHelper.artifactVersionToString(x.getVersion()));
      }
    });
    return version.get();
  }

}