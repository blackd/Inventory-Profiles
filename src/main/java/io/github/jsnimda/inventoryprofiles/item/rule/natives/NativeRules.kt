@file:Suppress("Reformat")

package io.github.jsnimda.inventoryprofiles.item.rule.natives

import io.github.jsnimda.inventoryprofiles.item.displayName
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Rule

val NATIVE_RULES_MAP = mutableMapOf<String, () -> Rule>()
val none by native(::EmptyRule)

val display_name    by typed(::StringTypedRule) { it.displayName }
val custom_name     by typed(::StringTypedRule) { TODO() }
val translated_name by typed(::StringTypedRule) { TODO() }
val item_id         by typed(::StringTypedRule) { TODO() }

val raw_id                    by typed(::NumberTypedRule) { TODO() }
val creative_menu_group_index by typed(::NumberTypedRule) { TODO() }
val damage                    by typed(::NumberTypedRule) { TODO() }
val enchantments_score        by typed(::NumberTypedRule) { TODO() }

val has_custom_name           by typed(::BooleanTypedRule) { TODO() }
val has_custom_potion_effects by typed(::BooleanTypedRule) { TODO() }
val has_potion_effects        by typed(::BooleanTypedRule) { TODO() }
val has_nbt                   by typed(::BooleanTypedRule) { TODO() }
val is_tag                    by typed(::BooleanTypedRule) { TODO() }
val is_item                   by typed(::BooleanTypedRule) { TODO() }

val nbt            by native(TODO())
val by_nbt         by native(TODO())
val potion_effects by native(TODO())
val potion_name    by native(TODO())
