package io.github.jsnimda.inventoryprofiles;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import io.github.jsnimda.inventoryprofiles.config.ConfigHandler;
import io.github.jsnimda.inventoryprofiles.config.ProfilesConfigHandler;
import io.github.jsnimda.inventoryprofiles.eventhandler.KeybindCallbacks;
import io.github.jsnimda.inventoryprofiles.eventhandler.KeybindProvider;
import net.minecraft.client.MinecraftClient;

/**
 * InitHandler
 */
public class InitHandler implements IInitializationHandler {

  @Override
  public void registerModHandlers() {

    // Register all event handler

    // save/load config file trigger
    // ProfilesConfigHandler.init();
    ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new ConfigHandler());

    // Keybind register
    InputEventHandler.getKeybindManager().registerKeybindProvider(KeybindProvider.INSTANCE);

    // Keybind set callbacks
    KeybindCallbacks.init(MinecraftClient.getInstance());

  }

}