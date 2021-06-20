package io.github.jsnimda.common.vanilla.alias

import net.minecraft.command.arguments.NBTPathArgument
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.PotionUtils
import net.minecraft.tags.ItemTags
import net.minecraft.tags.ITagCollection
import net.minecraft.nbt.INBT as NbtTag
import net.minecraft.tags.ITag as TagTag // ~.~ Tag is SetTag!!!!!!

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias ItemGroup = ItemGroup

typealias ItemTags = ItemTags
typealias TagTag<T> = TagTag<T>
typealias TagContainer<T> = ITagCollection<T>

// ============
// nbt
// ============

typealias CompoundTag = CompoundNBT
typealias NbtTag = NbtTag

typealias AbstractNumberTag = NumberNBT
typealias AbstractListTag<T> = CollectionNBT<T>

typealias NbtPathArgumentType = NBTPathArgument
typealias NbtPathArgumentTypeNbtPath = NBTPathArgument.NBTPath
typealias NbtHelper = NBTUtil
typealias StringNbtReader = JsonToNBT // JsonToNBT.getTagFromJson = StringNbtReader.parse

// ============
// enchantment
// ============

typealias EnchantmentHelper = EnchantmentHelper

// ============
// potion
// ============

typealias PotionUtil = PotionUtils
typealias StatusEffectInstance = EffectInstance
