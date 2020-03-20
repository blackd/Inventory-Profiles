package io.github.jsnimda.inventoryprofiles.config

import io.github.jsnimda.common.config.builder.*
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodOption

private const val category = "inventoryprofiles.config.category"

object ModSettings : ConfigSingleton {
  val defined by builder

    .CATEGORY("$category.inventory_sorting")
  val SORT_ORDER by enum(SortingMethodOption.DEFAULT)
  val ADD_INTERVAL_BETWEEN_CLICKS by bool(false)
  val INTERVAL_BETWEEN_CLICKS_MS by int(10, 1, 500)
  val RESTOCK_HOTBAR by bool(false)
  val SORT_AT_CURSOR by bool(false)
  val STOP_AT_SCREEN_CLOSE by bool(false)

    .CATEGORY("$category.debugs")
  val DEBUG_LOGS by bool(false)
}

object GuiSettings : ConfigSingleton {
  val defined by builder

    .CATEGORY("$category.inventory")
  val SHOW_SORT_BUTTON by bool(true)
  val SHOW_SORT_IN_COLUMNS_BUTTON by bool(true)
  val SHOW_SORT_IN_ROWS_BUTTON by bool(true)
  val SHOW_MOVE_ALL_BUTTON by bool(true)
  val SHOW_BUTTON_TOOLTIPS by bool(true)
}

object EditProfiles : ConfigSingleton{
  val defined by builder

    .CATEGORY("$category.coming_soon")
}

object Hotkeys : ConfigSingleton {
  val defined by builder

    .CATEGORY("$category.hotkeys")
  val OPEN_CONFIG_MENU by hotkey("R,C", KeybindSettings.INGAME_DEFAULT)
  val SORT_INVENTORY by hotkey("R", KeybindSettings.GUI_DEFAULT)
  val SORT_INVENTORY_IN_COLUMNS by hotkey("")
  val SORT_INVENTORY_IN_ROWS by hotkey("")
  val MOVE_ALL_ITEMS by hotkey("")

    .CATEGORY("$category.debugs")
  val DEBUG_SCREEN by hotkey("Z", KeybindSettings.ANY_DEFAULT)
}

object Tweaks : ConfigSingleton {
  val defined by builder

    .CATEGORY("inventoryprofiles.config.category.client_side_tweaks")
  val INSTANT_MINING_COOLDOWN by hotkeyedBool(false)
  val DISABLE_BLOCK_BREAKING_COOLDOWN by hotkeyedBool(false)
  val DISABLE_ITEM_USE_COOLDOWN by hotkeyedBool(false)
  val PREVENT_CLOSE_GUI_DROP_ITEM by hotkeyedBool(false)
  val DISABLE_SCREEN_SHAKING_ON_DAMAGE by hotkeyedBool(false)
  val DISABLE_LAVA_FOG by hotkeyedBool(false)
}

const val FILE_PATH = "inventoryprofiles/inventoryprofiles.json"

val Configs = listOf(
  ModSettings,
  GuiSettings,
  EditProfiles,
  Hotkeys,
  Tweaks
)

object SaveLoadManager : Savable by ConfigSaveLoadManager(Configs.toConfigs(), FILE_PATH)
