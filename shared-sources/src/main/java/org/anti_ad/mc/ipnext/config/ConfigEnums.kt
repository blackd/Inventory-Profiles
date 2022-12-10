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

package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.common.vanilla.alias.glue.I18n



private const val ENUM = "inventoryprofiles.enum"

enum class SortingMethod(val ruleName: String?) {
    DEFAULT("default"),
    ITEM_NAME("item_name"),
    ITEM_ID("item_id"),
    RAW_ID("raw_id"),
    CUSTOM(null);

    override fun toString(): String =
        I18n.translate("$ENUM.sorting_method.${name.lowercase()}")
}

enum class SortingMethodIndividual {
    GLOBAL,
    DEFAULT,
    ITEM_NAME,
    ITEM_ID,
    RAW_ID,
    CUSTOM;

    override fun toString(): String =
        if (this == GLOBAL) {
            val v = ModSettings.SORT_ORDER.value
            val tostr = v.toString()
            I18n.translate("$ENUM.sorting_method.global",
                           tostr.substringBefore('(').trim())
        } else {
            I18n.translate("$ENUM.sorting_method.${name.lowercase()}")
        }
}

enum class PostAction {
    NONE,
    GROUP_IN_ROWS,
    GROUP_IN_COLUMNS,
    DISTRIBUTE_EVENLY,
    SHUFFLE,
    FILL_ONE,
    REVERSE;

    override fun toString(): String =
        I18n.translate("$ENUM.post_action.${name.lowercase()}")
}


enum class ToolReplaceVisualNotification {
    SUBTITLE,
    HOTBAR,
    CHAT;
    override fun toString(): String =
            I18n.translate("$ENUM.tool_replace_visual_notification.${name.lowercase()}")
}
enum class ThresholdUnit {
    ABSOLUTE,
    PERCENTAGE;

    override fun toString(): String =
        I18n.translate("$ENUM.threshold_unit.${name.lowercase()}")
}

enum class ContinuousCraftingCheckboxValue {
    REMEMBER,
    CHECKED,
    UNCHECKED;

    override fun toString(): String =
        I18n.translate("$ENUM.continuous_crafting_checkbox_value.${name.lowercase()}")
}

enum class SwitchType {
    TOGGLE,
    HOLD;

    override fun toString(): String =
        I18n.translate("$ENUM.switch_type.${name.lowercase()}")
}

enum class DiffCalculatorType {
    SIMPLE,
    SCORE_BASED_SINGLE,
    SCORE_BASED_DUAL,
    ;

//  override fun toString(): String =
//    I18n.translate("$ENUM.diff_calculator_type.${name.toLowerCase()}")
}

enum class AutoRefillNbtMatchType {
    EXACT,
    CAN_HAVE_EXTRA;

    override fun toString(): String =
            I18n.translate("$ENUM.auto_refill_nbt_match_type.${name.lowercase()}")
}

enum class CreativeMenuSortOrder {
    SEARCH_TAB,
    CATEGORY_PRIORITY_LIST;

    override fun toString(): String =
            I18n.translate("$ENUM.creative_menu_sort_order.${name.lowercase()}")
}
