package io.github.jsnimda.inventoryprofiles.config;

import static fi.dy.masa.malilib.hotkeys.KeybindSettings.RELEASE_ALLOW_EXTRA;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.hotkeys.KeybindSettings.Context;

/**
 * Configs
 */
public class Configs {
  public static final KeybindSettings GUI_ALLOW_EXTRA = KeybindSettings.create(Context.GUI, KeyAction.PRESS, true, true, false, true);

  public static class Generic {
    public static final ConfigHotkey OPEN_CONFIG_GUI;
    public static final ConfigHotkey SORT_INVENTORY;
    public static final ConfigHotkey SORT_INVENTORY_BY_GROUP_COLUMNS;
    public static final ConfigHotkey SORT_INVENTORY_BY_GROUP_ROWS;
    public static final ConfigBoolean ENABLE_SWITCH_PROFILE_HOTKEY;
    public static final ConfigHotkey SWITCH_PROFILE;
    public static final ConfigHotkey MOVE_ALL_CONTAINER_EXISTING_ITEMS;

    public static final List<IConfigBase> LIST;
    public static final List<IHotkey> HOTKEY_LIST;
    static {
      OPEN_CONFIG_GUI                 = as("openConfigGui")             .hotkey("R,C");
      SORT_INVENTORY                  = as("sortInventory")             .hotkey("R", GUI_ALLOW_EXTRA);
      SORT_INVENTORY_BY_GROUP_COLUMNS = as("sortInventoryByGroupColumns").hotkey("", GUI_ALLOW_EXTRA);
      SORT_INVENTORY_BY_GROUP_ROWS    = as("sortInventoryByGroupRows")  .hotkey("", GUI_ALLOW_EXTRA);
      ENABLE_SWITCH_PROFILE_HOTKEY    = as("enableSwitchProfileHoykey") .bool(false);
      SWITCH_PROFILE                  = as("switchProfile")             .hotkey("R", RELEASE_ALLOW_EXTRA);
      MOVE_ALL_CONTAINER_EXISTING_ITEMS = as("moveAllContainerExistingItems").hotkey("", GUI_ALLOW_EXTRA);

      LIST = Cfg.list;
      HOTKEY_LIST = Cfg.listAs(IHotkey.class);
      Cfg.flush();
    }

  }
  public static class Tweaks {
    public static final ConfigBooleanHotkeyed INSTANT_MINING_COOLDOWN;
    public static final ConfigBooleanHotkeyed DISABLE_BLOCK_BREAKING_COOLDOWN;
    public static final ConfigBooleanHotkeyed DISABLE_ITEM_USE_COOLDOWN;
    public static final ConfigBooleanHotkeyed PREVENT_CLOSE_GUI_DROP_ITEM;
    public static final ConfigBooleanHotkeyed DISABLE_SCREEN_SHAKING_ON_DAMAGE;
    public static final ConfigBooleanHotkeyed DISABLE_LAVA_FOG;
    public static final List<IHotkeyTogglable> LIST;
    static {
      INSTANT_MINING_COOLDOWN         = as("instantMiningCooldown")       .hotkeyedBool(false, "");
      DISABLE_BLOCK_BREAKING_COOLDOWN = as("disableBlockBreakingCooldown").hotkeyedBool(false, "");
      DISABLE_ITEM_USE_COOLDOWN       = as("disableItemUseCooldown")      .hotkeyedBool(false, "");
      PREVENT_CLOSE_GUI_DROP_ITEM     = as("preventCloseGuiDropItem")     .hotkeyedBool(false, "");
      DISABLE_SCREEN_SHAKING_ON_DAMAGE = as("disbleScreenShakingOnDamage").hotkeyedBool(false, "");
      DISABLE_LAVA_FOG                = as("disableLavaFog")              .hotkeyedBool(false, "");

      LIST = Cfg.listAs(IHotkeyTogglable.class);
      Cfg.flush();
    }
  }
  public static class AdvancedOptions {
    public static final ConfigBoolean ADD_INTERVAL_BETWEEN_CLICKS;
    public static final ConfigInteger INTERVAL_BETWEEN_CLICKS_MS;
    public static final ConfigBoolean SORT_RESTOCK_HOTBAR;
    public static final ConfigBoolean SHOW_INVENTORY_BUTTON_TOOLTIPS;
    public static final ConfigBoolean INVENTORY_SHOW_PROFILE_BUTTONS;
    public static final ConfigBoolean INVENTORY_SHOW_SORT_BUTTONS;
    public static final ConfigBoolean INVENTORY_SHOW_MOVE_ALL_BUTTONS;
    public static final ConfigBoolean SORT_CURSOR_POINTING;
    public static final ConfigBoolean SORT_CLICK_TARGETS_FIRST;
    public static final ConfigBoolean DEBUG_LOGS;
    public static final List<IConfigBase> LIST;
    static {
      ADD_INTERVAL_BETWEEN_CLICKS     = as("addIntervalBetweenClicks")    .bool(false);
      INTERVAL_BETWEEN_CLICKS_MS      = as("intervalBetweenClicksMs")     .integer(10, 1, 500);
      SORT_RESTOCK_HOTBAR             = as("sortRestockHotbar")           .bool(true);
      SHOW_INVENTORY_BUTTON_TOOLTIPS  = as("showInventoryButtonTooltips") .bool(true);
      INVENTORY_SHOW_PROFILE_BUTTONS  = as("inventoryShowProfileButtons") .bool(true);
      INVENTORY_SHOW_SORT_BUTTONS     = as("inventoryShowSortButtons")    .bool(true);
      INVENTORY_SHOW_MOVE_ALL_BUTTONS = as("inventoryShowMoveAllButtons") .bool(true);
      SORT_CURSOR_POINTING            = as("sortCursorPointing")          .bool(false);
      SORT_CLICK_TARGETS_FIRST        = as("sortClickTargetsFirst")       .bool(true);
      DEBUG_LOGS                      = as("debugLogs")                   .bool(false);

      LIST = Cfg.list;
      Cfg.flush();
    }
  }

  // ===== code compact and collapse logics
  //

  public static Cfg as(String name) {
    return new Cfg(name);
  }

  public static final String TRANSLATION_KEY_COMMENT_PREFIX = "inventoryprofiles.config.comment.";
  public static final String TRANSLATION_KEY_PRETTYNAME_PREFIX = "inventoryprofiles.config.prettyname.";
  private static class Cfg {
    public static List<IConfigBase> list = new ArrayList<>();

    public static <T> List<T> listAs(Class<T> clazz) {
      return list.stream()
              .filter(clazz::isInstance)
              .map(clazz::cast)
              .collect(Collectors.toList());
    }
    public static void flush() {
      list = new ArrayList<>();
    }

    String name;
    String comment;
    String prettyName;
    public Cfg(String name) {
      this.name = name;
      this.comment = TRANSLATION_KEY_COMMENT_PREFIX + name;
      this.prettyName = TRANSLATION_KEY_PRETTYNAME_PREFIX + name;
    }
    public ConfigHotkey hotkey(String defaultStorageString) {
      return (ConfigHotkey)add(new ConfigHotkey(name, defaultStorageString, comment, prettyName));
    }
    public ConfigHotkey hotkey(String defaultStorageString, KeybindSettings settings) {
      return (ConfigHotkey)add(new ConfigHotkey(name, defaultStorageString, settings, comment, prettyName));
    }
    public ConfigBoolean bool(boolean defaultValue) {
      return (ConfigBoolean)add(new ConfigBoolean(name, defaultValue, comment, prettyName));
    }
    public ConfigBooleanHotkeyed hotkeyedBool(boolean defaultValue, String defaultHotkey) {
      return (ConfigBooleanHotkeyed)add(
        new ConfigBooleanHotkeyed(name, defaultValue, defaultHotkey, comment, prettyName));
    }
    public ConfigInteger integer(int defaultValue, int minValue, int maxValue) {
      return (ConfigInteger)add(new ConfigInteger(name, defaultValue, minValue, maxValue, comment));
    }
    public IConfigBase add(IConfigBase ele) {
      list.add(ele);
      return ele;
    }
  }
}