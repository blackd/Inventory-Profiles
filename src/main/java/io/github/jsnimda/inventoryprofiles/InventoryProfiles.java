package io.github.jsnimda.inventoryprofiles;

import fi.dy.masa.malilib.event.InitializationHandler;
import io.github.jsnimda.inventoryprofiles.config.Configs2;
import net.fabricmc.api.ModInitializer;

/**
 * InventoryProfiles
 */
public class InventoryProfiles implements ModInitializer {

  @Override
  public void onInitialize() {
    InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    Configs2.saveLoadManager.load();

  }

}