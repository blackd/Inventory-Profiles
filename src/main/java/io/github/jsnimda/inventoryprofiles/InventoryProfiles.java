package io.github.jsnimda.inventoryprofiles;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ModInitializer;

/**
 * InventoryProfiles
 */
public class InventoryProfiles implements ModInitializer {

  @Override
  public void onInitialize() {
    InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
  }

}