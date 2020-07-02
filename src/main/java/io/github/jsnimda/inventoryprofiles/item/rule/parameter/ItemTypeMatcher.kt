package io.github.jsnimda.inventoryprofiles.item.rule.parameter

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.trySwallow
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.NbtUtils

sealed class ItemTypeMatcher {
  abstract fun match(itemType: ItemType): Boolean

  class IsTag(val identifier: Identifier) : ItemTypeMatcher() { // lazy
    val tag by lazy { NbtUtils.getTagFromId(identifier) }

    override fun match(itemType: ItemType): Boolean {
      val tag = tag
      tag ?: return false.also { Log.warn("Unknown tag #$identifier") }
      return tag.contains(itemType.item)
    }
  }

  class IsItem(val identifier: Identifier) : ItemTypeMatcher() { // lazy
    val item by lazy { NbtUtils.getItemFromId(identifier) }

    override fun match(itemType: ItemType): Boolean {
      val item = item
      item ?: return false.also { Log.warn("Unknown item $identifier") }
      return itemType.item == item
    }
  }

  companion object {
    fun forTag(id: String): ItemTypeMatcher? {
      val identifier = trySwallow { Identifier(id) }
      identifier ?: return null
      return IsTag(identifier)
    }

    fun forItem(id: String): ItemTypeMatcher? {
      val identifier = trySwallow { Identifier(id) }
      identifier ?: return null
      return IsItem(identifier)
    }
  }
}
