package io.github.jsnimda.inventoryprofiles.item.rule.native

import io.github.jsnimda.common.extensions.ByPropertyName
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.item.rule.MutableEmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Parameter
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.*
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.NumberOrder.DESCENDING
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.RequireNbt.NOT_REQUIRED
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.StringCompare.UNICODE

// ============
// Some helper functions for creating rules
// ============

private class TypeBasedRuleProvider<T, R : TypeBasedRule<T>>(
  supplier: () -> R,
  valueOf: (Rule.(ItemType) -> T)? = null,
  val args: MutableList<Pair<Parameter<*>, Any?>> = mutableListOf(),
  val postActions: MutableList<R.() -> Unit> = mutableListOf()
) : ByPropertyName<() -> Rule>({ name ->
  { // return () -> Rule
    supplier().apply {
      valueOf?.let { this.valueOf = it }
      arguments.apply {
        args.forEach { (parameter, value) ->
          @Suppress("UNCHECKED_CAST")
          value?.let { defineParameter(parameter as Parameter<Any>, value) } ?: defineParameter(parameter)
        }
      }
      postActions.forEach { it() }
    }
  }.also { NATIVE_MAP[name] = it } // don't use NativeRules.map (un-init-ed)
}) {
  fun <P : Any> param(parameter: Parameter<P>, value: P) = // custom param on specific rule
    this.also { args.add(parameter to value) }

  fun param(parameter: Parameter<*>) = // custom param on specific rule
    this.also { args.add(parameter to null) }

  fun post(postAction: R.() -> Unit) =
    this.also { postActions.add(postAction) }
}

private fun <T, R : TypeBasedRule<T>> type(
  supplier: () -> R,
  valueOf: (Rule.(ItemType) -> T)? = null
) = TypeBasedRuleProvider(supplier, valueOf)

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
val translation_key /**/ by type(::StringBasedRule) { it.translationKey }.param(string_compare, UNICODE)
val item_id         /**/ by type(::StringBasedRule) { it.itemId }    /**/.param(string_compare, UNICODE)
val potion_name     /**/ by type(::StringBasedRule) { it.potionName }/**/.param(string_compare, UNICODE)

// ============
// Number Type Rule
val raw_id                    /**/ by type(::NumberBasedRule) { it.rawId }
val creative_menu_group_index /**/ by type(::NumberBasedRule) { it.groupIndex }
val damage                    /**/ by type(::NumberBasedRule) { it.damage }
val max_damage                /**/ by type(::NumberBasedRule) { it.maxDamage }
val durability                /**/ by type(::NumberBasedRule) { it.durability }
val enchantments_score        /**/ by type(::NumberBasedRule) { it.enchantmentsScore }.param(number_order, DESCENDING)

// ============
// Boolean Type Rule
val has_custom_name           /**/ by type(::BooleanBasedRule) { it.hasCustomName }
val is_damageable             /**/ by type(::BooleanBasedRule) { it.isDamageable }
val match_nbt                 /**/ by rule(::MatchNbtRule)
val is_tag                    /**/ by type(::MatchNbtRule).param(tag_name).param(require_nbt, NOT_REQUIRED)
  .post { andValue { arguments[require_nbt].match(it) && arguments[tag_name].match(it) } }
val is_item                   /**/ by type(::MatchNbtRule).param(item_name).param(require_nbt, NOT_REQUIRED)
  .post { andValue { arguments[require_nbt].match(it) && arguments[item_name].match(it) } }
//val has_custom_potion_effects /**/ by type(::BooleanBasedRule) { it.hasCustomPotionEffects }
//val has_potion_effects        /**/ by type(::BooleanBasedRule) { it.hasPotionEffects }

// ============
// Other
val by_nbt         /**/ by rule(::ByNbtRule)
val nbt_comparator /**/ by rule(::NbtComparatorRule)
val potion_effect  /**/ by rule(::PotionEffectRule)
