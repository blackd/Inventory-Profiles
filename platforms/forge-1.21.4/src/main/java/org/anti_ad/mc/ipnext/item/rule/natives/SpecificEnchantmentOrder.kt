/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2025 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.item.rule.natives

import it.unimi.dsi.fastutil.objects.Object2IntMap
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.EnchantmentTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments
import org.anti_ad.mc.common.vanilla.Vanilla
import kotlin.jvm.optionals.getOrNull

interface SpecificEnchantmentOrder {

    private companion object {

        fun <T> Set<Object2IntMap.Entry<Holder<T>>>.sortByListOrder(order: MutableList<Holder<T>>): MutableList<Object2IntMap.Entry<Holder<T>>> {
            return toMutableList().also { mutable ->
                mutable.sortWith(compareBy {
                    order.indexOf(it.key)
                })
            }
        }
    }

    val ItemEnchantments.size get() = this.size()

    fun compareEnchantments(a: ItemEnchantments, b: ItemEnchantments): Int {
        val context = TooltipContext.of(Vanilla.world())
        val wrapperLookup = context.registries()
        val registryEntryList: HolderSet<Enchantment> = getTooltipOrderList(wrapperLookup, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER)

        val normalizedList = registryEntryList.toMutableList() //.also{ it.reverse() }

        val enchantmentsA = a.entrySet().sortByListOrder(normalizedList)
        val enchantmentsB = b.entrySet().sortByListOrder(normalizedList)

        for (i in 0 until enchantmentsA.size.coerceAtMost(enchantmentsB.size)) {
            val indexA = normalizedList.indexOf(enchantmentsA[i].key)
            val indexB = normalizedList.indexOf(enchantmentsB[i].key)
            val valueA = enchantmentsA[i].intValue
            val valueB = enchantmentsB[i].intValue
            if (indexA != indexB) return indexA - indexB
            if (valueA != valueB) return valueB - valueA
        }
        return enchantmentsB.size - enchantmentsA.size
    }

    fun <T> getTooltipOrderList(registries: HolderLookup.Provider?, registryRef: ResourceKey<Registry<T>>, tagKey: TagKey<T>): HolderSet<T> {
        return registries?.lookupOrThrow<T>(registryRef)?.get(tagKey)?.getOrNull() ?: HolderSet.direct()
    }

}
