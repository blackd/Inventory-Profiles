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

@file:Suppress("ObjectPropertyName")

package org.anti_ad.mc.ipnext.item

import net.minecraft.potion.EffectType
import net.minecraftforge.common.extensions.IForgeFluid
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.alias.Enchantment
import org.anti_ad.mc.common.vanilla.alias.EnchantmentHelper
import org.anti_ad.mc.common.vanilla.alias.FoodComponent
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.ItemGroup
import org.anti_ad.mc.common.vanilla.alias.Items
import org.anti_ad.mc.common.vanilla.alias.PotionUtil
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.items.BucketItem
import org.anti_ad.mc.common.vanilla.alias.items.EntityBucketItem
import org.anti_ad.mc.common.vanilla.alias.items.Fluids
import org.anti_ad.mc.common.vanilla.alias.items.MilkBucketItem
import org.anti_ad.mc.common.vanilla.alias.items.MushroomStewItem
import org.anti_ad.mc.common.vanilla.alias.items.SuspiciousStewItem
import org.anti_ad.mc.ipnext.ingame.`(getIdentifier)`
import org.anti_ad.mc.ipnext.ingame.`(getRawId)`
import org.anti_ad.mc.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

fun ItemType.fullItemInfoAsJson(): String {
    var res = "{" + "\n\t\"id\" : \"" + this.itemId+ "\",\n"
    tag?.keySet()?.forEach {
        res += "\t{\n\t\t\"$it\" : ${tag.get(it)}\n},"
    }
    return "$res\n}"
}

fun ItemType.toNamespacedString(): String { // like ItemType.toString() but with namespace
    return itemId + (tag ?: "")
}

inline val ItemType.Companion.EMPTY
    get() = ItemType(Items.AIR,
                     null,
                     { false })

fun ItemType.isEmpty() : Boolean {
    return (item == Items.AIR).ifTrue {
        if (this.tag?.equals(null) == false) Log.warn("Informal item type $this")
    }
}

inline val ItemType.maxCount: Int
    get() = vanillaStack.maxStackSize

inline val ItemType.vanillaStack: VanillaItemStack
    get() = VanillaItemStack(this.item).apply { tag = this@vanillaStack.tag }

fun ItemType.vanillaStackWithCount(count: Int): VanillaItemStack =
    VanillaItemStack(this.item,
                     count).apply { tag = this@vanillaStackWithCount.tag }

inline val ItemType.identifier: Identifier
    get() = Registry.ITEM.`(getIdentifier)`(item)

inline val ItemType.namespace: String
    get() = identifier.namespace

inline val ItemType.itemClass
    get() = item.javaClass

//region ItemType String Relative

inline val ItemType.hasCustomName: Boolean
    get() = vanillaStack.hasDisplayName() //hasCustomHoverName() //hasCustomName()
inline val ItemType.customName: String
    get() = if (hasCustomName) displayName else ""
inline val ItemType.displayName: String
    get() = vanillaStack.displayName.string
inline val ItemType.translatedName: String
    get() = I18n.translate(translationKey)
inline val ItemType.itemId: String
    get() = identifier.toString()
inline val ItemType.translationKey: String
    get() = vanillaStack.translationKey //descriptionId
inline val ItemType.isStackable: Boolean
    get() = vanillaStack.isStackable

inline val ItemType.customOrTranslatedName: String
    get() = if (hasCustomName) displayName else I18n.translate(translationKey)

//endregion

//region ItemType Number Relative

inline val ItemType.groupIndex: Int
    get() = item.group?.index ?: when { //itemCategory?.id
        item === Items.ENCHANTED_BOOK -> ItemGroup.TOOLS.index // TAB_TOOLS.id
        namespace == "minecraft" -> ItemGroup.MISC.index // TAB_MISC.id
        else -> ItemGroup.GROUPS.size // TABS.size
    }

inline val ItemType.`(group)`: ItemGroup?
    get() = item.group


inline val ItemType.`(foodComponent)`: FoodComponent
    get() = item.food ?: error("this shouldn't happen")

inline val ItemType.`(isFood)`: Boolean
    get() = item.isFood


inline val FoodComponent.`(statusEffects)`: List<Pair<StatusEffectInstance, Float>>
    get() = mutableListOf<Pair<StatusEffectInstance, Float>>().also { ls ->
        this.effects.forEach {
            ls.add(Pair(it.left, it.right))
        }
    }

inline val FoodComponent.`(isHarmful)`: Boolean
    get() = run {
        var res = false
        run fastEnd@{
            this.`(statusEffects)`.forEach {
                if (it.first.potion.effectType == EffectType.HARMFUL) {
                    res = true
                    return@fastEnd
                }

            }
        }
        res

    }

inline val FoodComponent.`(saturationModifier)`
    get() = this.saturation

inline val ItemType.rawId: Int
    get() = Registry.ITEM.`(getRawId)`(item)
inline val ItemType.damage: Int
    get() = vanillaStack.damage // .damageValue
inline val ItemType.enchantmentsScore: Double
    //  get() = EnchantmentHelper.get(vanillaStack).toList().fold(0.0) { acc, (enchantment, level) ->
    get() = EnchantmentHelper.getEnchantments(vanillaStack).toList()
        .fold(0.0) { acc, (enchantment, level) ->
            acc + if (enchantment.isCurse) -0.001 else level.toDouble() / enchantment.maxLevel
        } // cursed enchantments +0 scores

inline val ItemType.enchantments: MutableMap<Enchantment, Int>
    get() = EnchantmentHelper.getEnchantments(vanillaStack)

inline val ItemType.isDamageable: Boolean
    get() = vanillaStack.isDamageable  // .isDamageableItem
inline val ItemType.maxDamage: Int
    get() = vanillaStack.maxDamage
inline val ItemType.durability: Int
    get() = maxDamage - damage

inline val ItemType.isBucket: Boolean
    get() = item is BucketItem || item is MilkBucketItem || item is EntityBucketItem

inline val ItemType.isFullBucket: Boolean
    get() = (item is BucketItem && item != Items.BUCKET) || item is MilkBucketItem || item is EntityBucketItem

inline val ItemType.isEmptyBucket: Boolean
    get() {
        return item == Items.BUCKET
    }

fun ItemType.isEmptyComparedTo(other: ItemType): Boolean {
    val otherItem = other.item
    Log.trace("isEmpty item: ${item.javaClass}")
    Log.trace("isEmpty otherItem: ${item.javaClass}")
    return if (item is MilkBucketItem && otherItem is BucketItem && otherItem.fluid == Fluids.EMPTY) {
        true
    } else if (otherItem == Items.BUCKET && item is BucketItem && item.fluid != Fluids.EMPTY) {
        true
    } else item is EntityBucketItem && otherItem is BucketItem && otherItem !is EntityBucketItem
    //item is MilkBucketItem || item is IMixinEntityBucketItem || (item is IMixinBucketItem && !(item.fluid as IMixinFluid).callIsEmpty())
}

fun ItemType.isFullComparedTo(other: ItemType): Boolean {
    val otherItem = other.item
    Log.trace("isFull item: ${item.javaClass}")
    Log.trace("isFull otherItem: ${item.javaClass}")
    return if (item == Items.BUCKET && otherItem is MilkBucketItem) {
        true
    } else item !is EntityBucketItem && otherItem is EntityBucketItem

}


inline val ItemType.isHoneyBottle: Boolean
    get() = item == Items.HONEY_BOTTLE

inline val ItemType.isStew: Boolean
    get() = item is MushroomStewItem || item is SuspiciousStewItem // SoupItem = MushroomStewItem
//endregion

//region ItemType Potion Relative

inline val ItemType.hasPotionName: Boolean
    get() = tag?.contains("Potion",
                          8) ?: false
inline val ItemType.potionName: String
    // getPotionTypeFromNBT().getNamePrefixed() = getPotion().finishTranslationKey()
    get() = if (hasPotionName) PotionUtil.getPotionTypeFromNBT(tag)
        .getNamePrefixed("") else "" // getPotion(tag).getName("") else ""
inline val ItemType.hasPotionEffects: Boolean
    get() = PotionUtil.getEffectsFromTag(tag) // getCustomEffects(tag)
        .isNotEmpty() // forge getEffectsFromTag() = getPotionEffects()
inline val ItemType.hasCustomPotionEffects: Boolean
    get() = PotionUtil.getFullEffectsFromTag(tag)// getAllEffects(tag)
        .isNotEmpty() // getCustomPotionEffects() = getFullEffectsFromTag
inline val ItemType.potionEffects: List<org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance>
    get() = PotionUtil.getFullEffectsFromTag(tag) // getCustomEffects(tag)
inline val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

@Suppress("ObjectPropertyName")
inline val StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(
        Registry.EFFECTS.getId(this.potion)
            .toString(), // forge EFFECTS = STATUS_EFFECT  == MOB_EFFECT | effectType = potion
        this.amplifier,
        this.duration
    )

data class PotionEffect(inline val effect: String,
                        inline val amplifier: Int,
                        inline val duration: Int) : Comparable<PotionEffect> {
    override fun compareTo(other: PotionEffect): Int { // stronger first
        this.effect.compareTo(other.effect).let { if (it != 0) return it }
        other.amplifier.compareTo(this.amplifier).let { if (it != 0) return it }
        other.duration.compareTo(this.duration).let { if (it != 0) return it }
        return 0
    }
}

//endregion
