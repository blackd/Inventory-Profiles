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

import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.effect.MobEffectCategory
import net.minecraftforge.common.extensions.IForgeFluid
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Enchantment
import org.anti_ad.mc.common.vanilla.alias.EnchantmentHelper
import org.anti_ad.mc.common.vanilla.alias.FoodComponent
import org.anti_ad.mc.common.vanilla.alias.ForgeRegistries
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.ItemGroup
import org.anti_ad.mc.common.vanilla.alias.ItemGroupType
import org.anti_ad.mc.common.vanilla.alias.ItemGroups
import org.anti_ad.mc.common.vanilla.alias.Items
import org.anti_ad.mc.common.vanilla.alias.PotionUtil
import org.anti_ad.mc.common.vanilla.alias.Registries
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.items.BucketItem
import org.anti_ad.mc.common.vanilla.alias.items.EntityBucketItem
import org.anti_ad.mc.common.vanilla.alias.items.Fluids
import org.anti_ad.mc.common.vanilla.alias.items.MilkBucketItem
import org.anti_ad.mc.common.vanilla.alias.items.MushroomStewItem
import org.anti_ad.mc.common.vanilla.alias.items.PowderSnowBucketItem
import org.anti_ad.mc.common.vanilla.alias.items.SuspiciousStewItem
import org.anti_ad.mc.ipnext.config.CreativeMenuSortOrder
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(getIdentifier)`
import org.anti_ad.mc.ipnext.ingame.`(getRawId)`
import org.anti_ad.mc.ipnext.ingame.`(itemType)`
import org.anti_ad.mc.ipnext.mixinhelpers.IMixinItemGroup
import org.anti_ad.mc.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

fun ItemType.fullItemInfoAsJson(): String {
    var res = "{" + "\n\t\"id\" : \"" + this.itemId + "\",\n"
    tag?.allKeys?.forEach {
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

inline val ItemType.searchItemStack: VanillaItemStack
    get() {
        return VanillaItemStack(this.item, 1)
    }

inline val ItemType.vanillaStack: VanillaItemStack
    get() = VanillaItemStack(this.item).apply { tag = this@vanillaStack.tag }

fun ItemType.vanillaStackWithCount(count: Int): VanillaItemStack =
        VanillaItemStack(this.item,
                         count).apply { tag = this@vanillaStackWithCount.tag }

@Suppress("DEPRECATION")
inline val ItemType.identifier: Identifier
    get() = ForgeRegistries.ITEMS.getKey(item)!! // `(getIdentifier)`(item)

inline val ItemType.namespace: String
    get() = identifier.namespace

inline val ItemType.itemClass
    get() = item.javaClass

//region ItemType String Relative

inline val ItemType.hasCustomName: Boolean
    get() = vanillaStack.hasCustomHoverName() //hasCustomHoverName() //hasCustomName()
inline val ItemType.customName: String
    get() = if (hasCustomName) displayName else ""
inline val ItemType.displayName: String
    get() = vanillaStack.displayName.string
inline val ItemType.translatedName: String
    get() = I18n.translate(translationKey)
inline val ItemType.itemId: String
    get() = identifier.toString()
inline val ItemType.translationKey: String
    get() = vanillaStack.descriptionId //translationKey //descriptionId
inline val ItemType.isStackable: Boolean
    get() = vanillaStack.isStackable

inline val ItemType.customOrTranslatedName: String
    get() = if (hasCustomName) displayName else I18n.translate(translationKey)

//endregion

//region ItemType Number Relative

inline val ItemGroup.priorityIndex: Int
    get() {
        if (this is IMixinItemGroup) {
            var index = ipnPriorityIndex
            if (index == -1) {
                val content = this.displayName.contents
                if (content is TranslatableContents) {
                    index = ItemTypeExtensionsObject.translationKeysPriorityList.indexOf(content.key)
                    ipnPriorityIndex = if (index == -1) {
                        Int.MAX_VALUE
                    } else {
                        index
                    }
                }
            }
            return index
        }
        return Int.MAX_VALUE
    }

private class ItemGroupComparator: Comparator<ItemGroup> {

    override fun compare(g1: ItemGroup?,
                         g2: ItemGroup?): Int {

        val defaultCompare = {
            if (g1 == null && g2 == null) {
                0
            } else {
                if (g1 == null) {
                    -1
                } else {
                    if (g2 == null) {
                        1
                    } else {
                        if (g1.row() == g2.row()) {
                            g1.column().compareTo(g2.column())
                        } else {
                            g1.row().compareTo(g2.row())
                        }
                    }
                }
            }
        }

        val priorityCompare = {
            if (g1 == null && g2 == null) {
                0
            } else {
                if (g1 == null) {
                    -1
                } else {
                    if (g2 == null) {
                        1
                    } else {
                        g1.priorityIndex.compareTo(g2.priorityIndex)
                    }
                }
            }
        }

        return if (ModSettings.CREATIVE_SORT_ORDER_TYPE.value == CreativeMenuSortOrder.SEARCH_TAB)
            defaultCompare()
        else
            priorityCompare()
    }
}

private fun initGroupIndex(): Boolean {
    val features = Vanilla.mc().player?.connection?.enabledFeatures()
    val lookup = Vanilla.mc().player?.level()?.registryAccess() //getRegistryManager()
    val opTabEnabled = Vanilla.mc().options.operatorItemsTab().get() && Vanilla.playerNullable()?.canUseGameMasterBlocks() ?: false
    return if (features != null && lookup != null) {
        ItemGroups.tryRebuildTabContents(features, opTabEnabled, lookup);
        true
    } else {
        false
    }
}

val ItemType.searchTabIndex: Int
    get() {
        initGroupIndex()
        return ItemGroups.searchTab().searchTabDisplayItems.indexOfFirst { it ->
            it != null && it.`(itemType)` == this
        }
    }

val ItemType.groupIndex: Int
    get() {
        val stack = this.searchItemStack
        initGroupIndex()
        var result = Int.MAX_VALUE
        val grps = ItemGroups.allTabs().filter {
            val grType = it.type ?: ItemGroupType.INVENTORY
            grType === ItemGroupType.CATEGORY
        }.sortedWith(ItemGroupComparator())

        run earlyStop@ {
            grps.forEachIndexed { index, itemGroup ->
                if (itemGroup.contains(stack)) {
                    result = index
                    return@earlyStop
                }
            }
        }
        if (result in 0..(grps.size-1)) {
            val grp = grps[result]
            Log.trace("${this.identifier} - > $result -> ${grp?.displayName}")
        }
        return result
    }

/*
inline val ItemType.`(group)`: ItemGroup?
    get() = item.itemCategory
*/

@Suppress("DEPRECATION")
inline val ItemType.`(foodComponent)`: FoodComponent
    get() = item.foodProperties ?: error("this shouldn't happen")

inline val ItemType.`(isFood)`: Boolean
    get() = item.isEdible


inline val FoodComponent.`(statusEffects)`: List<Pair<StatusEffectInstance, Float>>
    get() = mutableListOf<Pair<StatusEffectInstance, Float>>().also { ls ->
        this.effects.forEach {
            ls.add(Pair(it.first, it.second))
        }
    }

inline val FoodComponent.`(isHarmful)`: Boolean
    get() = run {
        var res = false
        run fastEnd@{
            this.`(statusEffects)`.forEach {
                if (it.first.effect.category == MobEffectCategory.HARMFUL) {
                    res = true
                    return@fastEnd
                }

            }
        }
        res

    }

inline val FoodComponent.`(saturationModifier)`
    get() = this.saturationModifier

@Suppress("DEPRECATION")
inline val ItemType.rawId: Int
    get() = Registries.ITEM.`(getRawId)`(item)
inline val ItemType.damage: Int
    get() = vanillaStack.damageValue //damage // .damageValue
inline val ItemType.enchantmentsScore: Double
    //  get() = EnchantmentHelper.get(vanillaStack).toList().fold(0.0) { acc, (enchantment, level) ->
    get() = EnchantmentHelper.getEnchantments(vanillaStack).toList()
        .fold(0.0) { acc, (enchantment, level) ->
            acc + if (enchantment.isCurse) -0.001 else level.toDouble() / enchantment.maxLevel
        } // cursed enchantments +0 scores

inline val ItemType.enchantments: MutableMap<Enchantment, Int>
    get() = EnchantmentHelper.getEnchantments(vanillaStack)

inline val ItemType.isDamageable: Boolean
    get() = vanillaStack.isDamageableItem //isDamageable  // .isDamageableItem
inline val ItemType.maxDamage: Int
    get() = vanillaStack.maxDamage
inline val ItemType.durability: Int
    get() = maxDamage - damage

inline val ItemType.isBucket: Boolean
    get() = item is BucketItem || item is MilkBucketItem || item is PowderSnowBucketItem

inline val ItemType.isFullBucket: Boolean
    get() = (item is BucketItem && item != Items.BUCKET) || item is MilkBucketItem || item is PowderSnowBucketItem

inline val ItemType.isEmptyBucket: Boolean
    get() {
        return item == Items.BUCKET
    }

fun ItemType.isEmptyComparedTo(other: ItemType): Boolean {
    val otherItem = other.item
    Log.trace("isEmpty item: ${item.javaClass}")
    Log.trace("isEmpty otherItem: ${item.javaClass}")
    return if ((item is MilkBucketItem || item is PowderSnowBucketItem) && otherItem is BucketItem && otherItem.fluid == Fluids.EMPTY) {
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
    return if (item == Items.BUCKET && (otherItem is MilkBucketItem || otherItem is PowderSnowBucketItem)) {
        true
    } else item !is EntityBucketItem && otherItem is EntityBucketItem

}

inline val ItemType.isHoneyBottle: Boolean
    get() = item == Items.HONEY_BOTTLE

inline val ItemType.isStew: Boolean
    get() = item is MushroomStewItem || item is SuspiciousStewItem

//region ItemType Potion Relative

inline val ItemType.hasPotionName: Boolean
    get() = tag?.contains("Potion", 8) ?: false
inline val ItemType.potionName: String
    // getPotionTypeFromNBT().getNamePrefixed() = getPotion().finishTranslationKey()
    get() = if (hasPotionName) PotionUtil.getPotion(tag).getName("") else "" //getPotionTypeFromNBT(tag).getNamePrefixed("") else "" // getPotion(tag).getName("") else ""
inline val ItemType.hasPotionEffects: Boolean
    get() = PotionUtil.getCustomEffects(tag)  //getEffectsFromTag(tag) // getCustomEffects(tag)
        .isNotEmpty() // forge getEffectsFromTag() = getPotionEffects()
inline val ItemType.hasCustomPotionEffects: Boolean
    get() = PotionUtil.getAllEffects(tag) //getFullEffectsFromTag(tag)// getAllEffects(tag)
        .isNotEmpty() // getCustomPotionEffects() = getFullEffectsFromTag
inline val ItemType.potionEffects: List<org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance>
    get() = PotionUtil.getCustomEffects(tag) //getFullEffectsFromTag(tag) // getCustomEffects(tag)
inline val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

@Suppress("ObjectPropertyName")
inline val StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(ForgeRegistries.MOB_EFFECTS.getKey(this.effect).toString(),
                         this.amplifier,
                         this.duration)

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

object ItemTypeExtensionsObject {
    val translationKeysPriorityList = mutableListOf<String>()

    fun priorityListChanged() {
        initGroupIndex()
        translationKeysPriorityList.clear()
        ItemGroups.allTabs().forEach {
            (it as IMixinItemGroup).ipnPriorityIndex = -1;
        }
        ModSettings.CATEGORY_PRIORITY_LIST.value.split(",").forEach {
            translationKeysPriorityList.add(it.trim())
        }
    }

    private val local: ThreadLocal<Boolean> = ThreadLocal<Boolean>().apply { set(true) }

    fun defaultOrderListChanged() {
        if (local.get()) {
            initGroupIndex()
            local.set(false)
            ModSettings.CATEGORY_ORIGINAL_ORDER.value = makeDefaultList()
            local.set(true)
        }
    }

    fun makeDefaultList(): String {
        var newValue = ""
        ItemGroups.allTabs().forEach {
            if (it.type == ItemGroupType.CATEGORY) {
                val content = it.displayName.contents
                if (content is TranslatableContents) {
                    newValue = if (newValue != "") "$newValue, ${content.key}" else content.key
                }
            }
        }
        return newValue
    }
}


//endregion
