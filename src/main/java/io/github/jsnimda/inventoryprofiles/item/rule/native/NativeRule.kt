package io.github.jsnimda.inventoryprofiles.item.rule.native

import io.github.jsnimda.common.util.LogicalStringComparator
import io.github.jsnimda.common.util.orElse
import io.github.jsnimda.common.util.selfIf
import io.github.jsnimda.common.util.selfIfNotEquals
import io.github.jsnimda.common.vanilla.VanillaUtils
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.NbtUtils
import io.github.jsnimda.inventoryprofiles.item.rule.BaseRule
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.*
import java.text.Collator
import java.util.*

abstract class NativeRule : BaseRule()

abstract class TypeBasedRule<T> : NativeRule() {
  abstract var valueOf: (ItemType) -> T
}

class StringBasedRule : TypeBasedRule<String>() {
  override var valueOf: (ItemType) -> String = { "" }

  init {
    arguments.apply {
      defineParameter(string_compare, StringCompare.LOCALE)
      defineParameter(locale, "mc")
      defineParameter(strength, Strength.PRIMARY)
      defineParameter(logical, true)
    }
    comparator = { a, b -> compareString(valueOf(a), valueOf(b)) }
  }

  private val lazyCompareString: Comparator<in String> by lazy(LazyThreadSafetyMode.NONE) {
    val rawComparator: Comparator<in String> = arguments[string_compare].comparator ?: run { // locale cmp
      val langTag = arguments[locale].selfIfNotEquals("mc") { VanillaUtils.languageCode() }.replace('_', '-')
      val locale = if (langTag == "sys") Locale.getDefault() else Locale.forLanguageTag(langTag)
      val strength = arguments[strength].value
      Collator.getInstance(locale).apply { this.strength = strength }
    }
    return@lazy rawComparator.selfIf { !arguments[logical] orElse { LogicalStringComparator(rawComparator) } }
  } // interestingly if using if else, compiler cannot guess type

  private fun compareString(str1: String, str2: String): Int {
    return lazyCompareString.compare(str1, str2)
//    val rawComparator: Comparator<in String> = arguments[string_compare].comparator ?: run { // locale cmp
//      val langTag = arguments[locale].selfIfNotEquals("mc") { VanillaUtils.languageCode() }.replace('_', '-')
//      val locale = if (langTag == "sys") Locale.getDefault() else Locale.forLanguageTag(langTag)
//      val strength = arguments[strength].value
//      Collator.getInstance(locale).apply { this.strength = strength }
//    }
//    val comparator: Comparator<in String> =
//      if (arguments[logical]) LogicalStringComparator(rawComparator) else rawComparator
//    return comparator.compare(str1, str2)
  }
}

class NumberBasedRule : TypeBasedRule<Number>() {
  override var valueOf: (ItemType) -> Number = { 0 }

  init {
    arguments.defineParameter(number_order, NumberOrder.ASCENDING)
    comparator = { a, b -> compareNumber(valueOf(a), valueOf(b)) }
  }

  private fun compareNumber(num1: Number, num2: Number) =
    arguments[number_order].compare(num1, num2)
}

class BooleanBasedRule : TypeBasedRule<Boolean>() {
  override var valueOf: (ItemType) -> Boolean = { false } // matchBy

  init {
    arguments.apply {
      defineParameter(match, Match.FIRST)
      defineParameter(sub_comparator_match, EmptyRule())
      defineParameter(sub_comparator_not_match, EmptyRule())
    }
    comparator = { itemType1, itemType2 ->
      compareBoolean(
        itemType1, itemType2, valueOf, arguments[match],
        arguments[sub_comparator_match]::compare,
        arguments[sub_comparator_not_match]::compare
      )
    }
  }
}

fun compareBoolean(
  itemType1: ItemType,
  itemType2: ItemType,
  matchBy: (ItemType) -> Boolean,
  match: Match,
  matchCompare: (ItemType, ItemType) -> Int = { _, _ -> 0 },
  notMatchCompare: (ItemType, ItemType) -> Int = { _, _ -> 0 }
): Int {
  val b1 = matchBy(itemType1)
  val b2 = matchBy(itemType2)
  return if (b1 == b2) {
    if (b1) matchCompare(itemType1, itemType2)
    else notMatchCompare(itemType1, itemType2)
  } else { // b1 != b2
    match.multiplier * if (b1) -1 else 1
  }
}

// ============
// other native rules
// ============
class NbtRule : NativeRule() {
  init {
    comparator = { a, b -> // compare a.tag and b.tag
      NbtUtils.compareNbt(a.tag, b.tag)
    }
  }
}

class NbtPathRule : NativeRule()

class PotionEffectRule : NativeRule()
