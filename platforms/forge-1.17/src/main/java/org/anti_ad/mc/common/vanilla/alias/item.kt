/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.commands.arguments.NbtPathArgument
import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.TagParser
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagCollection
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.nbt.Tag as NbtTag
import net.minecraft.tags.Tag as TagTag

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias ItemGroup = CreativeModeTab

typealias ItemTags = ItemTags
typealias TagTag<T> = TagTag<T>
typealias TagGroup<T> = TagCollection<T>

// ============
// nbt
// ============

typealias NbtCompound = CompoundTag
typealias NbtElement = NbtTag
typealias NbtList = ListTag
typealias NbtString = StringTag

typealias AbstractNbtNumber = NumericTag
typealias AbstractNbtList<T> = CollectionTag<T>

typealias NbtPathArgumentType = NbtPathArgument
typealias NbtPathArgumentTypeNbtPath = NbtPathArgument.NbtPath
typealias NbtHelper = NbtUtils
typealias StringNbtReader =  TagParser// JsonToNBT.getTagFromJson = StringNbtReader.parse

// ============
// enchantment
// ============

typealias EnchantmentHelper = EnchantmentHelper
typealias Enchantment = Enchantment
typealias Enchantments = Enchantments

// ============
// potion
// ============

typealias PotionUtil = PotionUtils
typealias StatusEffectInstance = MobEffectInstance
