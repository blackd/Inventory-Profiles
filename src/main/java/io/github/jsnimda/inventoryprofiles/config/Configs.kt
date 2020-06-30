@file:Suppress("unused")

package io.github.jsnimda.inventoryprofiles.config

import io.github.jsnimda.common.Savable
import io.github.jsnimda.common.config.builder.*
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.inventoryprofiles.parser.OpenConfigFolderButtonInfo
import io.github.jsnimda.inventoryprofiles.parser.ReloadRuleFileButtonInfo

private const val category = "inventoryprofiles.config.category"

object ModSettings : ConfigDeclaration {
  override val builder = createBuilder()

    .CATEGORY("§§h:3")
  val OPEN_CONFIG_FOLDER                        /**/ by button(OpenConfigFolderButtonInfo)
  val RELOAD_RULE_FILES                         /**/ by button(ReloadRuleFileButtonInfo)

    .CATEGORY("$category.inventory_sorting")
  val SORT_ORDER                                /**/ by enum(SortingMethod.DEFAULT)
  val CUSTOM_RULE                               /**/ by string("@default")

    .CATEGORY("$category.auto_refill")
  val ENABLE_AUTO_REFILL                        /**/ by bool(true)
  val REFILL_ARMOR                              /**/ by bool(true)
  val REFILL_BEFORE_TOOL_BREAK                  /**/ by bool(true)
  val TOOL_DAMAGE_THRESHOLD                     /**/ by int(10, 0, 100)
  val THRESHOLD_UNIT                            /**/ by enum(ThresholdUnit.ABSOLUTE)
  val AUTO_REFILL_WAIT_TICK                     /**/ by int(0, 0, 100)

    .CATEGORY("$category.advanced_options")
  val ADD_INTERVAL_BETWEEN_CLICKS               /**/ by bool(false)
  val INTERVAL_BETWEEN_CLICKS_MS                /**/ by int(10, 1, 500)
  val RESTOCK_HOTBAR                            /**/ by bool(false)
  val SORT_AT_CURSOR                            /**/ by bool(false)
  val STOP_AT_SCREEN_CLOSE                      /**/ by bool(false)

    .CATEGORY("$category.debugs")
  val DEBUG by bool(false)
}

object GuiSettings : ConfigDeclaration {
  override val builder = createBuilder()

    .CATEGORY("$category.inventory")
  val SHOW_REGULAR_SORT_BUTTON                  /**/ by bool(true)
  val REGULAR_POST_ACTION                       /**/ by enum(PostAction.NONE)
  val REGULAR_SORT_ORDER                        /**/ by enum(SortingMethodIndividual.GLOBAL)
  val REGULAR_CUSTOM_RULE                       /**/ by string("@default")
  val SHOW_SORT_IN_COLUMNS_BUTTON               /**/ by bool(true)
  val IN_COLUMNS_POST_ACTION                    /**/ by enum(PostAction.GROUP_IN_COLUMNS)
  val IN_COLUMNS_SORT_ORDER                     /**/ by enum(SortingMethodIndividual.GLOBAL)
  val IN_COLUMNS_CUSTOM_RULE                    /**/ by string("@default")
  val SHOW_SORT_IN_ROWS_BUTTON                  /**/ by bool(true)
  val IN_ROWS_POST_ACTION                       /**/ by enum(PostAction.GROUP_IN_ROWS)
  val IN_ROWS_SORT_ORDER                        /**/ by enum(SortingMethodIndividual.GLOBAL)
  val IN_ROWS_CUSTOM_RULE                       /**/ by string("@default")
  val SHOW_MOVE_ALL_BUTTON                      /**/ by bool(true)
  val SHOW_BUTTON_TOOLTIPS                      /**/ by bool(true)
}

object EditProfiles : ConfigDeclaration {
  override val builder = createBuilder()

    .CATEGORY("$category.coming_soon")
}

object Hotkeys : ConfigDeclaration {
  override val builder = createBuilder()

    .CATEGORY("$category.hotkeys")
  val OPEN_CONFIG_MENU                          /**/ by hotkey("R,C", KeybindSettings.INGAME_DEFAULT)
  val SORT_INVENTORY                            /**/ by hotkey("R", KeybindSettings.GUI_DEFAULT)
  val SORT_INVENTORY_IN_COLUMNS                 /**/ by hotkey("", KeybindSettings.GUI_DEFAULT)
  val SORT_INVENTORY_IN_ROWS                    /**/ by hotkey("", KeybindSettings.GUI_DEFAULT)
  val MOVE_ALL_ITEMS                            /**/ by hotkey("", KeybindSettings.GUI_DEFAULT)
}

object Tweaks : ConfigDeclaration {
  override val builder = createBuilder()

    .CATEGORY("$category.client_side_tweaks")
  val INSTANT_MINING_COOLDOWN                   /**/ by hotkeyedBool(false)
  val DISABLE_BLOCK_BREAKING_COOLDOWN           /**/ by hotkeyedBool(false)
  val DISABLE_ITEM_USE_COOLDOWN                 /**/ by hotkeyedBool(false)
  val PREVENT_CLOSE_GUI_DROP_ITEM               /**/ by hotkeyedBool(false)
  val DISABLE_SCREEN_SHAKING_ON_DAMAGE          /**/ by hotkeyedBool(false)
  val DISABLE_LAVA_FOG                          /**/ by hotkeyedBool(false)
  val CONTAINER_SWIPE_MOVING_ITEMS              /**/ by hotkeyedBool(true)
}

object Debugs : ConfigDeclaration {
  override val builder = createBuilder()

    .CATEGORY("$category.debugs")
  val DEBUG_SCREEN                              /**/ by hotkey("Z", KeybindSettings.ANY_DEFAULT)
  val SCREEN_DEPTH_TEST                         /**/ by hotkey("X,1", KeybindSettings.ANY_DEFAULT)
  val DEBUG_RENDER                              /**/ by bool(false)
}

const val FILE_PATH = "inventoryprofiles/inventoryprofiles.json"

val Configs = listOf(
  ModSettings,
  GuiSettings,
  EditProfiles,
  Hotkeys,
  Tweaks,
  Debugs
)

object SaveLoadManager : Savable by ConfigSaveLoadManager(Configs.toMultiConfig(), FILE_PATH)
