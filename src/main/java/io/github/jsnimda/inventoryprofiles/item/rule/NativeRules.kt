package io.github.jsnimda.inventoryprofiles.item.rule

import io.github.jsnimda.common.util.LogicalStringComparator
import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.locale
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.logical
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.match
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.number_order
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.strength
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.string_compare
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.sub_comparator_match
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.sub_comparator_not_match
import java.text.Collator
import java.util.*
import kotlin.reflect.KProperty

object NativeRules {
  val MAP = mutableMapOf<String, () -> NativeRule>()
  val none by native(::NoneRule)
}

abstract class NativeRule(val name: String) : Rule()

class NoneRule : NativeRule("none") { // sub_comparator should not be set on this class (won't handle)
  override fun innerCompare(itemType1: ItemType, itemType2: ItemType): Int = 0
}

//region #?_type_comparator

abstract class StringTypeComparator(name: String) : NativeRule(name) {
  init {
    defineParameter(string_compare, StringCompare.LOCALE)
    defineParameter(locale, "mc")
    defineParameter(strength, Strength.PRIMARY)
    defineParameter(logical, true)
  }

  override fun innerCompare(itemType1: ItemType, itemType2: ItemType): Int =
    compareString(stringBy(itemType1), stringBy(itemType2))

  abstract fun stringBy(itemType: ItemType): String
  fun compareString(str1: String, str2: String): Int {
    val rawComparator = arguments[string_compare].comparator ?: run {
      val langTag = arguments[locale].let {
        (if (it == "mc") VanillaState.languageCode() else it).replace('_', '-')
      }
      val locale = if (langTag == "sys") Locale.getDefault() else Locale.forLanguageTag(langTag)
      val strength = arguments[strength].value
      Collator.getInstance(locale)!!.apply {
        this.strength = strength
      }
    }
    val comparator = if (arguments[logical]) LogicalStringComparator(rawComparator) else rawComparator
    return comparator.compare(str1, str2)
  }
}

abstract class NumberTypeComparator(name: String) : NativeRule(name) {
  init {
    defineParameter(number_order, NumberOrder.ASCENDING)
  }

  override fun innerCompare(itemType1: ItemType, itemType2: ItemType): Int =
    compareNumber(numberBy(itemType1), numberBy(itemType2))

  abstract fun numberBy(itemType: ItemType): Number
  fun compareNumber(num1: Number, num2: Number) =
    arguments[number_order].compare(num1, num2)
}

abstract class BooleanTypeComparator(name: String) : NativeRule(name) {
  init {
    defineParameter(match, Match.FIRST)
    defineParameter(sub_comparator_match, "::none")
    defineParameter(sub_comparator_not_match, "::none")
  }

  abstract fun matchBy(itemType: ItemType): Boolean
  override fun innerCompare(itemType1: ItemType, itemType2: ItemType): Int {
    val b1 = matchBy(itemType1)
    val b2 = matchBy(itemType2)
    return if (b1 == b2) {
      if (b1) arguments[sub_comparator_match].compare(itemType1, itemType2)
      else arguments[sub_comparator_not_match].compare(itemType1, itemType2)
    } else { // b1 != b2
      arguments[match].multiplier * if (b1) -1 else 1
    }
  }
}

//endregion

// ============
// Some helper functions for creating rules
// ============
private class NativeRuleProvider0(val supplier: () -> NativeRule) {
  operator fun provideDelegate(thisRef: NativeRules, property: KProperty<*>): () -> NativeRule =
    supplier.also { thisRef.MAP[property.name] = it }
}
private class NativeRuleProvider1(val supplier: (name: String) -> NativeRule) {
  operator fun provideDelegate(thisRef: NativeRules, property: KProperty<*>): () -> NativeRule =
    { supplier(property.name) }.also { thisRef.MAP[property.name] = it }
}

private operator fun (() -> NativeRule).getValue(thisRef: NativeRules, property: KProperty<*>) = this
private fun native(supplier: () -> NativeRule) = NativeRuleProvider0(supplier)
private fun native(supplier: (name: String) -> NativeRule) = NativeRuleProvider1(supplier)

