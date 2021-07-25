package org.anti_ad.mc.ipnext.item.rule.vnative

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.asComparable
import org.anti_ad.mc.common.extensions.compareTo
import org.anti_ad.mc.common.extensions.letIf
import org.anti_ad.mc.common.util.LogicalStringComparator
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.NbtUtils
import org.anti_ad.mc.ipnext.item.comparablePotionEffects
import org.anti_ad.mc.ipnext.item.rule.BaseRule
import org.anti_ad.mc.ipnext.item.rule.EmptyRule
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.rule.parameter.*
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
            defineParameter(blank_string,
                            Match.LAST)
            defineParameter(string_compare,
                            StringCompare.LOCALE)
            defineParameter(locale,
                            "mc")
            defineParameter(strength,
                            Strength.PRIMARY)
            defineParameter(logical,
                            true)
        }
        comparator = { a, b ->
            compareString(valueOf(a),
                          valueOf(b))
        }
    }

    private val lazyCompareString: Comparator<in String> by lazy(LazyThreadSafetyMode.NONE) {
        val rawComparator: Comparator<in String> = arguments[string_compare].comparator ?: run { // locale cmp
            val langTag = arguments[locale].letIf({ it == "mc" }) { VanillaUtil.languageCode() }.replace('_',
                                                                                                         '-')
            val locale = if (langTag == "sys") Locale.getDefault() else Locale.forLanguageTag(langTag)
            val strength = arguments[strength].value
            Collator.getInstance(locale).apply { this.strength = strength }
        }
        return@lazy rawComparator.letIf(arguments[logical]) { LogicalStringComparator(it) }
    } // interestingly if using if else, compiler cannot guess type

    fun compareString(str1: String,
                      str2: String): Int {
        return compareByMatch(str1,
                              str2,
                              { it.isBlank() },
                              arguments[blank_string],
                              lazyCompareString::compare)
    }
}

class NumberBasedRule : TypeBasedRule<Number>() {
    override var valueOf: Rule.(ItemType) -> Number = { 0 }

    init {
        arguments.defineParameter(number_order,
                                  NumberOrder.ASCENDING)
        comparator = { a, b ->
            compareNumber(valueOf(a),
                          valueOf(b))
        }
    }

    fun compareNumber(num1: Number,
                      num2: Number) =
        arguments[number_order].compare(num1,
                                        num2)
}

open class BooleanBasedRule : TypeBasedRule<Boolean>() {
    override var valueOf: Rule.(ItemType) -> Boolean = { false } // matchBy

    init {
        arguments.apply {
            defineParameter(match,
                            Match.FIRST)
            defineParameter(sub_rule_match,
                            EmptyRule)
            defineParameter(sub_rule_not_match,
                            EmptyRule)
        }
        comparator = { itemType1, itemType2 ->
            compareByMatchSeparate(
                itemType1,
                itemType2,
                { valueOf(it) },
                arguments[match],
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
            defineParameter(nbt,
                            NbtCompound())
            defineParameter(allow_extra,
                            true)
        }
        valueOf = {
            if (arguments[allow_extra]) {
                NbtUtils.matchNbt(arguments[nbt],
                                  it.tag)
            } else {
                NbtUtils.matchNbtNoExtra(arguments[nbt],
                                         it.tag)
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
        bothSameCompare(value1,
                        value2)
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
    return compareByMatchSeparate(value1,
                                  value2,
                                  matchBy(value1),
                                  matchBy(value2),
                                  match,
                                  matchCompare,
                                  notMatchCompare)
}

inline fun <T> compareByMatchSeparate(
    value1: T,
    value2: T,
    b1: Boolean,
    b2: Boolean,
    match: Match,
    matchCompare: (T, T) -> Int = { _, _ -> 0 }, // both match
    notMatchCompare: (T, T) -> Int = { _, _ -> 0 } // both not match
): Int {
    return if (b1 == b2) {
        if (b1) matchCompare(value1,
                             value2)
        else notMatchCompare(value1,
                             value2)
    } else { // b1 != b2
        match.multiplier * if (b1) -1 else 1
    }
}

// ============
// other native rules
// ============
class ByNbtRule : NativeRule() {
    val dummyStringRule = StringBasedRule()
    val dummyNumberRule = NumberBasedRule()
    val dummies = listOf(dummyStringRule,
                         dummyNumberRule)

    init {
        arguments.apply {
            defineParameter(nbt_path)
            defineParameter(not_found,
                            Match.LAST)

            dummies.forEach { defineParametersFrom(it.arguments) }

            comparator = ::compareByNbtPath
        }
    }

    var initialized = false
    fun initializeDummyArguments() {
        if (initialized) return
        initialized = true
        dummies.forEach { it.arguments.setArgumentsFrom(arguments) }
    }

    fun compareByNbtPath(itemType1: ItemType,
                         itemType2: ItemType): Int {
        initializeDummyArguments()
        return ByNbtPathComparator(itemType1,
                                   itemType2).compare()
    }

    inner class ByNbtPathComparator(val itemType1: ItemType,
                                    val itemType2: ItemType) {
        fun compare(): Int {
            val tags1 = arguments[nbt_path].getTags(itemType1)
            val tags2 = arguments[nbt_path].getTags(itemType2)
            if (tags1.size > 1 || tags2.size > 1)
                Log.warn("given nbt path produce more than one result. currently support only the first result")
            val tag1 = tags1.firstOrNull()
            val tag2 = tags2.firstOrNull()
            val b1 = tag1 == null
            val b2 = tag2 == null
            if (b1 != b2) {
                return arguments[not_found].multiplier * if (b1) -1 else 1
            }
            if (tag1 == null || tag2 == null) return 0 // both not found // use || tag2 == null for smart cast
            return compareTag(tag1,
                              tag2)
        }

        fun compareTag(tag1: NbtUtils.WrappedTag,
                       tag2: NbtUtils.WrappedTag): Int {
            return when {
                tag1.isNumber -> if (tag2.isNumber) compareAsNumber(tag1,
                                                                    tag2) else null
                tag1.isCompound -> if (tag2.isCompound) compareAsCompound(tag1,
                                                                          tag2) else null
                tag1.isList -> if (tag2.isList) compareAsList(tag1,
                                                              tag2) else null
                else -> null
            } ?: compareAsString(tag1,
                                 tag2)
        }

        fun compareAsString(tag1: NbtUtils.WrappedTag,
                            tag2: NbtUtils.WrappedTag): Int {
            return dummyStringRule.compareString(tag1.asString,
                                                 tag2.asString)
        }

        fun compareAsNumber(tag1: NbtUtils.WrappedTag,
                            tag2: NbtUtils.WrappedTag): Int {
            return dummyNumberRule.compareNumber(tag1.asNumber,
                                                 tag2.asNumber)
        }

        fun compareAsCompound(tag1: NbtUtils.WrappedTag,
                              tag2: NbtUtils.WrappedTag): Int {
            return NbtUtils.compareNbt(tag1.asCompound,
                                       tag2.asCompound)
        }

        fun compareAsList(tag1: NbtUtils.WrappedTag,
                          tag2: NbtUtils.WrappedTag): Int {
            val list1 = tag1.asList.map { it.asComparable(::compareTag) }
            val list2 = tag2.asList.map { it.asComparable(::compareTag) }
            return list1.compareTo(list2)
        }
    }
}

class NbtComparatorRule : NativeRule() {
    init {
        comparator = { a, b -> // compare a.tag and b.tag
            NbtUtils.compareNbt(a.tag,
                                b.tag)
        }
    }
}

class PotionEffectRule : NativeRule() {
    init {
        comparator = { a, b -> // compare a.tag and b.tag
            a.comparablePotionEffects.compareTo(b.comparablePotionEffects)
        }
    }
}