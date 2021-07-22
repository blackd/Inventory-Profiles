@file:Suppress("unused")

package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.config.builder.*
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.ipnext.debug.GenerateRuleListButtonInfo
import org.anti_ad.mc.ipnext.debug.GenerateTagVanillaTxtButtonInfo
import org.anti_ad.mc.ipnext.parser.OpenConfigFolderButtonInfo
import org.anti_ad.mc.ipnext.parser.ReloadRuleFileButtonInfo

private const val category = "inventoryprofiles.config.category"

object ModSettings : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("§§vgap:3")
    val OPEN_CONFIG_FOLDER                        /**/ by button(OpenConfigFolderButtonInfo)
    val RELOAD_RULE_FILES                         /**/ by button(ReloadRuleFileButtonInfo)

        .CATEGORY("$category.inventory_sorting")
    val SORT_ORDER                                /**/ by enum(SortingMethod.DEFAULT)
    val CUSTOM_RULE                               /**/ by string("@custom")

        .CATEGORY("$category.move_matching_items")
    val INCLUDE_HOTBAR_MODIFIER                   /**/ by hotkey("LEFT_ALT",
                                                                 KeybindSettings.GUI_EXTRA)
    val MOVE_ALL_MODIFIER                         /**/ by hotkey("LEFT_SHIFT",
                                                                 KeybindSettings.GUI_EXTRA)
    val ALWAYS_INCLUDE_HOTBAR                     /**/ by bool(false)
    val ALWAYS_MOVE_ALL                           /**/ by bool(false)

        .CATEGORY("$category.lock_slots")
    val ENABLE_LOCK_SLOTS                         /**/ by bool(true)
    val ENABLE_LOCK_SLOTS_PER_SERVER              /**/ by bool(true)
    val LOCK_SLOTS_SWITCH_CONFIG_MODIFIER         /**/ by hotkey("LEFT_ALT",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_CONFIG_KEY                     /**/ by hotkey("BUTTON_1",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_QUICK_CONFIG_KEY               /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_QUICK_DISABLE                  /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_CONFIG_SWITCH_TYPE             /**/ by enum(SwitchType.HOLD)
    val SHOW_LOCKED_SLOTS_BACKGROUND              /**/ by bool(true)
    val SHOW_LOCKED_SLOTS_FOREGROUND              /**/ by bool(true)
    val LOCKED_SLOTS_FOREGROUND_STYLE             /**/ by int(1,
                                                              1,
                                                              6)
    val LOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW     /**/ by bool(false)
    val LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY      /**/ by bool(false)
    val LOCKED_SLOTS_DELAY_KEEPER_REINIT_TICKS    /**/ by int(5,
                                                              0,
                                                              100)

        .CATEGORY("$category.auto_refill")
    val ENABLE_AUTO_REFILL                        /**/ by bool(true)
    val DISABLE_FOR_DROP_ITEM                     /**/ by bool(false)
    val REFILL_ARMOR                              /**/ by bool(true)
    val REFILL_BEFORE_TOOL_BREAK                  /**/ by bool(true)
    val TOOL_DAMAGE_THRESHOLD                     /**/ by int(10,
                                                              0,
                                                              100)
    val THRESHOLD_UNIT                            /**/ by enum(ThresholdUnit.ABSOLUTE)
    val AUTO_REFILL_WAIT_TICK                     /**/ by int(0,
                                                              0,
                                                              100)

/* todo
  auto pick
  ref: MiningToolItem.getMiningSpeed()
*/

        .CATEGORY("$category.advanced_options")
    val ADD_INTERVAL_BETWEEN_CLICKS               /**/ by bool(false)
    val INTERVAL_BETWEEN_CLICKS_MS                /**/ by int(10,
                                                              1,
                                                              500)
    val HIGHLIGHT_CLICKING_SLOT                   /**/ by bool(true)
    val RESTOCK_HOTBAR                            /**/ by bool(false)
    val SORT_AT_CURSOR                            /**/ by bool(false)
    val MOVE_ALL_AT_CURSOR                        /**/ by bool(true)
    val STOP_AT_SCREEN_CLOSE                      /**/ by bool(false)

        .CATEGORY("$category.debugs")
    val DEBUG by bool(false)
}

object GuiSettings : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.inventory")
    val ENABLE_INVENTORY_BUTTONS                  /**/ by bool(true)
    val TREAT_UNKNOWN_SCREENS_AS_CONTAINERS       /**/ by bool(true)
    val SHOW_CONTINUOUS_CRAFTING_CHECKBOX         /**/ by bool(true)
    val CONTINUOUS_CRAFTING_CHECKBOX_VALUE        /**/ by enum(ContinuousCraftingCheckboxValue.REMEMBER)
    val CONTINUOUS_CRAFTING_SAVED_VALUE           /**/ by bool(true)
    val SHOW_REGULAR_SORT_BUTTON                  /**/ by bool(true)
    val REGULAR_POST_ACTION                       /**/ by enum(PostAction.NONE)
    val REGULAR_SORT_ORDER                        /**/ by enum(SortingMethodIndividual.GLOBAL)
    val REGULAR_CUSTOM_RULE                       /**/ by string("@custom")
    val SHOW_SORT_IN_COLUMNS_BUTTON               /**/ by bool(true)
    val IN_COLUMNS_POST_ACTION                    /**/ by enum(PostAction.GROUP_IN_COLUMNS)
    val IN_COLUMNS_SORT_ORDER                     /**/ by enum(SortingMethodIndividual.GLOBAL)
    val IN_COLUMNS_CUSTOM_RULE                    /**/ by string("@custom")
    val SHOW_SORT_IN_ROWS_BUTTON                  /**/ by bool(true)
    val IN_ROWS_POST_ACTION                       /**/ by enum(PostAction.GROUP_IN_ROWS)
    val IN_ROWS_SORT_ORDER                        /**/ by enum(SortingMethodIndividual.GLOBAL)
    val IN_ROWS_CUSTOM_RULE                       /**/ by string("@custom")
    val SHOW_MOVE_ALL_BUTTON                      /**/ by bool(true)
    val SHOW_BUTTON_TOOLTIPS                      /**/ by bool(true)

        .CATEGORY("$category.other")
    val USE_OLD_INSERT_METHOD                     /**/ by bool(false)
}

object EditProfiles : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.coming_soon")
}

object Hotkeys : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.hotkeys")
    val OPEN_CONFIG_MENU                          /**/ by hotkey("R,C",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val SORT_INVENTORY                            /**/ by hotkey("R",
                                                                 KeybindSettings.GUI_DEFAULT)
    val SORT_INVENTORY_IN_COLUMNS                 /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val SORT_INVENTORY_IN_ROWS                    /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val MOVE_ALL_ITEMS                            /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val THROW_ALL_ITEMS                            /**/ by hotkey("",
                                                                  KeybindSettings.GUI_EXTRA)

        .CATEGORY("$category.misc")
    val DUMP_ITEM_NBT_TO_CHAT                     /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
}

object Tweaks : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.client_side_tweaks")
        .CATEGORY("§§vgap:-5")
        .CATEGORY("$category.inventory")
    val PREVENT_CLOSE_GUI_DROP_ITEM               /**/ by hotkeyedBool(false)
    val CONTAINER_SWIPE_MOVING_ITEMS              /**/ by hotkeyedBool(true)
    val SWIPE_MOVE_CRAFTING_RESULT_SLOT           /**/ by hotkeyedBool(false)

        .CATEGORY("$category.survival")
    val INSTANT_MINING_COOLDOWN                   /**/ by hotkeyedBool(false)
    val DISABLE_BLOCK_BREAKING_COOLDOWN           /**/ by hotkeyedBool(false)
    val DISABLE_ITEM_USE_COOLDOWN                 /**/ by hotkeyedBool(false)
    val DISABLE_SCREEN_SHAKING_ON_DAMAGE          /**/ by hotkeyedBool(false)
    val DISABLE_LAVA_FOG                          /**/ by hotkeyedBool(false)

}

object Debugs : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.debugs")
    val TRACE_LOGS                                /**/ by bool(false)
    val DEBUG_RENDER                              /**/ by bool(false)
    val FORCE_NO_CLEAN_CURSOR                     /**/ by bool(false)
    val DIFF_CALCULATOR                           /**/ by enum(DiffCalculatorType.SIMPLE)
    val DEBUG_SCREEN                              /**/ by hotkey("Z,1",
                                                                 KeybindSettings.ANY_DEFAULT)
    val SCREEN_DEPTH_TEST                         /**/ by hotkey("Z,2",
                                                                 KeybindSettings.ANY_DEFAULT)
    val SCREEN_SPRITE_TEST                        /**/ by hotkey("Z,3",
                                                                 KeybindSettings.ANY_DEFAULT)
    val CLEAN_CURSOR                              /**/ by hotkey("X,1",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DUMP_PACKET_IDS                           /**/ by hotkey("X,2",
                                                                 KeybindSettings.ANY_DEFAULT)
    val GEN_TAG_VANILLA_TXT                       /**/ by button(GenerateTagVanillaTxtButtonInfo)
    val GEN_RULE_LIST                             /**/ by button(GenerateRuleListButtonInfo)
}

const val FILE_PATH = "inventoryprofilesnext/inventoryprofiles.json"

val Configs = listOf(
    ModSettings,
    GuiSettings,
    EditProfiles,
    Hotkeys,
    Tweaks,
    Debugs
)

object SaveLoadManager : Savable by ConfigSaveLoadManager(Configs.toMultiConfig(),
                                                          FILE_PATH)
