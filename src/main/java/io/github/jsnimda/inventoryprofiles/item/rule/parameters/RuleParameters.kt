@file:Suppress("Reformat")

package io.github.jsnimda.inventoryprofiles.item.rule.parameters

import io.github.jsnimda.inventoryprofiles.item.rule.Parameter
import java.text.Collator

// for .* imports

val PARAMETERS_MAP = mutableMapOf<String, Parameter<*>>()
val string_compare           by enum<StringCompare>()
val locale                   by any_string
val strength                 by enum<Strength>()
val logical                  by type_boolean
val match                    by enum<Match>()
val has_custom_name          by parameterOf(match.argumentType)
val number_order             by enum<NumberOrder>()
val sub_comparator_match     by any_comparator
val sub_comparator_not_match by any_comparator
val nbt                      by any_nbt
val allow_extra              by type_boolean
val require_nbt              by enum<RequireNbt>()
val tag_name                 by any_tag_name
val item_name                by any_item_name
val nbt_path                 by any_nbt_path
val has_nbt_path             by parameterOf(match.argumentType)
val reverse                  by type_boolean
val sub_comparator           by any_comparator
val has_potion_name          by parameterOf(match.argumentType)
val has_potion_effects       by parameterOf(match.argumentType)

enum class StringCompare(val comparator: Comparator<in String>?) {
  UNICODE(naturalOrder()),
  IGNORE_CASE(String.CASE_INSENSITIVE_ORDER),
  LOCALE(null)
}

enum class Strength(val value: Int) {
  PRIMARY(Collator.PRIMARY),
  SECONDARY(Collator.SECONDARY),
  TERTIARY(Collator.TERTIARY),
  IDENTICAL(Collator.IDENTICAL)
}

enum class NumberOrder(private val comparator: Comparator<in Double>) : Comparator<Number> {
  ASCENDING(naturalOrder()),
  DESCENDING(reverseOrder());

  override fun compare(num1: Number, num2: Number): Int =
    comparator.compare(num1.toDouble(), num2.toDouble())
}

enum class Match(val multiplier: Int) {
  FIRST(1),
  LAST(-1)
}

enum class RequireNbt {
  REQUIRED,
  NO_NBT,
  NOT_REQUIRED
}

