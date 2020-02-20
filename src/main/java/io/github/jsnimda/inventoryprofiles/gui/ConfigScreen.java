package io.github.jsnimda.inventoryprofiles.gui;

import java.util.function.Supplier;

import io.github.jsnimda.common.gui.ConfigOptionListWidget;
import io.github.jsnimda.common.gui.ConfigOptionWidgetBase;
import io.github.jsnimda.common.gui.ConfigScreenBase;
import io.github.jsnimda.inventoryprofiles.config.Configs;
import io.github.jsnimda.inventoryprofiles.config.Configs.GuiSettings;
import io.github.jsnimda.inventoryprofiles.config.Configs.Hotkeys;
import io.github.jsnimda.inventoryprofiles.config.Configs.ModSettings;
import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfigScreen extends ConfigScreenBase {

  private Screen parent = null;

  public static final String DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name.";
  public static final String DESCRIPTION_PREFIX = "inventoryprofiles.config.description.";

  public static int selectedIndex = 0;
  public static Supplier<ConfigOptionListWidget> modSettings;
  public static Supplier<ConfigOptionListWidget> guiSettings;
  public static Supplier<ConfigOptionListWidget> editProfiles;
  public static Supplier<ConfigOptionListWidget> hotkeys;
  public static Supplier<ConfigOptionListWidget> tweaks;
  static {
    modSettings = () -> ConfigOptionListWidget.from(Configs.getConfigs(ModSettings.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
    guiSettings = () -> ConfigOptionListWidget.from(Configs.getConfigs(GuiSettings.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
    editProfiles = () -> {
      ConfigOptionListWidget c = new ConfigOptionListWidget(DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
      c.addAnchor(I18n.format("inventoryprofiles.config.category.coming_soon"));
      return c;
    };
    hotkeys = () -> ConfigOptionListWidget.from(Configs.getConfigs(Hotkeys.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
    tweaks = () -> ConfigOptionListWidget.from(Configs.getConfigs(Tweaks.class), DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX);
  }

  public ConfigScreen(Screen parent) {
    this();
    this.parent = parent;
  }

  public ConfigScreen() {
		super(new TranslationTextComponent("inventoryprofiles.gui.config.title"));
    this.addNavigationButton(translate("ModSettings"), modSettings);
    this.addNavigationButton(translate("GuiSettings"), guiSettings);
    this.addNavigationButton(translate("EditProfiles"), editProfiles);
    this.addNavigationButton(translate("Hotkeys"), hotkeys);
    this.addNavigationButton(translate("Tweaks"), tweaks);

    setSelectedIndex(selectedIndex);
  }

  private static String translate(String key) {
    return I18n.format("inventoryprofiles.gui.config." + key);
  }

  @Override
  public void setSelectedIndex(int index) {
    selectedIndex = index;
    super.setSelectedIndex(index);
  }

  private ConfigOptionWidgetBase<?> openConfigMenuHotkeyWidget;

  @Override
  public void init() {
    super.init();
    openConfigMenuHotkeyWidget = ConfigOptionWidgetBase.of(Hotkeys.OPEN_CONFIG_MENU);
    openConfigMenuHotkeyWidget.x = this.width - 10 - 150;
    openConfigMenuHotkeyWidget.y = 5;
    openConfigMenuHotkeyWidget.width = 150;
    openConfigMenuHotkeyWidget.height = 20;
    children.add(openConfigMenuHotkeyWidget);

  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    renderBackground(0);
    openConfigMenuHotkeyWidget.render(mouseX, mouseY, partialTicks);
    super.render(mouseX, mouseY, partialTicks);
  }

  @Override
  public void renderBackground() {
    // do nothing
  }

  @Override
  public void onClose() {
    Configs.saveLoadManager.save();
    if (parent != null) {
      this.minecraft.displayGuiScreen(parent);
    } else {
      super.onClose();
    }
  }

}