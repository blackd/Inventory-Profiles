package io.github.jsnimda.inventoryprofiles;

import io.github.jsnimda.common.forge.CommonForgeEventHandler;
import io.github.jsnimda.inventoryprofiles.forge.ForgeEventHandler;
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

/**
 * InventoryProfiles
 */
@Mod(ModInfo.MOD_ID)
public class ForgeModEntry {

  public ForgeModEntry() {

    MinecraftForge.EVENT_BUS.register(new CommonForgeEventHandler());

    MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> {
      return (x, y) -> new ConfigScreen();
    });

//    GlobalInputHandler.getInstance().registerInputHandler(new InputHandler());
//
//    Configs.saveLoadManager.load();

    InventoryProfilesKt.init();

//    GlobalInitHandler.INSTANCE.onInit();
    // ^^ let do it on first tick event
  }

}
