package io.github.jsnimda.inventoryprofiles;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.inventoryprofiles.config.Configs;
import io.github.jsnimda.inventoryprofiles.config.ProfilesConfigHandler;
import io.github.jsnimda.inventoryprofiles.input.InputHandler;
import net.fabricmc.api.ModInitializer;

/**
 * InventoryProfiles
 */
public class InventoryProfiles implements ModInitializer {

  @Override
  public void onInitialize() {

    // ProfilesConfigHandler.init();

    // Keybind register
    GlobalInputHandler.getInstance().registerInputHandler(new InputHandler());

    Configs.saveLoadManager.load();

  }

}