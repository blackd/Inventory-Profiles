package io.github.jsnimda.inventoryprofiles.item

import com.mojang.brigadier.StringReader
import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.alias.CompoundTag
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.common.vanilla.alias.Item
import io.github.jsnimda.common.vanilla.alias.Registry
import net.minecraft.command.arguments.NbtPathArgumentType
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag
import net.minecraft.tag.ItemTags
import net.minecraft.tag.Tag as TagTag

// ============
// vanillamapping code depends on mappings
// ============

object NbtUtils {
  // ============
  // Vanilla Item
  // ============
  fun getItemFromId(id: Identifier): Item? {
    return Registry.ITEM.get(id)
  }

  fun getTagFromId(id: Identifier): TagTag<Item>? {
    // see ItemPredicateArgumentType
    // (for data pack)
    // Tag<Item> tag = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getTagManager().items().get(identifier);
    // (hard coded)
    // ItemTags.getContainer()
    return ItemTags.getContainer().get(id)
  }

  // ============
  // nbt
  // ============
  fun compareNbt(a: CompoundTag?, b: CompoundTag?): Int {
    return 0
    TODO()
  }

  private fun Tag.compareTo(another: Tag) = 0

  fun matchNbtNoExtra(a: CompoundTag?, b: CompoundTag?): Boolean { // handle null and empty
    return a?.takeUnless { it.isEmpty } == b?.takeUnless { it.isEmpty }
  }

  fun matchNbt(a: CompoundTag?, b: CompoundTag?): Boolean { // b superset of a (a <= b)
    if (a == null || a.isEmpty) return true // treats {} as null
    return innerMatchNbt(a, b)
  }

  private fun innerMatchNbt(a: CompoundTag?, b: CompoundTag?): Boolean { // b superset of a (a <= b)
    // NbtHelper.matches()
    return NbtHelper.matches(a, b, true) // criteria, testTarget, allowExtra (for list)
  }

  private fun getNbtPath(path: String): NbtPathArgumentType.NbtPath? {
    // NbtPathArgumentType().parse(StringReader(path))
    return tryCatch { NbtPathArgumentType().parse(StringReader(path)) }
  }

  private fun getTagsForPath(nbtPath: NbtPathArgumentType.NbtPath, target: Tag): List<Tag> {
    return tryCatch(listOf()) { nbtPath.get(target) }
  }

  fun parseNbt(nbt: String): CompoundTag? {
    // StringNbtReader
    return tryCatch { StringNbtReader.parse(nbt) }
  }


}