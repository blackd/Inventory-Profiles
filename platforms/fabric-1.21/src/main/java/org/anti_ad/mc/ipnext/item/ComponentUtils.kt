package org.anti_ad.mc.ipnext.item

import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.tag.EnchantmentTags
import org.anti_ad.mc.common.vanilla.alias.ChargedProjectilesComponent
import org.anti_ad.mc.common.vanilla.alias.DataComponentType
import org.anti_ad.mc.common.vanilla.alias.DataComponentTypes
import org.anti_ad.mc.common.vanilla.alias.DyedColorComponent
import org.anti_ad.mc.common.vanilla.alias.ItemEnchantmentsComponent
import org.anti_ad.mc.common.vanilla.alias.MapColorComponent
import org.anti_ad.mc.common.vanilla.alias.MapIdComponent
import org.anti_ad.mc.common.vanilla.alias.MapPostProcessingComponent
import org.anti_ad.mc.common.vanilla.alias.NbtElement
import org.anti_ad.mc.common.vanilla.alias.PotionContentsComponent
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.ipnext.ingame.`(itemType)`
import org.anti_ad.mc.ipnext.item.NbtUtils.compareTo

@Suppress("UNCHECKED_CAST")
object ComponentUtils {

    private val registry: MutableMap<String, Comparator<Any?>> = mutableMapOf()

    private val ItemEnchantmentsComponent.score: Double
        get() {
            return -1 * this.enchantmentsMap.toList().fold(0.0) { acc, (enchantment, level) ->
                acc + if (enchantment.isIn(EnchantmentTags.CURSE)) -0.001 else level.toDouble() / enchantment.value().maxLevel
            }
        }

    private val PotionContentsComponent.score: Double
        get() {
            return if (this.hasEffects()) {
                this.effects.toList().fold(0.0) {acc, statusEffectInstance ->
                    val effectType = statusEffectInstance.effectType.value()
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
                    res += if (statusEffectInstance.isInfinite) 5.0 else 0.0
                    effectType.category
                    acc + res * -1
                }
            } else 0.0
        }

    private val DEFAULT_COMPARATOR: Comparator<Any?> = nullsLast { _: Any, _: Any ->
        0
    }

    private val nbtComparator: Comparator<NbtElement?> = nullsLast { a: NbtElement, b: NbtElement ->
        a.compareTo(b)
    }.apply {
        registry[DataComponentTypes.BLOCK_ENTITY_DATA.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.CUSTOM_DATA.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.BUCKET_ENTITY_DATA.toString()] = this as Comparator<Any?>
        registry[DataComponentTypes.ENTITY_DATA.toString()] = this as Comparator<Any?>
    }

    private val textComparator: Comparator<Text?> = nullsLast( compareBy<Text> { it.content.toString() }
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

    private val mapIdComparator = nullsLast( compareBy<MapIdComponent> { it.id }).apply {
        registry[DataComponentTypes.MAP_ID.toString()] = this as Comparator<Any?>
    }

    private val mapMapPostProcessingComponentComparator = nullsLast( compareBy<MapPostProcessingComponent> { it.id })
        .apply {
            registry[DataComponentTypes.MAP_ID.toString()] = this as Comparator<Any?>
        }

    val itemTypeComparator = nullsLast ( compareBy<ItemType> { itemType -> itemType.itemId }
                                             .thenBy { itemType -> itemType.vanillaStack.count }
                                             .thenComparator { itemType1, itemType2 -> compareComponents(itemType1, itemType2) }
                                       )


    private val chargedProjectilesComponentComparator = nullsLast(
            compareBy<ChargedProjectilesComponent> { it.projectiles.size.inv() }.thenComparator { o1, o2 ->

                val l1: List<ItemType> = o1.projectiles.map { item -> item.`(itemType)` }.sortedWith(itemTypeComparator)
                val l2: List<ItemType> = o2.projectiles.map { item -> item.`(itemType)` }.sortedWith(itemTypeComparator)
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



    fun DataComponentType<*>?.comparatorFor(): Comparator<Any?> {
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
        val list = tag.types.toList().sortedBy { it.toString() }
        val list1 = tag1.types.toList().sortedBy { it.toString() }
        val matching = list.filter {
            it in list1
        }
        matching.forEach {
           it?.comparatorFor()?.compare(tag[it], tag1[it])?.let { cmp ->
               if (cmp != 0) return cmp
           }
        }
        return tag.size().compareTo(tag1.size())
    }

}
