package io.github.jsnimda.inventoryprofiles.item

import com.mojang.brigadier.StringReader
import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.alias.CompoundTag
import net.minecraft.command.arguments.NbtPathArgumentType
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag

object NbtUtils {
  fun compareNbt(a: CompoundTag?, b: CompoundTag?) =
    //todo
    0


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