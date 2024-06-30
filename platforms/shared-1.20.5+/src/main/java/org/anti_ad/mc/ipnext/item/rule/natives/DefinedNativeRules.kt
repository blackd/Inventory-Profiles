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

package org.anti_ad.mc.ipnext.item.rule.natives

import org.anti_ad.mc.common.extensions.ByPropertyName
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.customName
import org.anti_ad.mc.ipnext.item.damage
import org.anti_ad.mc.ipnext.item.displayName
import org.anti_ad.mc.ipnext.item.durability
import org.anti_ad.mc.ipnext.item.`(enchantmentsScore)`
import org.anti_ad.mc.ipnext.item.groupIndex
import org.anti_ad.mc.ipnext.item.searchTabIndex
import org.anti_ad.mc.ipnext.item.hasCustomName
import org.anti_ad.mc.ipnext.item.isDamageable
import org.anti_ad.mc.ipnext.item.itemId
import org.anti_ad.mc.ipnext.item.maxDamage
import org.anti_ad.mc.ipnext.item.potionName
import org.anti_ad.mc.ipnext.item.rawId
import org.anti_ad.mc.ipnext.item.rule.MutableEmptyRule
import org.anti_ad.mc.ipnext.item.rule.Parameter
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.rule.parameter.NumberOrder.DESCENDING
import org.anti_ad.mc.ipnext.item.rule.parameter.RequireNbt.NOT_REQUIRED
import org.anti_ad.mc.ipnext.item.rule.parameter.StringCompare.UNICODE
import org.anti_ad.mc.ipnext.item.rule.parameter.item_name
import org.anti_ad.mc.ipnext.item.rule.parameter.number_order
import org.anti_ad.mc.ipnext.item.rule.parameter.require_nbt
import org.anti_ad.mc.ipnext.item.rule.parameter.string_compare
import org.anti_ad.mc.ipnext.item.rule.parameter.tag_name
import org.anti_ad.mc.ipnext.item.translatedName
import org.anti_ad.mc.ipnext.item.translationKey

// ============
// Some helper functions for creating rules
// ============

private class TypeBasedRuleProvider<T, R : TypeBasedRule<T>>(supplier: () -> R,
                                                             valueOf: (Rule.(ItemType) -> T)? = null,
                                                             val args: MutableList<Pair<Parameter<*>, Any?>> = mutableListOf(),
                                                             val postActions: MutableList<R.() -> Unit> = mutableListOf()) : ByPropertyName<() -> Rule>(
    { name -> { // return () -> Rule
        supplier().apply {
            valueOf?.let { this.valueOf = it }
            arguments.apply {
                args.forEach { (parameter, value) ->
                    @Suppress("UNCHECKED_CAST")
                    value?.let {
                        defineParameter(parameter as Parameter<Any>,
                                        value)
                    } ?: defineParameter(parameter)
                }
            }
            postActions.forEach { it() }
        }
    }.also { NATIVE_MAP[name] = it } // don't use NativeRules.map (un-init-ed)
    }) {

    fun <P : Any> param(parameter: Parameter<P>,
                        value: P) = // custom param on specific rule
        this.also { args.add(parameter to value) }

    fun param(parameter: Parameter<*>) = // custom param on specific rule
        this.also { args.add(parameter to null) }

    fun post(postAction: R.() -> Unit) =
        this.also { postActions.add(postAction) }
}

private fun <T, R : TypeBasedRule<T>> type(supplier: () -> R,
                                           valueOf: (Rule.(ItemType) -> T)? = null) = TypeBasedRuleProvider(supplier,
                                                                                                            valueOf)

private fun <R : Rule> rule(supplier: () -> R) = ByPropertyName<() -> R> { name ->
    supplier.also { NATIVE_MAP[name] = it }
}

// ============
// NativeRules
// ============
object NativeRules {
    val map = NATIVE_MAP // force load class
}

private val NATIVE_MAP = mutableMapOf<String, () -> Rule>()

val none by rule(::MutableEmptyRule)

// ============
// String Type Rule
val display_name    /**/ by type(::StringBasedRule) { it.displayName }
val custom_name     /**/ by type(::StringBasedRule) { it.customName }
val translated_name /**/ by type(::StringBasedRule) { it.translatedName }
val translation_key /**/ by type(::StringBasedRule) { it.translationKey }.param(string_compare,
                                                                                UNICODE)
val item_id         /**/ by type(::StringBasedRule) { it.itemId }    /**/.param(string_compare,
                                                                                UNICODE)
val potion_name     /**/ by type(::StringBasedRule) { it.potionName }/**/.param(string_compare,
                                                                                UNICODE)

// ============
// Number Type Rule
val raw_id                    /**/ by type(::NumberBasedRule) { it.rawId }
val creative_menu_group_index /**/ by type(::NumberBasedRule) { it.groupIndex }
val search_tab_index          /**/ by type(::NumberBasedRule) { it.searchTabIndex }
val damage                    /**/ by type(::NumberBasedRule) { it.damage }
val max_damage                /**/ by type(::NumberBasedRule) { it.maxDamage }
val durability                /**/ by type(::NumberBasedRule) { it.durability }
val enchantments_score        /**/ by type(::NumberBasedRule) { it.`(enchantmentsScore)` }.param(number_order,
                                                                                                 DESCENDING)
val accumulated_count         /**/ by type(::NumberBasedRule) { it.accumulatedCount }.param(number_order, DESCENDING)

// ============
// Boolean Type Rule
val has_custom_name           /**/ by type(::BooleanBasedRule) { it.hasCustomName }
val is_damageable             /**/ by type(::BooleanBasedRule) { it.isDamageable }
val component_match_nbt       /**/ by rule(::MatchNbtRule)
val is_tag                    /**/ by type(::MatchNbtRule).param(tag_name).param(require_nbt,
                                                                                 NOT_REQUIRED)
    .post { andValue { arguments[require_nbt].match(it) && arguments[tag_name].match(it) } }

val is_item                   /**/ by type(::SimpleParameterBasedRule).param(item_name).param(require_nbt,
                                                                                               NOT_REQUIRED)
    .post { andValue {
        val itemId = arguments[item_name]
        val rqMatch = arguments[require_nbt].match(it)
        val typeMatch = itemId.match(it)
        rqMatch && typeMatch
    } }

//val has_custom_potion_effects /**/ by type(::BooleanBasedRule) { it.hasCustomPotionEffects }
//val has_potion_effects        /**/ by type(::BooleanBasedRule) { it.hasPotionEffects }

// ============
// Other
val component_by_nbt         /**/ by rule(::ByNbtRule)
val component_nbt_comparator /**/ by rule(::NbtComparatorRule)
val potion_effect            /**/ by rule(::PotionEffectRule)
val components_comparator    /**/ by rule(::AllComponentsRule)
