@file:Suppress("Reformat")

package io.github.jsnimda.inventoryprofiles.item.rule.natives

import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.*
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.NumberOrder.DESCENDING
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.StringCompare.UNICODE
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.has_custom_name

val NATIVE_RULES_MAP = mutableMapOf<String, () -> Rule>()
val none by native(::EmptyRule)

val display_name    by typed(::StringTypedRule) { it.displayName }
val custom_name     by typed(::StringTypedRule) { it.customName }.param(has_custom_name, Match.FIRST) {
  val defaultInnerCompare = innerCompare
  innerCompare = { itemType1, itemType2 ->
    compareBoolean(itemType1, itemType2, { it.hasCustomName }, arguments[has_custom_name], defaultInnerCompare)
  }
}
val translated_name by typed(::StringTypedRule) { it.translatedName }
val translation_key by typed(::StringTypedRule) { it.translationKey }.param(string_compare, UNICODE)
val item_id         by typed(::StringTypedRule) { it.itemId }        .param(string_compare, UNICODE)

val raw_id                    by typed(::NumberTypedRule) { it.rawId }
val creative_menu_group_index by typed(::NumberTypedRule) { it.groupIndex }
val damage                    by typed(::NumberTypedRule) { it.damage }
val enchantments_score        by typed(::NumberTypedRule) { it.enchantmentsScore }.param(number_order, DESCENDING)

val has_custom_name           by typed(::BooleanTypedRule) { it.hasCustomName }
val has_custom_potion_effects by typed(::BooleanTypedRule) { it.hasCustomPotionEffects }
val has_potion_effects        by typed(::BooleanTypedRule) { it.hasPotionEffects }
val has_nbt                   by typed(::BooleanTypedRule) { it.tag != null }
val is_tag                    by typed(::BooleanTypedRule) { TODO() }
val is_item                   by typed(::BooleanTypedRule) { TODO() }

val nbt            by rule(TODO())
val by_nbt         by rule(TODO())
val potion_effects by rule(TODO())
val potion_name    by rule(TODO())
