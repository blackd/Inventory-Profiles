package org.anti_ad.mc.ipnext.item

import com.mojang.brigadier.StringReader
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.*
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.ipnext.ingame.`(asString)`
import org.anti_ad.mc.ipnext.ingame.`(getByIdentifier)`
import org.anti_ad.mc.ipnext.ingame.`(type)`

// ============
// vanillamapping code depends on mappings
// ============

object NbtUtils {
  // ============
  // Vanilla Item
  // ============
  fun getItemFromId(id: Identifier): Item? {
    return Registry.ITEM.`(getByIdentifier)`(id)
  }

  fun getTagFromId(id: Identifier): TagTag<Item>? {
    // see ItemPredicateArgumentType
    // (for data pack)
    // Tag<Item> tag = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getTagManager().items().get(identifier);
    // (hard coded)
    // ItemTags.getContainer()
    return ItemTags.getTagGroup().getTag(id)
  }

  // ============
  // nbt
  // ============
  fun compareNbt(a: NbtCompound?, b: NbtCompound?): Int {
    val b1 = a == null
    val b2 = b == null
    if (b1 != b2)
      return if (b1) -1 else 1 // no nbt = first
    if (a == null || b == null) return 0
    val keys1: List<String> = a.keys.sorted()
    val keys2: List<String> = b.keys.sorted()
    val pairs1 = keys1.map { (it to a.get(it)).asComparable(::compareStringTag) }
    val pairs2 = keys2.map { (it to b.get(it)).asComparable(::compareStringTag) }
    return pairs1.compareTo(pairs2)
  }

  private fun compareStringTag(p1: Pair<String, NbtElement?>, p2: Pair<String, NbtElement?>): Int {
    val (key1, tag1) = p1
    val (key2, tag2) = p2
    val result = key1.compareTo(key2)
    if (result != 0) return result
    if (tag1 == null || tag2 == null) return 0 // actually they should be non null
    return tag1.compareTo(tag2)
  }

  private fun NbtElement.compareTo(other: NbtElement): Int {
    val w1 = WrappedTag(this)
    val w2 = WrappedTag(other)
    return when {
      w1.isNumber -> if (w2.isNumber) w1.asDouble.compareTo(w2.asDouble) else null
      w1.isCompound -> if (w2.isCompound) compareNbt(w1.asCompound, w2.asCompound) else null
      w1.isList -> if (w2.isList) w1.asListComparable.compareTo(w2.asListComparable) else null
      else -> null
    } ?: w1.asString.compareTo(w2.asString)
  }

  fun parseNbt(nbt: String): NbtCompound? {
    // StringNbtReader
    return tryCatch { StringNbtReader.parse(nbt) }
  }

  // ============
  // match nbt
  // ============
  fun matchNbtNoExtra(a: NbtCompound?, b: NbtCompound?): Boolean { // handle null and empty
    return a?.unlessIt { isEmpty } == b?.unlessIt { isEmpty }
  }

  fun matchNbt(a: NbtCompound?, b: NbtCompound?): Boolean { // b superset of a (a <= b)
    if (a == null || a.isEmpty) return true // treats {} as null
    return innerMatchNbt(a, b)
  }

  class NbtPath(val value: NbtPathArgumentTypeNbtPath) { // wrapper class to avoid direct imports to vanilla code
    companion object {
      fun of(string: String): NbtPath? {
        return getNbtPath(string)?.let { NbtPath(it) }
      }
    }

    fun getTags(itemType: ItemType): List<WrappedTag> {
      val tag = itemType.tag
      tag ?: return listOf()
      return getTagsForPath(value, tag).map { WrappedTag(it) }
    }
  }

  class WrappedTag(val value: NbtElement) {
    val isString: Boolean
      get() = value.`(type)` == 8
    val isNumber: Boolean
      get() = value.`(type)` in 1..6
    val isCompound: Boolean
      get() = value.`(type)` == 10
    val isList: Boolean
      get() = value.`(type)` in listOf(7, 9, 11, 12)
    val asString: String
      get() = value.`(asString)`
    val asNumber: Number // todo what if number is long > double precision range
      get() = (value as? AbstractNbtNumber)?.doubleValue() ?: 0
    val asDouble: Double
      get() = (value as? AbstractNbtNumber)?.doubleValue() ?: 0.0
    val asCompound: NbtCompound
      get() = value as? NbtCompound ?: NbtCompound()
    val asList: List<WrappedTag>
      get() = (value as? AbstractNbtList<*>)?.map { WrappedTag(it) } ?: listOf()
    val asListUnwrapped: List<NbtElement>
      get() = (value as? AbstractNbtList<*>)?.toList() ?: listOf()
    val asListComparable: List<AsComparable<NbtElement>>
      get() = asListUnwrapped.map { it.asComparable { a, b -> a.compareTo(b) } }
  }

  // ============
  // private
  // ============
  private fun innerMatchNbt(a: NbtCompound?, b: NbtCompound?): Boolean { // b superset of a (a <= b)
    // NbtHelper.matches()
    return NbtHelper.matches(a, b, true) // criteria, testTarget, allowExtra (for list)
  }

  private fun getNbtPath(path: String): NbtPathArgumentTypeNbtPath? {
    // NbtPathArgumentType().parse(StringReader(path))
    return tryOrPrint(Log::warn) { NbtPathArgumentType().parse(StringReader(path)) }
  }

  private fun getTagsForPath(nbtPath: NbtPathArgumentTypeNbtPath, target: NbtElement): List<NbtElement> {
    return trySwallow(listOf()) { nbtPath.get(target) }
  }
}