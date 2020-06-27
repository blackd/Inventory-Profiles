package io.github.jsnimda.inventoryprofiles.item.rule.native

import io.github.jsnimda.common.util.ByPropertyName
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Parameter
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.Match
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.NumberOrder.DESCENDING
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.StringCompare.UNICODE
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.has_custom_name
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.has_potion_name
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.number_order
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.string_compare

// ============
// Some helper functions for creating rules
// ============
//private class TypedRuleProvider<T>(
//  private val supplier: () -> TypeBasedRule<T>,
//  private val valueOf: (ItemType) -> T
//) {
//  private val args = mutableListOf<Pair<Parameter<Any>, Any>>() // additional param
//  private val postActions = mutableListOf<TypeBasedRule<T>.() -> Unit>()
//  fun <P : Any> param(parameter: Parameter<P>, value: P) = // custom param on specific rule
//    this.also { @Suppress("UNCHECKED_CAST") args.add(parameter as Parameter<Any> to value as Any) }
//
//  fun post(postAction: TypeBasedRule<T>.() -> Unit) =
//    this.also { postActions.add(postAction) }
//
//  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): () -> Rule = {
//    supplier().apply {
//      this.valueOf = this@TypedRuleProvider.valueOf
//      arguments.apply { args.forEach { defineParameter(it.first, it.second) } }
//      postActions.forEach { it() }
//    }
//  }.also { NATIVE_RULES_MAP[property.name] = it }
//}
private class TypeBasedRuleProvider<T>(
  supplier: () -> TypeBasedRule<T>,
  valueOf: (ItemType) -> T,
  val args: MutableList<Pair<Parameter<Any>, Any>> = mutableListOf(),
  val postActions: MutableList<TypeBasedRule<T>.() -> Unit> = mutableListOf()
) : ByPropertyName<() -> Rule>({ name ->
  { // return () -> Rule
    supplier().apply {
      this.valueOf = valueOf
      arguments.apply { args.forEach { defineParameter(it.first, it.second) } }
      postActions.forEach { it() }
    }
  }.also { NATIVE_MAP[name] = it } // don't use NativeRules.map (un-init-ed)
}) {
  fun <P : Any> param(parameter: Parameter<P>, value: P) = // custom param on specific rule
    this.also { @Suppress("UNCHECKED_CAST") args.add(parameter as Parameter<Any> to value as Any) }

  fun post(postAction: TypeBasedRule<T>.() -> Unit) =
    this.also { postActions.add(postAction) }
}

private fun <T> type(
  supplier: () -> TypeBasedRule<T>,
  valueOf: (ItemType) -> T
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

val none by rule(::EmptyRule)

// ============
// String Typed Rule
val display_name    /**/ by type(::StringBasedRule) { it.displayName }
val custom_name     /**/ by type(::StringBasedRule) { it.customName }.param(has_custom_name, Match.FIRST)
  .post {
    val defaultInnerCompare = comparator
    comparator = { itemType1, itemType2 ->
      compareBoolean(itemType1, itemType2, { it.hasCustomName }, arguments[has_custom_name], defaultInnerCompare)
    }
  }
val translated_name /**/ by type(::StringBasedRule) { it.translatedName }
val translation_key /**/ by type(::StringBasedRule) { it.translationKey }.param(string_compare, UNICODE)
val item_id         /**/ by type(::StringBasedRule) { it.itemId }    /**/.param(string_compare, UNICODE)
val potion_name     /**/ by type(::StringBasedRule) { it.potionName }/**/.param(string_compare, UNICODE)
  .param(has_potion_name, Match.FIRST).post {
    val defaultInnerCompare = comparator
    comparator = { itemType1, itemType2 ->
      compareBoolean(itemType1, itemType2, { it.hasPotionName }, arguments[has_potion_name], defaultInnerCompare)
    }
  }

// ============
// Number Typed Rule
val raw_id                    /**/ by type(::NumberBasedRule) { it.rawId }
val creative_menu_group_index /**/ by type(::NumberBasedRule) { it.groupIndex }
val damage                    /**/ by type(::NumberBasedRule) { it.damage }
val enchantments_score        /**/ by type(::NumberBasedRule) { it.enchantmentsScore }.param(number_order, DESCENDING)

// ============
// Boolean Typed Rule
val has_custom_name           /**/ by type(::BooleanBasedRule) { it.hasCustomName }
val has_nbt                   /**/ by type(::BooleanBasedRule) { it.tag != null }
val is_tag                    /**/ by type(::BooleanBasedRule) { false/*TODO*/ }
val is_item                   /**/ by type(::BooleanBasedRule) { false/*TODO*/ }
val has_custom_potion_effects /**/ by type(::BooleanBasedRule) { it.hasCustomPotionEffects }
val has_potion_effects        /**/ by type(::BooleanBasedRule) { it.hasPotionEffects }

// ============
// Other
val nbt            /**/ by rule(::NbtRule)
val by_nbt_path    /**/ by rule(::NbtPathRule)
val potion_effects /**/ by rule(::PotionEffectRule)
