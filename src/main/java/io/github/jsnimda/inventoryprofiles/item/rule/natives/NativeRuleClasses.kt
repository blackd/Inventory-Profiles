package io.github.jsnimda.inventoryprofiles.item.rule.natives

import io.github.jsnimda.common.util.LogicalStringComparator
import io.github.jsnimda.common.vanilla.VanillaUtils
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.BaseRule
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.*
import java.text.Collator
import java.util.*

abstract class NativeRule : BaseRule()

abstract class TypedRule<T> : NativeRule() {
  abstract var transformBy: (ItemType) -> T
}

class StringTypedRule : TypedRule<String>() {
  override var transformBy: (ItemType) -> String = { "" }

  init {
    arguments.apply {
      defineParameter(string_compare, StringCompare.LOCALE)
      defineParameter(locale, "mc")
      defineParameter(strength, Strength.PRIMARY)
      defineParameter(logical, true)
    }
    innerCompare = { a, b -> compareString(transformBy(a), transformBy(b)) }
  }

  private fun compareString(str1: String, str2: String): Int {
    val rawComparator: Comparator<in String> = arguments[string_compare].comparator ?: run {
      val langTag = arguments[locale].let {
        (if (it == "mc") VanillaUtils.languageCode() else it).replace('_', '-')
      }
      val locale = if (langTag == "sys") Locale.getDefault() else Locale.forLanguageTag(langTag)
      val strength = arguments[strength].value
      Collator.getInstance(locale).apply {
        this.strength = strength
      }
    }
    val comparator: Comparator<in String> =
      if (arguments[logical]) LogicalStringComparator(rawComparator) else rawComparator
    return comparator.compare(str1, str2)
  }
}

class NumberTypedRule : TypedRule<Number>() {
  override var transformBy: (ItemType) -> Number = { 0 }

  init {
    arguments.defineParameter(number_order, NumberOrder.ASCENDING)
    innerCompare = { a, b -> compareNumber(transformBy(a), transformBy(b)) }
  }

  private fun compareNumber(num1: Number, num2: Number) =
    arguments[number_order].compare(num1, num2)
}

class BooleanTypedRule : TypedRule<Boolean>() {
  override var transformBy: (ItemType) -> Boolean = { false } // matchBy

  init {
    arguments.apply {
      defineParameter(match, Match.FIRST)
      defineParameter(sub_comparator_match, EmptyRule())
      defineParameter(sub_comparator_not_match, EmptyRule())
    }
    innerCompare = { itemType1, itemType2 ->
      compareBoolean(
        itemType1, itemType2, transformBy, arguments[match],
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
