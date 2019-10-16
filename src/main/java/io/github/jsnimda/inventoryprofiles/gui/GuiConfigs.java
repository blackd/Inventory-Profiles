package io.github.jsnimda.inventoryprofiles.gui;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;

/**
 * GuiConfigs
 */
public class GuiConfigs extends GuiConfigsBase {

  private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;

  public GuiConfigs() {
    super(10, 50, ModInfo.MOD_ID, null, "inventoryprofiles.gui.title.configs");
  }

  @Override
  public void initGui() {
    super.initGui();
    this.clearOptions();

    int x = 10;
    int y = 26;

    for (ConfigGuiTab tab : ConfigGuiTab.values())
    {
      x += this.createButton(x, y, -1, tab) + 2;
    }
  }

  private int createButton(int x, int y, int width, ConfigGuiTab tab) {
    ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
    button.setEnabled(GuiConfigs.tab != tab);
    this.addButton(button, new ButtonListener(tab, this));

    return button.getWidth();
  }

  @Override
  protected int getConfigWidth()
  {
      ConfigGuiTab tab = GuiConfigs.tab;

      if (tab == ConfigGuiTab.GENERIC)
      {
          //return 100;
          return super.getConfigWidth();
      }

      return super.getConfigWidth();
  }

  @Override
  public List<ConfigOptionWrapper> getConfigs() {
    return ConfigOptionWrapper.createFor(l());
  }
  private List<? extends IConfigBase> l(){
    ConfigGuiTab tab = GuiConfigs.tab;
    if (tab == ConfigGuiTab.GENERIC){
      return Generic.LIST;
    }
    // TODO combine hotkey and values into one page
    // if (tab == ConfigGuiTab.TWEAKS){
    //   // ImmutableList.Builder<IConfigBase> a = ImmutableList.builder();
    //   // Configs.Tweaks.LIST.forEach(x->{
    //   //   a.add(new ConfigTypeWrapper(ConfigType.HOTKEY, x));
    //   //   a.add(new ConfigTypeWrapper(ConfigType.BOOLEAN, x));
    //   // });
    //   // return a.build();
    //   return Tweaks.LIST;
    // }
    if (tab == ConfigGuiTab.TWEAK_VALUES){
      return ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Tweaks.LIST));
    }
    if (tab == ConfigGuiTab.TWEAK_HOTKEYS){
      return ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(Tweaks.LIST));
    }
    if (tab == ConfigGuiTab.ADVANCED_OPTIONS){
      return AdvancedOptions.LIST;
    }
    return Collections.emptyList();
  }
  
  private static class ButtonListener implements IButtonActionListener
  {
      private final GuiConfigs parent;
      private final ConfigGuiTab tab;

      public ButtonListener(ConfigGuiTab tab, GuiConfigs parent)
      {
          this.tab = tab;
          this.parent = parent;
      }

      @Override
      public void actionPerformedWithButton(ButtonBase button, int mouseButton)
      {
          GuiConfigs.tab = this.tab;

          this.parent.reCreateListWidget(); // apply the new config width
          this.parent.getListWidget().resetScrollbarPosition();
          this.parent.initGui();
      }
  }

  public enum ConfigGuiTab {
    GENERIC ("inventoryprofiles.gui.button.config_gui.generic"),
    ADVANCED_OPTIONS ("inventoryprofiles.gui.button.config_gui.advanced_options"),
    //PROFILES ("inventoryprofiles.gui.button.config_gui.profiles"),
    //TWEAKS ("inventoryprofiles.gui.button.config_gui.tweaks"); //,
    TWEAK_VALUES ("inventoryprofiles.gui.button.config_gui.tweak_values"), //,
    TWEAK_HOTKEYS ("inventoryprofiles.gui.button.config_gui.tweak_hotkeys");
    //HOTKEYS ("inventoryprofiles.gui.button.config_gui.hotkeys");

    private final String translationKey;

    private ConfigGuiTab(String translationKey)
    {
      this.translationKey = translationKey;
    }

    public String getDisplayName()
    {
      return StringUtils.translate(this.translationKey);
    }
  }
}