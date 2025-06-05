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
import net.minecraft.registry.RegistryWrapper
import org.anti_ad.mc.alias.component.type.ItemEnchantmentsComponent
import org.anti_ad.mc.alias.enchantment.Enchantment
import org.anti_ad.mc.alias.item.TooltipContext
import org.anti_ad.mc.alias.registry.Registry
import org.anti_ad.mc.alias.registry.RegistryKey
import org.anti_ad.mc.alias.registry.RegistryKeys
import org.anti_ad.mc.alias.registry.entry.RegistryEntry
import org.anti_ad.mc.alias.registry.entry.RegistryEntryList
import org.anti_ad.mc.alias.registry.tag.EnchantmentTags
import org.anti_ad.mc.alias.registry.tag.TagKey
import org.anti_ad.mc.common.vanilla.Vanilla
import kotlin.jvm.optionals.getOrNull

interface SpecificEnchantmentOrder {

    private companion object {

        fun <T> Set<Object2IntMap.Entry<RegistryEntry<T>>>.sortByListOrder(order: MutableList<RegistryEntry<T>>): MutableList<Object2IntMap.Entry<RegistryEntry<T>>> {
            return toMutableList().also { mutable ->
                mutable.sortWith(compareBy {
                    order.indexOf(it.key)
                })
            }
        }
    }

    fun compareEnchantments(a: ItemEnchantmentsComponent, b: ItemEnchantmentsComponent): Int {
        val context = TooltipContext.create(Vanilla.world())
        val wrapperLookup = context.registryLookup;
        val registryEntryList: RegistryEntryList<Enchantment> = getTooltipOrderList(wrapperLookup, RegistryKeys.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER)

        val normalizedList = registryEntryList.toMutableList() //.also{ it.reverse() }

        val enchantmentsA = a.enchantmentEntries.sortByListOrder(normalizedList)
        val enchantmentsB = b.enchantmentEntries.sortByListOrder(normalizedList)

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

    fun <T> getTooltipOrderList(registries: RegistryWrapper.WrapperLookup?, registryRef: RegistryKey<Registry<T>>, tagKey: TagKey<T>): RegistryEntryList<T> {
        return registries?.getOrThrow(registryRef)?.getOptional(tagKey)?.getOrNull() ?: RegistryEntryList.of(listOf<RegistryEntry<T>>())
    }

}
