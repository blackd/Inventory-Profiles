package io.github.jsnimda.inventoryprofiles.external;

import java.util.function.Function;

import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.gui.GuiConfigs;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

/**
 * ModMenuApiImpl
 */
public class ModMenuApiImpl implements ModMenuApi {

  @Override
  public String getModId() {
    return ModInfo.MOD_ID;
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return (screen) -> {
      GuiConfigs gui = new GuiConfigs();
      gui.setParent(screen);
      return gui;
    };
  }

}