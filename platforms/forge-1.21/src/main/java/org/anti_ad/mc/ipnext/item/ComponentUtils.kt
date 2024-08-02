/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2024 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.item

import com.mojang.serialization.Codec
import org.anti_ad.mc.alias.component.ComponentType
import org.anti_ad.mc.alias.component.DataComponentTypes
import org.anti_ad.mc.alias.component.type.DyedColorComponent
import org.anti_ad.mc.alias.component.type.ItemEnchantmentsComponent
import org.anti_ad.mc.alias.component.type.MapColorComponent
import org.anti_ad.mc.alias.component.type.MapIdComponent
import org.anti_ad.mc.alias.component.type.MapPostProcessingComponent
import org.anti_ad.mc.alias.component.type.PotionContentsComponent
import org.anti_ad.mc.alias.component.type.*
import org.anti_ad.mc.alias.entity.effect.StatusEffectCategory
import org.anti_ad.mc.alias.nbt.NbtCompound
import org.anti_ad.mc.alias.nbt.NbtElement
import org.anti_ad.mc.alias.nbt.NbtList
import org.anti_ad.mc.alias.nbt.NbtOps
import org.anti_ad.mc.alias.nbt.NbtString
import org.anti_ad.mc.alias.registry.tag.EnchantmentTags
import org.anti_ad.mc.alias.text.Text
import org.anti_ad.mc.common.vanilla.Vanilla

import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.ingame.`(itemType)`
import org.anti_ad.mc.ipnext.item.NbtUtils.compareTo
import java.util.*
import kotlin.Comparator

@Suppress("UNCHECKED_CAST")
object ComponentUtils {

    private val registry: MutableMap<String, Comparator<Any?>> = mutableMapOf()

    private val ItemEnchantmentsComponent.score: Double
        get() {
            return -1 * this.entrySet().fold(0.0) { acc, (enchantment, level) ->
                acc + if (enchantment.`is`(EnchantmentTags.CURSE)) -0.001 else level.toDouble() / enchantment.value().maxLevel
            }
        }

    private val PotionContentsComponent.score: Double
        get() {
            return if (this.hasEffects()) {
                this.allEffects.toList().fold(0.0) {acc, statusEffectInstance ->
                    val effectType = statusEffectInstance.effect.value()
                    var res: Double = when(effectType.category) {
                        StatusEffectCategory.HARMFUL -> {
                            -5.0
                        }
                        StatusEffectCategory.NEUTRAL, null -> {
                            0.0
                        }
                        StatusEffectCategory.BENEFICIAL -> {
                            5.0
                        }
                    }
                    res += statusEffectInstance.duration / 20.0 / 1000.0
                    res += statusEffectInstance.amplifier * 5.0
                    res += if (statusEffectInstance.isInfiniteDuration) 5.0 else 0.0
                    effectType.category
                    acc + res * -1
                }
            } else 0.0
        }

    private val DEFAULT_COMPARATOR: Comparator<Any?> = nullsLast { _: Any, _: Any ->
        0
    }

    private val nbtComparator: Comparator<NbtComponent?> = nullsLast { a: NbtComponent, b: NbtComponent ->
        a.copyTag().compareTo(b.copyTag())
    }.apply {
        registry[DataComponentTypes.BLOCK_ENTITY_DATA.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.CUSTOM_DATA.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.BUCKET_ENTITY_DATA.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.ENTITY_DATA.toString()] = this as Comparator<Any?>
    }

    private val textComparator: Comparator<Text?> = nullsLast( compareBy<Text> { it.contents.toString() }
                                                                   .thenBy { it.siblings?.size ?: 0 }
                                                                   .thenComparing { a, b ->
                                                                       a.siblings.forEachIndexed { i, text ->
                                                                           getTextComparator().compare(text, b.siblings[i]).let {
                                                                               if (it != 0) return@thenComparing it
                                                                           }
                                                                       }
                                                                       0
                                                                   }
                                                             ).apply {
        registry[DataComponentTypes.CUSTOM_NAME.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.ITEM_NAME.toString()] = this as Comparator<Any?>
    }
    private fun getTextComparator(): Comparator<Text?> = textComparator


    private val mapColorComponentComparator = nullsLast( compareBy<MapColorComponent> { it.rgb }).apply {
        registry[DataComponentTypes.MAP_COLOR.toString()] = this as Comparator<Any?>
    }

    private val dyedComponentComparator = nullsLast( compareBy<DyedColorComponent> { it.rgb }).apply {
        registry[DataComponentTypes.DYED_COLOR.toString()] = this as Comparator<Any?>
    }

    private val mapIdComparator = nullsLast( compareBy<MapIdComponent> {
        it.id
    }).apply {
        registry[DataComponentTypes.MAP_ID.toString()] = this as Comparator<Any?>
    }

    private val mapMapPostProcessingComponentComparator = nullsLast( compareBy<MapPostProcessingComponent> { it.id() })
        .apply {
            registry[DataComponentTypes.MAP_POST_PROCESSING.toString()] = this as Comparator<Any?>
        }

    val itemTypeComparator = nullsLast ( compareBy<ItemType> { itemType -> itemType.itemId }
                                             .thenBy { itemType -> itemType.vanillaStack.count }
                                             .thenComparator { itemType1, itemType2 -> compareComponents(itemType1, itemType2) }
                                       )


    private val chargedProjectilesComponentComparator = nullsLast(
            compareBy<ChargedProjectilesComponent> { it.items.size.inv() }.thenComparator { o1, o2 ->

                val l1: List<ItemType> = o1.items.map { item -> item.`(itemType)` }.sortedWith(itemTypeComparator)
                val l2: List<ItemType> = o2.items.map { item -> item.`(itemType)` }.sortedWith(itemTypeComparator)
                l1.forEachIndexed { index, itemType ->
                    val v = itemTypeComparator.compare(itemType, l2[index])
                    if (v != 0) return@thenComparator v
                }
                0
            })
        .apply {
            registry[DataComponentTypes.CHARGED_PROJECTILES.toString()] = this as Comparator<Any?>
        }

    private val itemEnchantmentsComponentComparator = nullsLast (compareBy<ItemEnchantmentsComponent> { it.score }).apply {
        registry[DataComponentTypes.ENCHANTMENTS.toString()] = this as Comparator<Any?>
    }

    private val potionEffectsComponentComparator = nullsLast (
            compareBy<PotionContentsComponent> { it.score }).apply {
        registry[DataComponentTypes.POTION_CONTENTS.toString()] = this as Comparator<Any?>
    }



    fun ComponentType<*>?.comparatorFor(): Comparator<Any?> {
        registry[this.toString()]?.let {
            return it
        }
        if (this is Comparable<*>) {
            return nullsLast ( compareBy<Comparable<*>> { it } ) as Comparator<Any?>
        }
        return DEFAULT_COMPARATOR
    }

    fun compareComponents(item: ItemType, item1: ItemType): Int {
        val tag = item.tag
        val tag1 = item1.tag
        val b1 = tag == null
        val b2 = tag1 == null
        if (b1 != b2)
            return if (b1) -1 else 1 // no nbt = first
        if (tag == null || tag1 == null) return 0
        val list = tag.keySet().toList().sortedBy { it.toString() }
        val list1 = tag1.keySet().toList().sortedBy { it.toString() }
        val matching = list.filter {
            it in list1
        }
        matching.forEach {
            val c1 = tag[it]
            val c2 = tag1[it]
            Log.trace("c1: ${c1?.javaClass}, c2: ${c2?.javaClass}")
            if (c1 is Comparable<*>) {
                val cmp = (c1 as Comparable<Any?>).compareTo(c2)
                if (cmp != 0) return  cmp
            }
            val comparator = it?.comparatorFor()
            comparator?.compare(c1, c2)?.let { cmp ->
                if (cmp != 0) return cmp
            }
        }
        return tag.size().compareTo(tag1.size())
    }

    fun ComponentType<*>.toFilteredNbtOrNull(value: Optional<*>): NbtElement? {
        if (value.isEmpty) return null
        val codecAny: Codec<Any> = this.codecOrThrow() as Codec<Any>
        val realValue: Any? = value.get()
        if (realValue != null && realValue is NbtElement) return realValue.copy()
        val opt = codecAny.encodeStart(Vanilla.world().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                                       value.get()).resultOrPartial()
        return if (opt.isPresent) {
            when (this) {
                DataComponentTypes.ENCHANTMENTS -> {
                    (opt.get() as NbtCompound).sanitizeEnchantments()
                }

                DataComponentTypes.DAMAGE       -> {
                    null
                }

                DataComponentTypes.CUSTOM_NAME  -> {
                    null
                }

                else                            -> {
                    opt.get()
                }
            }
        } else null
    }

    fun ComponentType<*>.toFullNbtOrNull(value: Optional<*>): NbtElement? {
        if (value.isEmpty) return null
        val codecAny: Codec<Any> = this.codecOrThrow() as Codec<Any>
        val realValue: Any? = value.get()
        if (realValue != null && realValue is NbtElement) return realValue.copy()
        val opt = codecAny.encodeStart(Vanilla.world().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                                       value.get()).resultOrPartial()
        return if (opt.isPresent) {
            opt.get()
        } else null
    }


    private fun NbtCompound.sanitizeEnchantments(): NbtElement {
        val levels = this["levels"] as NbtCompound
        val newLevels = NbtList()
        levels.allKeys.forEach { it ->
            Log.trace("found level $it")
            newLevels.add(NbtString.valueOf(it))
        }

        return newLevels
    }
}
