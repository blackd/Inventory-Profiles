package io.github.jsnimda.inventoryprofiles.item.rule.native

import io.github.jsnimda.common.util.LogicalStringComparator
import io.github.jsnimda.common.util.orElse
import io.github.jsnimda.common.util.selfIf
import io.github.jsnimda.common.util.selfIfNotEquals
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.CompoundTag
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.NbtUtils
import io.github.jsnimda.inventoryprofiles.item.rule.BaseRule
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.*
import java.text.Collator
import java.util.*

abstract class NativeRule : BaseRule()

abstract class TypeBasedRule<T> : NativeRule() {
  abstract var valueOf: Rule.(ItemType) -> T
}

class StringBasedRule : TypeBasedRule<String>() {
  override var valueOf: Rule.(ItemType) -> String = { "" }

  init {
    arguments.apply {
      defineParameter(blank_string, Match.LAST)
      defineParameter(string_compare, StringCompare.LOCALE)
      defineParameter(locale, "mc")
      defineParameter(strength, Strength.PRIMARY)
      defineParameter(logical, true)
    }
    comparator = { a, b -> compareString(valueOf(a), valueOf(b)) }
  }

  private val lazyCompareString: Comparator<in String> by lazy(LazyThreadSafetyMode.NONE) {
    val rawComparator: Comparator<in String> = arguments[string_compare].comparator ?: run { // locale cmp
      val langTag = arguments[locale].selfIfNotEquals("mc") { VanillaUtil.languageCode() }.replace('_', '-')
      val locale = if (langTag == "sys") Locale.getDefault() else Locale.forLanguageTag(langTag)
      val strength = arguments[strength].value
      Collator.getInstance(locale).apply { this.strength = strength }
    }
    return@lazy rawComparator.selfIf { !arguments[logical] orElse { LogicalStringComparator(rawComparator) } }
  } // interestingly if using if else, compiler cannot guess type

  private fun compareString(str1: String, str2: String): Int {
    return compareByMatch(str1, str2, { it.isBlank() }, arguments[blank_string], lazyCompareString::compare)
  }
}

class NumberBasedRule : TypeBasedRule<Number>() {
  override var valueOf: Rule.(ItemType) -> Number = { 0 }

  init {
    arguments.defineParameter(number_order, NumberOrder.ASCENDING)
    comparator = { a, b -> compareNumber(valueOf(a), valueOf(b)) }
  }

  private fun compareNumber(num1: Number, num2: Number) =
    arguments[number_order].compare(num1, num2)
}

open class BooleanBasedRule : TypeBasedRule<Boolean>() {
  override var valueOf: Rule.(ItemType) -> Boolean = { false } // matchBy

  init {
    arguments.apply {
      defineParameter(match, Match.FIRST)
      defineParameter(sub_rule_match, EmptyRule)
      defineParameter(sub_rule_not_match, EmptyRule)
    }
    comparator = { itemType1, itemType2 ->
      compareByMatchSeparate(
        itemType1, itemType2, { valueOf(it) }, arguments[match],
        arguments[sub_rule_match]::compare,
        arguments[sub_rule_not_match]::compare
      )
    }
  }

  fun andValue(extraValue: Rule.(ItemType) -> Boolean) {
    val oldValueOf = valueOf
    valueOf = { extraValue(it) && oldValueOf(it) }
  }
}

class MatchNbtRule : BooleanBasedRule() {
  init {
    arguments.apply {
      defineParameter(nbt, CompoundTag())
      defineParameter(allow_extra, true)
    }
    valueOf = {
      if (arguments[allow_extra]) {
        NbtUtils.matchNbt(arguments[nbt], it.tag)
      } else {
        NbtUtils.matchNbtNoExtra(arguments[nbt], it.tag)
      }
    }
  }
}

inline fun <T> compareByMatch(
  value1: T,
  value2: T,
  matchBy: (T) -> Boolean,
  match: Match,
  bothSameCompare: (T, T) -> Int = { _, _ -> 0 }
): Int {
  val b1 = matchBy(value1)
  val b2 = matchBy(value2)
  return if (b1 == b2) {
    bothSameCompare(value1, value2)
  } else { // b1 != b2
    match.multiplier * if (b1) -1 else 1
  }
}

inline fun <T> compareByMatchSeparate(
  value1: T,
  value2: T,
  matchBy: (T) -> Boolean,
  match: Match,
  matchCompare: (T, T) -> Int = { _, _ -> 0 }, // both match
  notMatchCompare: (T, T) -> Int = { _, _ -> 0 } // both not match
): Int {
  val b1 = matchBy(value1)
  val b2 = matchBy(value2)
  return if (b1 == b2) {
    if (b1) matchCompare(value1, value2)
    else notMatchCompare(value1, value2)
  } else { // b1 != b2
    match.multiplier * if (b1) -1 else 1
  }
}

// ============
// other native rules
// ============
class NbtComparatorRule : NativeRule() {
  init {
    comparator = { a, b -> // compare a.tag and b.tag
      NbtUtils.compareNbt(a.tag, b.tag)
    }
  }
}

class ByNbtRule : NativeRule()

class PotionEffectRule : NativeRule()
