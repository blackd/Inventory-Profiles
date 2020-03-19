package io.github.jsnimda.inventoryprofiles.compat;

import java.util.function.Function;

import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen;
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
      ConfigScreen screen1 = new ConfigScreen();
      screen1.setParent(screen);
      return new ConfigScreen();
    };
  }

}