package io.github.jsnimda.inventoryprofiles.item.rule

import io.github.jsnimda.inventoryprofiles.item.ItemType

class EmptyRule : Rule {
  override val arguments = Arguments()
  override fun compare(itemType1: ItemType, itemType2: ItemType): Int = 0
}