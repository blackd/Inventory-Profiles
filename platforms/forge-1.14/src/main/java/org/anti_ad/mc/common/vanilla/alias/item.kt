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

import net.minecraft.command.arguments.NBTPathArgument
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CollectionNBT
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.nbt.NumberNBT
import net.minecraft.nbt.StringNBT
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.PotionUtils
import net.minecraft.tags.TagCollection
import net.minecraft.tags.ItemTags
import net.minecraft.nbt.INBT as NbtTag
import net.minecraft.tags.Tag as TagTag

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias ItemGroup = ItemGroup

typealias ItemTags = ItemTags
typealias TagTag<T> = TagTag<T>
typealias TagGroup<T> = TagCollection<T>

// ============
// nbt
// ============

typealias NbtCompound = CompoundNBT
typealias NbtElement = NbtTag
typealias NbtList = ListNBT
typealias NbtString = StringNBT

typealias AbstractNbtNumber = NumberNBT
typealias AbstractNbtList<T> = CollectionNBT<T>

typealias NbtPathArgumentType = NBTPathArgument
typealias NbtPathArgumentTypeNbtPath = NBTPathArgument.NBTPath
typealias NbtHelper = NBTUtil
typealias StringNbtReader = JsonToNBT // JsonToNBT.getTagFromJson = StringNbtReader.parse

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
typealias StatusEffectInstance = EffectInstance
