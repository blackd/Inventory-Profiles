package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.command.arguments.NbtPathArgumentType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.AbstractListTag
import net.minecraft.nbt.AbstractNumberTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.StringTag
import net.minecraft.potion.PotionUtil
import net.minecraft.tag.ItemTags
import net.minecraft.tag.TagContainer
import net.minecraft.nbt.Tag as NbtTag
import net.minecraft.tag.Tag as TagTag

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias ItemGroup = ItemGroup

typealias ItemTags = ItemTags
typealias TagTag<T> = TagTag<T>
typealias TagGroup<T> = TagContainer<T>

// ============
// nbt
// ============

typealias NbtCompound = CompoundTag
typealias NbtElement = NbtTag
typealias NbtList = ListTag
typealias NbtString = StringTag

typealias AbstractNbtNumber = AbstractNumberTag
typealias AbstractNbtList<T> = AbstractListTag<T>

typealias NbtPathArgumentType = NbtPathArgumentType
typealias NbtPathArgumentTypeNbtPath = NbtPathArgumentType.NbtPath
typealias NbtHelper = NbtHelper
typealias StringNbtReader = StringNbtReader // JsonToNBT.getTagFromJson = StringNbtReader.parse

// ============
// enchantment
// ============

typealias EnchantmentHelper = EnchantmentHelper

// ============
// potion
// ============

typealias PotionUtil = PotionUtil
typealias StatusEffectInstance = StatusEffectInstance
