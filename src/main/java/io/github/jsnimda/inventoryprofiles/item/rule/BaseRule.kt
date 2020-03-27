package io.github.jsnimda.inventoryprofiles.item.rule

import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.reverse
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.sub_comparator

abstract class BaseRule : Rule {
  final override val arguments = Arguments()
  var innerCompare: (ItemType, ItemType) -> Int = { _, _ -> 0 }

  init {
    arguments.apply {
      defineParameter(reverse, false)
      defineParameter(sub_comparator, EmptyRule())
    }
  }

  final override fun compare(itemType1: ItemType, itemType2: ItemType): Int {
    val result = innerCompare(itemType1, itemType2)
    if (result != 0) return result * if (arguments[reverse]) -1 else 1
    return arguments[sub_comparator].compare(itemType1, itemType2)
  }
}
