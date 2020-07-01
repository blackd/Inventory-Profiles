package io.github.jsnimda.inventoryprofiles.item.rule

import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.reverse
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.sub_rule

interface Rule : Comparator<ItemType> {
  val arguments: ArgumentMap
  override fun compare(itemType1: ItemType, itemType2: ItemType): Int
}

object EmptyRule : Rule {
  override val arguments: ArgumentMap
    get() = ArgumentMap()

  override fun compare(itemType1: ItemType, itemType2: ItemType): Int {
    return 0
  }
}

class MutableEmptyRule : BaseRule()

abstract class BaseRule : Rule {
  final override val arguments = ArgumentMap()
  var comparator: (ItemType, ItemType) -> Int = { _, _ -> 0 }

  init {
    arguments.apply {
      defineParameter(reverse, false)
      defineParameter(sub_rule, EmptyRule)
    }
  }

  private val lazyCompare by lazy(LazyThreadSafetyMode.NONE) { // call when arguments no more changes
    val mul = if (arguments[reverse]) -1 else 1
    val noSubComparator = arguments.isDefaultValue(sub_rule)
    return@lazy if (noSubComparator) {
      fun(itemType1: ItemType, itemType2: ItemType): Int {
        return comparator(itemType1, itemType2) * mul
      }
    } else {
      val subComparator = arguments[sub_rule]
      fun(itemType1: ItemType, itemType2: ItemType): Int {
        val result = comparator(itemType1, itemType2)
        if (result != 0) return result * mul
        return subComparator.compare(itemType1, itemType2)
      }
    }
  }

  final override fun compare(itemType1: ItemType, itemType2: ItemType): Int {
    return lazyCompare(itemType1, itemType2)
//    val result = comparator(itemType1, itemType2)
//    if (result != 0) return result * if (arguments[reverse]) -1 else 1
//    if (arguments.isDefaultValue(sub_comparator)) return 0 // still empty rule
//    return arguments[sub_comparator].compare(itemType1, itemType2)
  }
}