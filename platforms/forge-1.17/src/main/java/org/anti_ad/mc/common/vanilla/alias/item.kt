package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.commands.arguments.NbtPathArgument //net.minecraft.command.arguments.NBTPathArgument
import net.minecraft.world.item.enchantment.EnchantmentHelper //net.minecraft.world.item.enchantment.Enchantment //net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.world.item.Item //net.minecraft.item.Item
import net.minecraft.world.item.CreativeModeTab //net.minecraft.world.item.Item //net.minecraft.item.ItemGroup
import net.minecraft.world.item.ItemStack //net.minecraft.item.ItemStack
import net.minecraft.world.item.Items //net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.TagParser
import net.minecraft.world.effect.MobEffectInstance //net.minecraft.world.effect.MobEffect //net.minecraft.potion.EffectInstance
import net.minecraft.world.item.alchemy.PotionUtils //net.minecraft.potion.PotionUtils
import net.minecraft.tags.TagCollection //net.minecraft.tags.TagCollection //net.minecraft.tags.SetTag //net.minecraft.tags.Tag //net.minecraft.tags.ITagCollection
import net.minecraft.tags.ItemTags
import net.minecraft.nbt.Tag as NbtTag//net.minecraft.nbt.INBT as NbtTag
import net.minecraft.tags.Tag as TagTag //net.minecraft.tags.ITag as TagTag

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias ItemGroup = CreativeModeTab

typealias ItemTags = ItemTags
typealias TagTag<T> = TagTag<T>
typealias TagContainer<T> = TagCollection<T>

// ============
// nbt
// ============

typealias NbtCompound = CompoundTag
typealias NbtTag = NbtTag

typealias AbstractNumberTag = NumericTag
typealias AbstractListTag<T> = CollectionTag<T>

typealias NbtPathArgumentType = NbtPathArgument
typealias NbtPathArgumentTypeNbtPath = NbtPathArgument.NbtPath
typealias NbtHelper = NbtUtils
typealias StringNbtReader =  TagParser// JsonToNBT.getTagFromJson = StringNbtReader.parse

// ============
// enchantment
// ============

typealias EnchantmentHelper = EnchantmentHelper

// ============
// potion
// ============

typealias PotionUtil = PotionUtils
typealias StatusEffectInstance = MobEffectInstance
