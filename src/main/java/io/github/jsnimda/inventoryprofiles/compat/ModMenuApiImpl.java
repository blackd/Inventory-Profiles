package io.github.jsnimda.inventoryprofiles.compat;

import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

/**
 * ModMenuApiImpl
 */
public class ModMenuApiImpl implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return ConfigScreen::new;
  }

}