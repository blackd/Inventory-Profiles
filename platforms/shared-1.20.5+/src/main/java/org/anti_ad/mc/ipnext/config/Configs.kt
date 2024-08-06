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
import org.anti_ad.mc.common.config.builder.color
import org.anti_ad.mc.common.config.builder.createBuilder
import org.anti_ad.mc.common.config.builder.enum
import org.anti_ad.mc.common.config.builder.enumForMinMCVersion
import org.anti_ad.mc.common.config.builder.handledString
import org.anti_ad.mc.common.config.builder.handledStringForMinMCVersion
import org.anti_ad.mc.common.config.builder.hotkey

import org.anti_ad.mc.common.config.builder.int
import org.anti_ad.mc.common.config.builder.string
import org.anti_ad.mc.common.config.builder.toMultiConfig
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.vanilla.render.asAlpha
import org.anti_ad.mc.common.vanilla.render.b
import org.anti_ad.mc.common.vanilla.render.blue
import org.anti_ad.mc.common.vanilla.render.g
import org.anti_ad.mc.common.vanilla.render.green
import org.anti_ad.mc.common.vanilla.render.r
import org.anti_ad.mc.common.vanilla.render.red
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.integration.MergePriority
import org.anti_ad.mc.ipnext.config.defaults.AUTO_REFILL_WAIT_TICK_DEFAULT
import org.anti_ad.mc.ipnext.config.defaults.AUTO_REFILL_WAIT_TICK_MINIMUM
import org.anti_ad.mc.ipnext.debug.GenerateTagsAsJson
import org.anti_ad.mc.ipnext.event.AutoRefillHandler
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper
import org.anti_ad.mc.ipnext.gui.ConfigScreeHelper.keyToggleBool
import org.anti_ad.mc.ipnext.item.ItemTypeExtensionsObject

private const val category = "inventoryprofiles.config.category"

object ModSettings : ConfigDeclaration {

    override val builder = createBuilder()

        .CATEGORY("§§vgap:3")
    val OPEN_CONFIG_FOLDER                        /**/ by button(OpenConfigFolderButtonInfo)
    val RELOAD_RULE_FILES                         /**/ by button(ReloadRuleFileButtonInfo)

        .CATEGORY("$category.inventory_sorting")
    val SORT_ORDER                                /**/ by enum(SortingMethod.DEFAULT)
    val CUSTOM_RULE                               /**/ by string("@custom")

        .CATEGORY("§§hide")
    val CREATIVE_SORT_ORDER_TYPE                  /**/ by enumForMinMCVersion(1193,
                                                                              ModInfo.MINECRAFT_VERSION,
                                                                              CreativeMenuSortOrder.SEARCH_TAB)

    val CATEGORY_PRIORITY_LIST                    /**/ by handledStringForMinMCVersion(1193,
                                                                                       ModInfo.MINECRAFT_VERSION,
                                                                                       "itemGroup.tools, itemGroup.combat, itemGroup.redstone, itemGroup.coloredBlocks, itemGroup.functional, itemGroup.natural, itemGroup.buildingBlocks, itemGroup.foodAndDrink, itemGroup.ingredients, itemGroup.spawnEggs",
                                                                                       ItemTypeExtensionsObject::priorityListChanged)
    val CATEGORY_ORIGINAL_ORDER                   /**/ by handledStringForMinMCVersion(1193,
                                                                                       ModInfo.MINECRAFT_VERSION,
                                                                                       ItemTypeExtensionsObject.makeDefaultList(),
                                                                                       ItemTypeExtensionsObject::defaultOrderListChanged)
        .CATEGORY("$category.move_matching_items")
    val INCLUDE_HOTBAR_MODIFIER                   /**/ by hotkey("LEFT_ALT",
                                                                 KeybindSettings.GUI_EXTRA)
    val MOVE_ALL_MODIFIER                         /**/ by hotkey("LEFT_SHIFT",
                                                                 KeybindSettings.GUI_EXTRA)
    val MOVE_FOCUS_MACH_MODIFIER                  /**/ by hotkey("LEFT_CONTROL",
                                                                 KeybindSettings.GUI_EXTRA)
    val MOVE_JUST_REFILL_MODIFIER                 /**/ by hotkey("CAPS_LOCK",
                                                                 KeybindSettings.GUI_EXTRA)

    val ALWAYS_INCLUDE_HOTBAR                     /**/ by bool(false)
    val ALWAYS_MOVE_ALL                           /**/ by bool(false)
    val ALWAYS_THROW_ALL                          /**/ by bool(false)

        .CATEGORY("$category.highlight_focused_items")
    val HIGHLIGHT_FOUSED_ITEMS                    /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val HIGHLIGHT_FOUSED_ITEMS_ANIMATED           /**/ by keyToggleBool(false, KeybindSettings.GUI_DEFAULT)
    val HIGHLIGHT_FOUSED_ITEMS_FOREGROUND         /**/ by bool(true)
    val HIGHLIGHT_FOUSED_ITEMS_COLOR              /**/ by color(0x70.asAlpha().red(1).green(0xB6).blue(0x0b))
    val HIGHLIGHT_FOUSED_ITEMS_BG_COLOR           /**/ by color(0xAA.asAlpha().red(1).green(0xB6).blue(0x0b))
        .CATEGORY("§§hide")
    val HIGHLIGHT_FOUSED_WAIT_TICKS               /**/ by int(5, 3, 15)

/*
        .CATEGORY("$category.randomizer")
    val ENABLE_RANDOMIZER                         /**/ by bool(true)
*/
        .CATEGORY("$category.profiles")
    val ENABLE_PROFILES                          /**/ by keyToggleBool(true)
        .CATEGORY("§§hide")
    val PROFILES_PER_SERVER                      /**/ by bool(true)

        .CATEGORY("$category.lock_slots")
    val ENABLE_LOCK_SLOTS                         /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val ENABLE_LOCK_SLOTS_PER_SERVER              /**/ by bool(true)


        .CATEGORY("$category.auto_refill")
    val ENABLE_AUTO_REFILL                        /**/ by keyToggleBool(true)

        .CATEGORY("$category.villager_trading")
        //.CATEGORY("§§hide")
    val VILLAGER_TRADING_ENABLE                   /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val VILLAGER_TRADING_GROUP_1                  /**/ by keyToggleBool(false, KeybindSettings.GUI_DEFAULT)
    val VILLAGER_TRADING_GROUP_2                  /**/ by keyToggleBool(false, KeybindSettings.GUI_DEFAULT)

    val VILLAGER_TRADING_LOCAL_COLOR              /**/ by color(130.r(0x96).g(1).b(0xb))
    val VILLAGER_TRADING_GLOBAL_COLOR             /**/ by color(130.r(1).g(0x96).b(0xb))
        .CATEGORY("§§vgap:5")
    val VILLAGER_TRADING_LOCAL_COLOR1             /**/ by color(130.r(0x35).g(0x3f).b(0xA2))
    val VILLAGER_TRADING_GLOBAL_COLOR1            /**/ by color(130.r(0xff).g(0x76).b(0x3d))
        .CATEGORY("§§vgap:5")
    val VILLAGER_TRADING_LOCAL_COLOR2             /**/ by color(130.r(0xf6).g(0xDF).b(0x65))
    val VILLAGER_TRADING_GLOBAL_COLOR2            /**/ by color(130.r(0xd7).g(0x74).b(0xeb))




        .CATEGORY("$category.privacy")
    val ENABLE_UPDATES_CHECK                      /**/ by bool(true)

        .CATEGORY("$category.advanced_options")
    val ADD_INTERVAL_BETWEEN_CLICKS               /**/ by keyToggleBool(false, KeybindSettings.GUI_DEFAULT)
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
    val RESTOCK_HOTBAR                            /**/ by keyToggleBool(false, KeybindSettings.GUI_DEFAULT)
    val SORT_AT_CURSOR                            /**/ by bool(true)
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
        .CATEGORY("$category.auto-refill.general")
    val DISABLE_FOR_DROP_ITEM                     /**/ by keyToggleBool(false)
    val IGNORE_LOCKED_SLOTS                       /**/ by bool(true)
    val AUTO_REFILL_PREFER_SMALLER_STACKS         /**/ by bool(false)
    val REFILL_ARMOR                              /**/ by keyToggleBool(true)
    val REFILL_BEFORE_TOOL_BREAK                  /**/ by keyToggleBool(true)
    val AUTOREFILL_BLACKLIST                      /**/ by handledString("", AutoRefillHandler::blackListChanged)
    val STACKABLE_THRESHOLD                       /**/ by int(0,
                                                              0,
                                                              64)
    val TOOL_DAMAGE_THRESHOLD                     /**/ by int(10,
                                                              0,
                                                              100)
    val THRESHOLD_UNIT                            /**/ by enum(ThresholdUnit.ABSOLUTE)
    val AUTO_REFILL_WAIT_TICK                     /**/ by int(AUTO_REFILL_WAIT_TICK_DEFAULT,
                                                              AUTO_REFILL_WAIT_TICK_MINIMUM,
                                                              100)
    val AUTO_REFILL_TEMP_DISABLE_REFILL_FOR_TOOLS /**/ by hotkey("LEFT_ALT", KeybindSettings.INGAME_DEFAULT.copy(allowExtraKeys = true))

    val AUTO_REFILL_ENABLE_PER_SLOT_CONFIG        /**/ by keyToggleBool(true)
    val AUTO_REFILL_ENABLE_INDICATOR_ICONS        /**/ by keyToggleBool(true)
    val AUTO_REFILL_ENABLE_HORBAR_INDICATOR_ICONS /**/ by keyToggleBool(true)


        .CATEGORY("$category.auto-refill.matching")
    val DISABLE_FOR_LOYALTY_ITEMS                 /**/ by bool(true)
    val AUTO_REFILL_MATCH_CUSTOM_NAME             /**/ by bool(true)
    val AUTO_REFILL_MATCH_NBT                     /**/ by bool(true)
    val AUTO_REFILL_MATCH_NBT_TYPE                /**/ by enum(AutoRefillNbtMatchType.EXACT)
    val AUTO_REFILL_IGNORE_NBT_FOR_BUCKETS        /**/ by bool(true)
    val AUTO_REFILL_MATCH_ANY_FOOD                /**/ by bool(false)
    val AUTO_REFILL_MATCH_HARMFUL_FOOD            /**/ by bool(false)

        .CATEGORY("$category.auto-refill.non-enchanted")
    val ALLOW_BREAK_FOR_NON_ENCHANTED             /**/ by keyToggleBool(false)
    val TOOL_MAX_DURABILITY_THRESHOLD             /**/ by int(500,
                                                              0,
                                                              5000)
        .CATEGORY("$category.auto-refill.notifications")
    val ALLOW_ALERTS_WITHOUT_TOOL_PROTECTION      /**/ by keyToggleBool(false)
    val VISUAL_DURABILITY_NOTIFICATION            /**/ by keyToggleBool(true)
    val TYPE_VISUAL_DURABILITY_NOTIFICATION       /**/ by enum(ToolReplaceVisualNotification.HOTBAR)
    val AUDIO_DURABILITY_NOTIFICATION             /**/ by keyToggleBool(true)
    val NUMBER_OF_NOTIFICATIONS                   /**/ by int(3,
                                                              1,
                                                              10)
    val NOTIFICATION_STEP                         /**/ by int(5,
                                                              1,
                                                              5)
    val VISUAL_REPLACE_SUCCESS_NOTIFICATION       /**/ by keyToggleBool(true)
    val AUDIO_REPLACE_SUCCESS_NOTIFICATION        /**/ by keyToggleBool(true)
    val TYPE_VISUAL_REPLACE_SUCCESS_NOTIFICATION  /**/ by enum(ToolReplaceVisualNotification.HOTBAR)
    val VISUAL_REPLACE_FAILED_NOTIFICATION        /**/ by keyToggleBool(true)
    val AUDIO_REPLACE_FAILED_NOTIFICATION         /**/ by keyToggleBool(true)
    val TYPE_VISUAL_REPLACE_FAILED_NOTIFICATION   /**/ by enum(ToolReplaceVisualNotification.SUBTITLE)

}


object LockedSlotsSettings : ConfigDeclaration {

    override val builder = createBuilder()

        .CATEGORY("§§vgap:3")
    val LOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW           /**/ by bool(true)
    val LOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE    /**/ by bool(true)
    val LOCK_SLOTS_DISABLE_USER_INTERACTION             /**/ by keyToggleBool(false, KeybindSettings.GUI_DEFAULT)

        .CATEGORY("$category.lock_slots.empty")
    val LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY            /**/ by keyToggleBool(false)
    val LOCKED_SLOTS_DELAY_KEEPER_REINIT_TICKS          /**/ by int(50,
                                                                    20,
                                                                    400)
    val LOCKED_SLOTS_EMPTY_HOTBAR_AS_SEMI_LOCKED        /**/ by keyToggleBool(false)
    val LOCKED_SLOTS_EMPTY_HOTBAR_BLACKLIST             /**/ by handledString("carryon", LockedSlotKeeper::updateIgnored)

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
    val SHOW_LOCKED_SLOTS_BACKGROUND              /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val SHOW_LOCKED_SLOTS_FOREGROUND              /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val SHOW_LOCKED_SLOTS_BG_COLOR                /**/ by color(192.asAlpha().red(255).green(85).blue(85))
    val SHOW_LOCKED_SLOTS_HOTBAR_COLOR            /**/ by color(94.asAlpha().red(255).green(94).blue(65))
    val ALSO_SHOW_LOCKED_SLOTS_IN_HOTBAR          /**/ by keyToggleBool(true)
    val LOCKED_SLOTS_FOREGROUND_STYLE             /**/ by int(2,
                                                              1,
                                                              6)

}

object GuiSettings : ConfigDeclaration {

    override val builder = createBuilder()

        .CATEGORY("$category.gui.general")
    val INVENTORY_OVERLAY_BUTTONS_VISIBLE         /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val ENABLE_INVENTORY_EDITOR_BUTTON            /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val ENABLE_INVENTORY_SETTINGS_BUTTON          /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
        .CATEGORY("$category.profiles")
    val ENABLE_PROFILES_UI                        /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val ENABLE_PROFILES_ANNOUNCEMENT              /**/ by bool(true)
        .CATEGORY("$category.inventory")
    val ENABLE_INVENTORY_BUTTONS                  /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val SHOW_BUTTONS_BOTH_SIDES                   /**/ by bool(false)

    val SHOW_FAST_RENAME_CHECKBOX                 /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val FAST_RENAME_CHECKBOX_VALUE                /**/ by enum(ContinuousCraftingCheckboxValue.UNCHECKED)
    val FAST_RENAME_SAVED_VALUE                   /**/ by bool(false)
    val SHOW_CONTINUOUS_CRAFTING_CHECKBOX         /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val CONTINUOUS_CRAFTING_CHECKBOX_VALUE        /**/ by enum(ContinuousCraftingCheckboxValue.REMEMBER)
    val CONTINUOUS_CRAFTING_SAVED_VALUE           /**/ by bool(true)
    val SHOW_REGULAR_SORT_BUTTON                  /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val REGULAR_POST_ACTION                       /**/ by enum(PostAction.NONE)
    val REGULAR_SORT_ORDER                        /**/ by enum(SortingMethodIndividual.GLOBAL)
    val REGULAR_CUSTOM_RULE                       /**/ by string("@custom")
    val SHOW_SORT_IN_COLUMNS_BUTTON               /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val IN_COLUMNS_POST_ACTION                    /**/ by enum(PostAction.GROUP_IN_COLUMNS)
    val IN_COLUMNS_SORT_ORDER                     /**/ by enum(SortingMethodIndividual.GLOBAL)
    val IN_COLUMNS_CUSTOM_RULE                    /**/ by string("@custom")
    val SHOW_SORT_IN_ROWS_BUTTON                  /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val IN_ROWS_POST_ACTION                       /**/ by enum(PostAction.GROUP_IN_ROWS)
    val IN_ROWS_SORT_ORDER                        /**/ by enum(SortingMethodIndividual.GLOBAL)
    val IN_ROWS_CUSTOM_RULE                       /**/ by string("@custom")
    val SHOW_MOVE_ALL_BUTTON                      /**/ by keyToggleBool(true, KeybindSettings.GUI_DEFAULT)
    val SHOW_BUTTON_TOOLTIPS                      /**/ by bool(true)


        .CATEGORY("§§hide - default button possitions")
    val SETTINGS_TOP by int(25, Int.MIN_VALUE, Int.MAX_VALUE)
    val SETTINGS_LEFT by int(10, Int.MIN_VALUE, Int.MAX_VALUE)
    val EDITOR_TOP by int(10, Int.MIN_VALUE, Int.MAX_VALUE)
    val EDITOR_LEFT by int(10, Int.MIN_VALUE, Int.MAX_VALUE)

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
        .CATEGORY("$category.still_under_construction")
    val INCLUDE_CUSTOM_NAME                       /**/ by bool(false)

}

object ScrollSettings: ConfigDeclaration {
    override val builder = createBuilder()
        .CATEGORY("$category.scroll.modifiers")
    val SCROLL_FULL_STACK                                /**/ by hotkey("LEFT_SHIFT",
                                                                        KeybindSettings.GUI_EXTRA)
    val SCROLL_LEAVE_LAST                                /**/ by hotkey("Z",
                                                                        KeybindSettings.GUI_EXTRA)
    val SCROLL_SPREAD                                    /**/ by hotkey("X",
                                                                        KeybindSettings.GUI_EXTRA)
    val SCROLL_THROW                                     /**/ by hotkey("C",
                                                                        KeybindSettings.GUI_EXTRA)
    val TEMP_DISABLE                                     /**/ by hotkey("LEFT_CONTROL",
                                                                        KeybindSettings.GUI_EXTRA)
        .CATEGORY("$category.scroll.single")
    val SCROLL_AUTO_PICKUP_NEXT_FOR_SINGLE               /**/ by bool(true)

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
    val MOVE_ALL_ITEMS                            /**/ by hotkey("R,T",
                                                                 KeybindSettings.GUI_EXTRA)
    val THROW_ALL_ITEMS                           /**/ by hotkey("R,M",
                                                                 KeybindSettings.GUI_EXTRA)
    val SCROLL_TO_CHEST                           /**/ by hotkey("MOUSE_SCROLL_UP",
                                                                 KeybindSettings.GUI_EXTRA)
    val SCROLL_TO_INVENTORY                       /**/ by hotkey("MOUSE_SCROLL_DOWN",
                                                                 KeybindSettings.GUI_EXTRA)
    val OPEN_GUI_EDITOR                           /**/ by hotkey("R,G",
                                                                 KeybindSettings.GUI_DEFAULT)
        .CATEGORY("$category.profiles")
    val APPLY_PROFILE                             /**/ by hotkey("R,H",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val NEXT_PROFILE                              /**/ by hotkey("R,Z",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PREV_PROFILE                              /**/ by hotkey("R,X",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PROFILE_1                                 /**/ by hotkey("R,1",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PROFILE_2                                 /**/ by hotkey("R,2",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val PROFILE_3                                 /**/ by hotkey("R,3",
                                                                 KeybindSettings.INGAME_DEFAULT)
    val SAVE_AS_PROFILE                           /**/ by hotkey("R,P",
                                                                 KeybindSettings.GUI_DEFAULT)

        .CATEGORY("$category.auto_refill")
    val AUTO_REFILL_GUI_TOGGLE_FOR_SLOT           /**/ by hotkey("LEFT_CONTROL,BUTTON_1",
                                                                 KeybindSettings.ANY_DEFAULT)
    val AUTO_REFILL_GAME_TOGGLE_FOR_SLOT          /**/ by hotkey("LEFT_CONTROL,R",
                                                                 KeybindSettings.INGAME_DEFAULT.copy(allowExtraKeys = true, orderSensitive = false))

        .CATEGORY("$category.villager_trading")
        //.CATEGORY("§§hide")
    val GLOBAL_BOOKMARK_TRADE                     /**/ by hotkey("B",
                                                                 KeybindSettings.GUI_DEFAULT)
    val LOCAL_BOOKMARK_TRADE                      /**/ by hotkey("L",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DO_GLOBAL_TRADE                           /**/ by hotkey("T",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DO_LOCAL_TRADE                            /**/ by hotkey("LEFT_SHIFT,T",
                                                                 KeybindSettings.GUI_DEFAULT)

    val GLOBAL_BOOKMARK_TRADE1                    /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val LOCAL_BOOKMARK_TRADE1                     /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DO_GLOBAL_TRADE1                          /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DO_LOCAL_TRADE1                           /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)

    val GLOBAL_BOOKMARK_TRADE2                     /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val LOCAL_BOOKMARK_TRADE2                      /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DO_GLOBAL_TRADE2                           /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)
    val DO_LOCAL_TRADE2                            /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)

        .CATEGORY("$category.misc")
    val DUMP_ITEM_NBT_TO_CHAT                     /**/ by hotkey("",
                                                                 KeybindSettings.GUI_DEFAULT)

    val COPY_COMPONENTS                           /**/ by hotkey("LEFT_CONTROL,C",
                                                                 KeybindSettings.GUI_DEFAULT)

    val COPY_ITEM_ID                              /**/ by hotkey("LEFT_CONTROL,LEFT_SHIFT,C",
                                                                 KeybindSettings(KeybindSettings.Context.GUI,
                                                                                 KeybindSettings.KeyAction.PRESS,
                                                                                 allowExtraKeys = false,
                                                                                 orderSensitive = false))
}

object Tweaks : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.client_side_tweaks")
        .CATEGORY("§§vgap:-5")
        .CATEGORY("$category.inventory")
    val PREVENT_CLOSE_GUI_DROP_ITEM               /**/ by keyToggleBool(false)
    val CONTAINER_SWIPE_MOVING_ITEMS              /**/ by keyToggleBool(true)
    val SWIPE_MOVE_CRAFTING_RESULT_SLOT           /**/ by keyToggleBool(false)

}

object Debugs : ConfigDeclaration {
    override val builder = createBuilder()

        .CATEGORY("$category.debugs")
    val TRACE_LOGS                                /**/ by bool(false)
    val DEBUG_RENDER                              /**/ by bool(false)
    val FORCE_NO_CLEAN_CURSOR                     /**/ by bool(false)
    val FORCE_SERVER_METHOD_FOR_LOCKED_SLOTS      /**/ by bool(false)
    val DIFF_CALCULATOR                           /**/ by enum(DiffCalculatorType.SIMPLE)
    val DEBUG_SCREEN                              /**/ by hotkey("Z,1",
                                                                 KeybindSettings.ANY_DEFAULT)
    val SCREEN_DEPTH_TEST                         /**/ by hotkey("Z,2",
                                                                 KeybindSettings.ANY_DEFAULT)
    val SCREEN_SPRITE_TEST                        /**/ by hotkey("Z,3",
                                                                 KeybindSettings.ANY_DEFAULT)
    val CLEAN_CURSOR                              /**/ by hotkey("X,1",
                                                                 KeybindSettings.GUI_DEFAULT)
    val GEN_TAG_VANILLA_TXT                       /**/ by button(GenerateTagVanillaTxtButtonInfo)
    val GEN_RULE_LIST                             /**/ by button(GenerateRuleListButtonInfo)
    //val DO_VERSION_CHECK                          /**/ by button(IPNInfoManager.DoVersionCheckButtonInfo)
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
    ScrollSettings,
    Hotkeys,
    Tweaks,
    Debugs,
    Modpacks
)

object SaveLoadManager : Savable by ConfigSaveLoadManager(Configs.toMultiConfig(),
                                                          FILE_PATH)
