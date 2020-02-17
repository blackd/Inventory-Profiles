package io.github.jsnimda.inventoryprofiles.gui;

import java.util.function.Supplier;

import io.github.jsnimda.common.gui.ConfigOptionListWidget;
import io.github.jsnimda.common.gui.ConfigScreenBase;
import io.github.jsnimda.inventoryprofiles.config.Configs2;
import io.github.jsnimda.inventoryprofiles.config.Configs2.GuiSettings;
import io.github.jsnimda.inventoryprofiles.config.Configs2.Hotkeys;
import io.github.jsnimda.inventoryprofiles.config.Configs2.ModSettings;
import io.github.jsnimda.inventoryprofiles.config.Configs2.Tweaks;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class ConfigScreen extends ConfigScreenBase {

  public static final String DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name.";
  public static final String DESCRIPTION_PREFIX = "inventoryprofiles.config.description.";

  public static int selectedIndex = 0;
  public static Supplier<ConfigOptionListWidget> modSettings;
  public static Supplier<ConfigOptionListWidget> guiSettings;
  public static Supplier<ConfigOptionListWidget> hotkeys;
  public static Supplier<ConfigOptionListWidget> tweaks;
  static {
    modSettings = () -> ConfigOptionListWidget.from(Configs2.getConfigs(ModSettings.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
    guiSettings = () -> ConfigOptionListWidget.from(Configs2.getConfigs(GuiSettings.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
    hotkeys = () -> ConfigOptionListWidget.from(Configs2.getConfigs(Hotkeys.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
    tweaks = () -> ConfigOptionListWidget.from(Configs2.getConfigs(Tweaks.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
  }

  public ConfigScreen() {
		super(new TranslatableText("inventoryprofiles.gui.config.title"));
    this.addNavigationButton(translate("ModSettings"), modSettings);
    this.addNavigationButton(translate("GuiSettings"), guiSettings);
    this.addNavigationButton(translate("EditProfiles"));
    this.addNavigationButton(translate("Hotkeys"), hotkeys);
    this.addNavigationButton(translate("Tweaks"), tweaks);

    setSelectedIndex(selectedIndex);
  }

  private static String translate(String key) {
    return I18n.translate("inventoryprofiles.gui.config." + key);
  }

  @Override
  public void setSelectedIndex(int index) {
    selectedIndex = index;
    super.setSelectedIndex(index);
  }

  @Override
  public void init() {
    super.init();
    this.addButton(new ButtonWidget(this.width - 10 - 150, 5, 150, 20, "R + C", null));

  }

  @Override
  public void onClose() {
    Configs2.saveLoadManager.save();
    super.onClose();
  }

}