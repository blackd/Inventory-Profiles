package io.github.jsnimda.inventoryprofiles.config;

import static io.github.jsnimda.common.config.ConfigsClassBuilder.*;

import io.github.jsnimda.common.config.CategorizedConfigOptions;
import io.github.jsnimda.common.config.ConfigSaveLoadManager;
import io.github.jsnimda.common.config.ConfigsClassBuilder.Category;
import io.github.jsnimda.common.config.ConfigsClassBuilder.ConfigOptionsClass;
import io.github.jsnimda.common.config.ConfigsClassBuilder.Exclude;
import io.github.jsnimda.common.config.options.ConfigBoolean;
import io.github.jsnimda.common.config.options.ConfigEnum;
import io.github.jsnimda.common.config.options.ConfigHotkey;
import io.github.jsnimda.common.config.options.ConfigHotkeyedBoolean;
import io.github.jsnimda.common.config.options.ConfigInteger;
import io.github.jsnimda.common.input.KeybindSettings;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodOption;

public class Configs {

  @ConfigOptionsClass
  public static class ModSettings {
    @Category("inventoryprofiles.config.category.inventory_sorting")
    public static final ConfigEnum<SortingMethodOption> SORT_ORDER      = enumList(SortingMethodOption.DEFAULT);
    public static final ConfigBoolean ADD_INTERVAL_BETWEEN_CLICKS       = bool(false);
    public static final ConfigInteger INTERVAL_BETWEEN_CLICKS_MS        = integer(10, 1, 500);
    public static final ConfigBoolean RESTOCK_HOTBAR                    = bool(false);
    public static final ConfigBoolean SORT_AT_CURSOR                    = bool(false);
    public static final ConfigBoolean STOP_AT_SCREEN_CLOSE              = bool(false);
    @Category("inventoryprofiles.config.category.debugs")
    public static final ConfigBoolean DEBUG_LOGS                        = bool(false);
  }

  @ConfigOptionsClass
  public static class GuiSettings {
    @Category("inventoryprofiles.config.category.inventory")
    public static final ConfigBoolean SHOW_SORT_BUTTON                  = bool(true);
    public static final ConfigBoolean SHOW_SORT_IN_COLUMNS_BUTTON       = bool(true);
    public static final ConfigBoolean SHOW_SORT_IN_ROWS_BUTTON          = bool(true);
    public static final ConfigBoolean SHOW_MOVE_ALL_BUTTON              = bool(true);
    public static final ConfigBoolean SHOW_BUTTON_TOOLTIPS              = bool(true);
  }

  @ConfigOptionsClass
  public static class Hotkeys {
    @Category("inventoryprofiles.config.category.hotkeys")
    public static final ConfigHotkey OPEN_CONFIG_MENU                   = hotkey("R,C", KeybindSettings.INGAME_DEFAULT);
    public static final ConfigHotkey SORT_INVENTORY                     = hotkey("R", KeybindSettings.GUI_DEFAULT);
    public static final ConfigHotkey SORT_INVENTORY_IN_COLUMNS          = hotkey("");
    public static final ConfigHotkey SORT_INVENTORY_IN_ROWS             = hotkey("");
    public static final ConfigHotkey MOVE_ALL_ITEMS                     = hotkey("");
    @Category("inventoryprofiles.config.category.debugs")
    public static final ConfigHotkey DEBUG_SCREEN                       = hotkey("Z", KeybindSettings.ANY_DEFAULT);
  }

  @ConfigOptionsClass
  public static class Tweaks {
    @Category("inventoryprofiles.config.category.client_side_tweaks")
    // public static final ConfigHotkeyedBoolean INSTANT_MINING_COOLDOWN           = hotkeyedBool(false);
    public static final ConfigHotkeyedBoolean DISABLE_BLOCK_BREAKING_COOLDOWN   = hotkeyedBool(false);
    public static final ConfigHotkeyedBoolean DISABLE_ITEM_USE_COOLDOWN         = hotkeyedBool(false);
    public static final ConfigHotkeyedBoolean PREVENT_CLOSE_GUI_DROP_ITEM       = hotkeyedBool(false);
    // public static final ConfigHotkeyedBoolean DISABLE_SCREEN_SHAKING_ON_DAMAGE  = hotkeyedBool(false);
    // public static final ConfigHotkeyedBoolean DISABLE_LAVA_FOG                  = hotkeyedBool(false);
  }

  @Exclude
  public static final CategorizedConfigOptions CLASS_CONFIGS = loadWithNested(Configs.class, false);

  public static final String FILE_PATH = "inventoryprofiles/inventoryprofiles.json";
  public static ConfigSaveLoadManager saveLoadManager = new ConfigSaveLoadManager(CLASS_CONFIGS, FILE_PATH);

  public static CategorizedConfigOptions getConfigs(Class<?> clazz) {
    return (CategorizedConfigOptions)CLASS_CONFIGS.getConfigOptionsMap().get(clazz.getSimpleName());
  }

}