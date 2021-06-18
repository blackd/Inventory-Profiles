package org.anti_ad.mc.common.fabric;

public class FabricUtil {

  public static String getModVersionString(String modId) { // ref: malilib StringUtils.getModVersionString()
    for (net.fabricmc.loader.api.ModContainer container : net.fabricmc.loader.api.FabricLoader.getInstance()
        .getAllMods()) {
      if (container.getMetadata().getId().equals(modId)) {
        return container.getMetadata().getVersion().getFriendlyString();
      }
    }

    return "?";
  }

}