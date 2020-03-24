package io.github.jsnimda.inventoryprofiles.item.rule

import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.reverse
import io.github.jsnimda.inventoryprofiles.item.rule.RuleParameters.sub_comparator

//fun Rule.stringArgumentOf(parameter: Parameter): String =
//  arguments.getValue(parameter.name)
//
//inline fun <reified T : Enum<T>> Rule.enumArgumentOf(parameter: Parameter): T =
//  enumValueOf<T>(stringArgumentOf(parameter).toUpperCase())
//
//fun Rule.booleanArgumentOf(parameter: Parameter): Boolean =
//  enumValueOf<Bool>(stringArgumentOf(parameter).toUpperCase()).value

abstract class Rule : Comparator<ItemType> {
  val arguments = Arguments()
  abstract fun innerCompare(itemType1: ItemType, itemType2: ItemType): Int
  final override fun compare(itemType1: ItemType, itemType2: ItemType): Int {
    val result = innerCompare(itemType1, itemType2)
    if (result != 0) return result * if (arguments[reverse]) -1 else 1
    arguments[sub_comparator].let { rule ->
      if (rule is NoneRule) return 0
      return rule.compare(itemType1, itemType2)
    }
  }

  protected fun defineParameter(parameter: Parameter<*>, defaultValue: String) {
    arguments.defineParameter(parameter, defaultValue)
  }

  protected fun <T> defineParameter(parameter: Parameter<T>, defaultValue: T) {
    arguments.defineParameter(parameter, defaultValue)
  }

  init {
    defineParameter(reverse, false)
    defineParameter(sub_comparator, "::none")
  }
}
