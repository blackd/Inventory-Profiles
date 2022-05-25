/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("unused")

package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.config.builder.CATEGORY
import org.anti_ad.mc.common.config.builder.ConfigDeclaration
import org.anti_ad.mc.common.config.builder.ConfigSaveLoadManager
import org.anti_ad.mc.common.config.builder.bool
import org.anti_ad.mc.common.config.builder.button
import org.anti_ad.mc.common.config.builder.createBuilder
import org.anti_ad.mc.common.config.builder.enum
import org.anti_ad.mc.common.config.builder.handledString
import org.anti_ad.mc.common.config.builder.hotkey
import org.anti_ad.mc.common.config.builder.hotkeyedBool
import org.anti_ad.mc.common.config.builder.int
import org.anti_ad.mc.common.config.builder.string
import org.anti_ad.mc.common.config.builder.toMultiConfig
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.integration.MergePriority
import org.anti_ad.mc.ipnext.debug.GenerateTagsAsJson
import org.anti_ad.mc.ipnext.event.blackListChanged

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
    val MOVE_FOCUS_MACH_MODIFIER                  /**/ by hotkey("LEFT_CONTROL",
                                                                 KeybindSettings.GUI_EXTRA)
    val ALWAYS_INCLUDE_HOTBAR                     /**/ by bool(false)
    val ALWAYS_MOVE_ALL                           /**/ by bool(false)
    val ALWAYS_THROW_ALL                          /**/ by bool(false)

        .CATEGORY("$category.profiles")
    val ENABLE_PROFILES                          /**/ by bool(true)
        .CATEGORY("§§hide")
    val PROFILES_PER_SERVER                      /**/ by bool(true)

        .CATEGORY("$category.lock_slots")
    val ENABLE_LOCK_SLOTS                         /**/ by bool(true)
    val ENABLE_LOCK_SLOTS_PER_SERVER              /**/ by bool(true)


        .CATEGORY("$category.auto_refill")
    val ENABLE_AUTO_REFILL                        /**/ by bool(true)


        .CATEGORY("$category.privacy")
    val ENABLE_UPDATES_CHECK                      /**/ by bool(true)
    val ENABLE_ANALYTICS                          /**/ by bool(false)

        .CATEGORY("$category.advanced_options")
    val ADD_INTERVAL_BETWEEN_CLICKS               /**/ by bool(false)
    val INTERVAL_BETWEEN_CLICKS_MS                /**/ by int(10,
                                                              1,
                                                              500)
    val AUTO_CRAFT_DELAY                          /**/ by int(1,
                                                              1,
                                                              20)
    val CONTINUOUS_CRAFTING_METHOD                /**/ by int(1,
                                                              1,
                                                              2)
    val HIGHLIGHT_CLICKING_SLOT                   /**/ by bool(true)
    val RESTOCK_HOTBAR                            /**/ by bool(false)
    val SORT_AT_CURSOR                            /**/ by bool(false)
    val MOVE_ALL_AT_CURSOR                        /**/ by bool(true)
    val STOP_AT_SCREEN_CLOSE                      /**/ by bool(false)
    val IGNORE_DURABILITY                         /**/ by bool(true)


        .CATEGORY("$category.debugs")
    val DEBUG                                     /**/ by bool(false)
    val FOR_MODPACK_DEVS                          /**/ by bool(false)


        .CATEGORY("§§hide - first run")
    val FIRST_RUN by bool(true)
}

object AutoRefillSettings : ConfigDeclaration {

    override val builder = createBuilder()
        .CATEGORY("§§vgap:3")

    val DISABLE_FOR_DROP_ITEM                     /**/ by bool(false)
    val IGNORE_LOCKED_SLOTS                       /**/ by bool(true)
    val REFILL_ARMOR                              /**/ by bool(true)
    val REFILL_BEFORE_TOOL_BREAK                  /**/ by bool(true)
    val DISABLE_FOR_LOYALTY_ITEMS                 /**/ by bool(true)
    val AUTOREFILL_BLACKLIST                      /**/ by handledString("", ::blackListChanged)
    val TOOL_DAMAGE_THRESHOLD                     /**/ by int(10,
                                                              0,
                                                              100)
    val THRESHOLD_UNIT                            /**/ by enum(ThresholdUnit.ABSOLUTE)
    val AUTO_REFILL_WAIT_TICK                     /**/ by int(0,
                                                              0,
                                                              100)
        .CATEGORY("$category.auto-refill.non-enchanted")
    val ALLOW_BREAK_FOR_NON_ENCHANTED             /**/ by bool(false)
    val TOOL_MAX_DURABILITY_THRESHOLD             /**/ by int(500,
                                                              0,
                                                              5000)
        .CATEGORY("$category.auto-refill.notifications")
    val VISUAL_DURABILITY_NOTIFICATION            /**/ by bool(true)
    val TYPE_VISUAL_DURABILITY_NOTIFICATION       /**/ by enum(ToolReplaceVisualNotification.HOTBAR)
    val AUDIO_DURABILITY_NOTIFICATION             /**/ by bool(true)
    val NUMBER_OF_NOTIFICATIONS                   /**/ by int(3,
                                                              1,
                                                              10)
    val NOTIFICATION_STEP                         /**/ by int(5,
                                                              1,
                                                              5)
    val VISUAL_REPLACE_SUCCESS_NOTIFICATION       /**/ by bool(true)
    val AUDIO_REPLACE_SUCCESS_NOTIFICATION        /**/ by bool(true)
    val TYPE_VISUAL_REPLACE_SUCCESS_NOTIFICATION  /**/ by enum(ToolReplaceVisualNotification.HOTBAR)
    val VISUAL_REPLACE_FAILED_NOTIFICATION        /**/ by bool(true)
    val AUDIO_REPLACE_FAILED_NOTIFICATION         /**/ by bool(true)
    val TYPE_VISUAL_REPLACE_FAILED_NOTIFICATION   /**/ by enum(ToolReplaceVisualNotification.SUBTITLE)





}


object LockedSlotsSettings : ConfigDeclaration {

    override val builder = createBuilder()

        .CATEGORY("§§vgap:3")
    val LOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW           /**/ by bool(true)
    val LOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE    /**/ by bool(true)
    val LOCK_SLOTS_DISABLE_USER_INTERACTION             /**/ by bool(false)

        .CATEGORY("$category.lock_slots.empty")
    val LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY            /**/ by bool(false)
    val LOCKED_SLOTS_DELAY_KEEPER_REINIT_TICKS          /**/ by int(50,
                                                                    20,
                                                                    400)

        .CATEGORY("$category.hotkeys")
    val LOCK_SLOTS_SWITCH_CONFIG_MODIFIER         /**/ by hotkey("LEFT_ALT",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_CONFIG_KEY                     /**/ by hotkey("BUTTON_1",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_QUICK_CONFIG_KEY               /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_QUICK_DISABLE                  /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val LOCK_SLOTS_CONFIG_SWITCH_TYPE             /**/ by enum(SwitchType.HOLD)

        .CATEGORY("$category.lock_slots.gui")
    val SHOW_LOCKED_SLOTS_BACKGROUND              /**/ by bool(true)
    val SHOW_LOCKED_SLOTS_FOREGROUND              /**/ by bool(true)
    val ALSO_SHOW_LOCKED_SLOTS_IN_HOTBAR          /**/ by bool(true)
    val LOCKED_SLOTS_FOREGROUND_STYLE             /**/ by int(2,
                                                              1,
                                                              6)

}

object GuiSettings : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.profiles")
    val ENABLE_PROFILES_UI                        /**/ by bool(true)
    val ENABLE_PROFILES_ANNOUNCEMENT              /**/ by bool(true)
        .CATEGORY("$category.inventory")
    val ENABLE_INVENTORY_BUTTONS                  /**/ by bool(true)
    val ENABLE_INVENTORY_EDITOR_BUTTON            /**/ by bool(true)
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

}

object EditProfiles : ConfigDeclaration {
    override val builder = createBuilder()
        .CATEGORY("$category.still_under_construction")
    val OPEN_CONFIG_PROFILES_HELP                 /**/ by button(OpenProfilesHelpButtonInfo)
    val OPEN_SERVER_PROFILES                      /**/ by button(OpenProfilesConfigButtonInfo)

        .CATEGORY("$category.quick_slots")
    val QUICK_SLOT_1_PROFILE                      /**/ by string("")
    val QUICK_SLOT_2_PROFILE                      /**/ by string("")
    val QUICK_SLOT_3_PROFILE                      /**/ by string("")

}

object Hotkeys : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.hotkeys")
    val OPEN_CONFIG_MENU                          /**/ by hotkey("R,C",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val RELOAD_CUSTOM_CONFIGS                     /**/ by hotkey("R,Y",
                                                                 KeybindSettings.ANY_DEFAULT)
        .CATEGORY("$category.inventory")
    val SORT_INVENTORY                            /**/ by hotkey("R",
                                                                 KeybindSettings.GUI_DEFAULT)
    val SORT_INVENTORY_IN_COLUMNS                 /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val SORT_INVENTORY_IN_ROWS                    /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val MOVE_ALL_ITEMS                            /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val THROW_ALL_ITEMS                           /**/ by hotkey("",
                                                                 KeybindSettings.GUI_EXTRA)
    val OPEN_GUI_EDITOR                           /**/ by hotkey("R,G",
                                                                 KeybindSettings.GUI_DEFAULT)
        .CATEGORY("$category.profiles")
    val APPLY_PROFILE                             /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val NEXT_PROFILE                              /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PREV_PROFILE                              /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PROFILE_1                                 /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PROFILE_2                                 /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PROFILE_3                                 /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val SAVE_AS_PROFILE                           /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)

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

object Modpacks : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.modpacks")
    val MAKE_BLOCK_SCREEN_GENERATOR_SCRIPT        /**/ by button(GenerateTagsAsJson)
    val DIFF_CALCULATOR_PRIORITY                  /**/ by enum(MergePriority.EXTERNAL)
    val EXPORT_HINTS                              /**/ by button(ExportHints(true))
    val EXPORT_EXTERNAL_HINTS                     /**/ by button(ExportHints(false))

    val GEN_TEST_ARENA                            /**/ by hotkey("",
                                                                 KeybindSettings.INGAME_DEFAULT)

}

const val FILE_PATH = "inventoryprofilesnext/inventoryprofiles.json"

val Configs = listOf(
    ModSettings,
    GuiSettings,
    LockedSlotsSettings,
    AutoRefillSettings,
    EditProfiles,
    Hotkeys,
    Tweaks,
    Debugs,
    Modpacks
)

object SaveLoadManager : Savable by ConfigSaveLoadManager(Configs.toMultiConfig(),
                                                          FILE_PATH)
