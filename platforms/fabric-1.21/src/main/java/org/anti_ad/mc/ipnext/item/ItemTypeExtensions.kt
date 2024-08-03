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

@file:Suppress("NOTHING_TO_INLINE", "ObjectPropertyName")

package org.anti_ad.mc.ipnext.item

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.anti_ad.mc.alias.block.ShulkerBoxBlock
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.alias.component.*
import org.anti_ad.mc.alias.component.type.*
import org.anti_ad.mc.alias.enchantment.Enchantment
import org.anti_ad.mc.alias.enchantment.EnchantmentHelper
import org.anti_ad.mc.alias.entity.effect.StatusEffectCategory
import org.anti_ad.mc.alias.entity.effect.StatusEffectInstance
import org.anti_ad.mc.alias.item.BlockItem
import org.anti_ad.mc.alias.item.BucketItem
import org.anti_ad.mc.alias.item.ItemGroup
import org.anti_ad.mc.alias.item.ItemGroupType
import org.anti_ad.mc.alias.item.ItemGroups
import org.anti_ad.mc.alias.item.Items
import org.anti_ad.mc.alias.item.MilkBucketItem
import org.anti_ad.mc.alias.item.PowderSnowBucketItem
import org.anti_ad.mc.alias.nbt.NbtElement
import org.anti_ad.mc.alias.nbt.NbtOps
import org.anti_ad.mc.alias.potion.Potion
import org.anti_ad.mc.alias.registry.Registries
import org.anti_ad.mc.alias.registry.RegistryKey
import org.anti_ad.mc.alias.registry.tag.EnchantmentTags
import org.anti_ad.mc.alias.text.TranslatableTextContent
import org.anti_ad.mc.alias.util.Identifier
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.compat.integrations.Integrations
import org.anti_ad.mc.ipnext.config.CreativeMenuSortOrder
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(getIdentifier)`
import org.anti_ad.mc.ipnext.ingame.`(getRawId)`
import org.anti_ad.mc.ipnext.ingame.`(itemType)`
import org.anti_ad.mc.ipnext.mixin.IMixinBucketItem
import org.anti_ad.mc.ipnext.mixin.IMixinEntityBucketItem
import org.anti_ad.mc.ipnext.mixin.IMixinFluid
import org.anti_ad.mc.ipnext.mixinhelpers.IMixinItemGroup
import java.util.*
import org.anti_ad.mc.alias.item.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

fun ItemType.fullItemInfoAsJson(): String {
    var res = "{" + "\n\t\"id\" : \"" + this.itemId+ "\",\n"
    tag?.types?.forEach {
        res += "\t{\n\t\t\"$it\" : ${tag.get(it)}\n},"
    }
    return "$res\n}"
}


fun ItemType.toNamespacedString(): String { // like ItemType.toString() but with namespace
    return itemId + (tag ?: "")
}

inline val ItemType.Companion.EMPTY
    get() = ItemType(Items.AIR,
                     ComponentMapImpl(Items.AIR.components),
                     ComponentChanges.EMPTY,
                     { false })

fun ItemType.isEmpty(): Boolean {
    return (item == Items.AIR)
    //air has components now so removed the check for it
}

inline val ItemType.isShulker: Boolean
    get() = item is BlockItem && item.block is ShulkerBoxBlock

val ItemType.isEmptyShulker: Boolean
    get() {
        val iss = isShulker
        return iss && (tag?.get(DataComponentTypes.CONTAINER).isEmpty())
    }

private fun ContainerComponent?.isEmpty(): Boolean {
    return this == null || this === ContainerComponent.DEFAULT || this == ContainerComponent.DEFAULT
}

inline val ItemType.maxCount: Int
    get() {
        val carpetEmptyShulkersStackSize = Integrations.carpetEmptyShulkersStackSize
        return if (carpetEmptyShulkersStackSize > 1 && isEmptyShulker) {
            carpetEmptyShulkersStackSize
        } else {
            vanillaStack.maxCount
        }
    }

inline val ItemType.searchItemStack: VanillaItemStack
    get() {
        return VanillaItemStack(this.item, 1)
    }

inline val ItemType.vanillaStack: VanillaItemStack
    get() {
        val tag = ComponentMapImpl(this@vanillaStack.tag)
        tag.changes = this@vanillaStack.changes
        return VanillaItemStack(this.item, 1, tag)
    }

fun ItemType.vanillaStackWithCount(count: Int): VanillaItemStack =
    VanillaItemStack(this.item,
                     count,
                     this@vanillaStackWithCount.tag as ComponentMapImpl) // nbt was tag

inline val ItemType.identifier: Identifier
    get() = Registries.ITEM.`(getIdentifier)`(item)

inline val ItemType.namespace: String
    get() = identifier.namespace

inline val ItemType.itemClass
    get() = item.javaClass

//region ItemType String Relative

inline val ItemType.hasCustomName: Boolean
    get() = this.vanillaStack.components[DataComponentTypes.CUSTOM_NAME] != null
inline val ItemType.customName: String
    get() = if (hasCustomName) displayName else ""
inline val ItemType.displayName: String
    get() = vanillaStack.name.string
inline val ItemType.translatedName: String
    get() = I18n.translate(translationKey)
inline val ItemType.itemId: String
    get() = identifier.toString()
inline val ItemType.translationKey: String
    get() = vanillaStack.translationKey
inline val ItemType.isStackable: Boolean
    get() = vanillaStack.isStackable

inline val ItemType.customOrTranslatedName: String
    get() = if (hasCustomName) displayName else I18n.translate(translationKey)

//endregion

//region ItemType Number Relative



inline val ItemGroup.`(priorityIndex)`: Int
    get() {
        if (this is IMixinItemGroup) {
            var index = ipnPriorityIndex
            if (index == -1) {
                val content = this.displayName.content
                if (content is TranslatableTextContent) {
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
                        if (g1.row == g2.row) {
                            g1.column.compareTo(g2.column)
                        } else {
                            g1.row.compareTo(g2.row)
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
                        g1.`(priorityIndex)`.compareTo(g2.`(priorityIndex)`)
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
    val features = Vanilla.mc().player?.networkHandler?.enabledFeatures
    val lookup = Vanilla.mc().player?.world?.getRegistryManager()
    val opTabEnabled = Vanilla.mc().options.getOperatorItemsTab().getValue() && Vanilla.playerNullable()?.isCreativeLevelTwoOp ?: false
    return if (features != null && lookup != null) {
        ItemGroups.updateDisplayContext(features, opTabEnabled, lookup);
        true
    } else {
        false
    }
}

val ItemType.searchTabIndex: Int
    get() {
        initGroupIndex()
        val index = ItemGroups.getSearchGroup().searchTabStacks.indexOfFirst { it ->
            it != null && it.`(itemType)`.item == this.item
        }
        if (index == -1) {
            Log.warn("Item ${this.itemId} not present in the Creative Inventory Search Tab!")
        }
        return index
    }

val ItemType.groupIndex: Int
    get() {
        val stack = this.searchItemStack
        initGroupIndex()
        var result = Int.MAX_VALUE
        val grps = ItemGroups.getGroups().filter {
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

inline val ItemType.`(foodComponent)`: FoodComponent
    get() = item.components.get(DataComponentTypes.FOOD) ?: error("this shouldn't happen")

inline val ItemType.`(isFood)`: Boolean
    get() = item.components.get(DataComponentTypes.FOOD) != null


inline val FoodComponent.`(statusEffects)`: List<Pair<StatusEffectInstance, Float>>
    get() = mutableListOf<Pair<StatusEffectInstance, Float>>().also { ls ->
        this.effects.forEach {
            ls.add(Pair(it.effect, it.probability))
        }
    }

inline val FoodComponent.`(isHarmful)`: Boolean
    get() = run {
        var res = false
        run fastEnd@ {
            this.`(statusEffects)`.forEach {
                if (it.first.effectType.value()?.category == StatusEffectCategory.HARMFUL) {
                    res = true
                    return@fastEnd
                }

            }
        }
        res

    }
inline val FoodComponent.`(saturationModifier)`
    get() = this.saturation

inline val FoodComponent.`(convertsToBawl)`
    get() = this.usingConvertsTo.isPresent && this.usingConvertsTo.get().item == Items.BOWL

val COMPONENTS_CHANGES_CODEC: Codec<VanillaItemStack> = Codec.lazyInitialized {
    RecordCodecBuilder.create { instance ->
        instance.group(ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter {
            stack -> stack?.componentChanges
        }).apply(instance) { changes: ComponentChanges? ->
            VanillaItemStack(Registries.ITEM.getEntry(Items.AIR), 0, changes)
        }
    }
}

fun VanillaItemStack.`(componentsToCodecList)`(): MutableList<Codec<*>> {
    //PairCodec<ComponentType<*>, Optional<*>>
    val res = mutableListOf<Codec<*>>()
    if (!this.componentChanges.isEmpty) {
        this.componentChanges.entrySet().forEach {
            res.add(it.key.codecOrThrow)
        }
    }
    return res
}


inline val ItemType.`(componentsToNbt)`: NbtElement?
    get() = if (!this.isEmpty()) {
        COMPONENTS_CHANGES_CODEC.encodeStart(Vanilla.world().registryManager.getOps(NbtOps.INSTANCE), this.vanillaStack).getOrThrow();
    } else {
        null
    }

inline val ItemType.rawId: Int
    get() = Registries.ITEM.`(getRawId)`(item)
inline val ItemType.damage: Int
    get() = vanillaStack.damage

inline val ItemType.`(enchantmentsScore)`: Double
    get() = EnchantmentHelper.getEnchantments(vanillaStack).enchantmentEntries.fold(0.0) { acc, (enchantment, level) ->
        acc + if (enchantment.isIn(EnchantmentTags.CURSE)) -0.001 else level.toDouble() / enchantment.value().maxLevel
    } // cursed enchantments +0 scores

inline val ItemType.`(enchantments)`: MutableMap<RegistryKey<Enchantment>, Int>
    get() = EnchantmentHelper.getEnchantments(vanillaStack).enchantmentEntries.associateTo (mutableMapOf()) {
        it.key.key.get() to it.intValue
    }

inline val ItemType.isDamageable: Boolean
    get() = vanillaStack.isDamageable
inline val ItemType.maxDamage: Int
    get() = vanillaStack.maxDamage
inline val ItemType.durability: Int
    get() = if (isDamageableFn())  maxDamage - damage else 0

inline val ItemType.isBucket: Boolean
    get() = item is BucketItem || item is MilkBucketItem || item is PowderSnowBucketItem

inline val ItemType.isFullBucket: Boolean
    get() = item is MilkBucketItem || item is IMixinEntityBucketItem || item is PowderSnowBucketItem || (item is IMixinBucketItem && !(item.fluid as IMixinFluid).callIsEmpty())

inline fun ItemType.isEmptyComparedTo(other: ItemType): Boolean {
    val otherItem = other.item
    return if (item is MilkBucketItem && otherItem is IMixinBucketItem && (otherItem.fluid as IMixinFluid).callIsEmpty()) {
        true
    } else if (item is PowderSnowBucketItem && otherItem is IMixinBucketItem && (otherItem.fluid as IMixinFluid).callIsEmpty()) {
        true
    } else if (otherItem == Items.BUCKET && item is IMixinBucketItem && !(item.fluid as IMixinFluid).callIsEmpty()) {
        true
    } else item is IMixinEntityBucketItem && otherItem is IMixinBucketItem && otherItem !is IMixinEntityBucketItem
    //item is MilkBucketItem || item is IMixinEntityBucketItem || (item is IMixinBucketItem && !(item.fluid as IMixinFluid).callIsEmpty())
}

inline fun ItemType.isFullComparedTo(other: ItemType): Boolean {
    val otherItem = other.item
    return if (item == Items.BUCKET && otherItem is MilkBucketItem) {
        true
    } else if (item == Items.BUCKET && otherItem is PowderSnowBucketItem) {
        true
    } else item !is IMixinEntityBucketItem && otherItem is IMixinEntityBucketItem

}


inline val ItemType.isEmptyBucket: Boolean
    get() {
        return item !is IMixinEntityBucketItem && item is IMixinBucketItem && (item.fluid as IMixinFluid).callIsEmpty()
    }

inline val ItemType.isHoneyBottle: Boolean
    get() = item == Items.HONEY_BOTTLE

inline val ItemType.isStew: Boolean
    get() = this.`(isFood)` && vanillaStack.components[DataComponentTypes.FOOD]?.`(convertsToBawl)` ?: false

//endregion

//region ItemType Potion Relative

inline val ItemType.hasPotionName: Boolean
    get() = tag?.contains(DataComponentTypes.POTION_CONTENTS) ?: false

inline val ItemType.potionName: String
    get() = if (tag != null && hasPotionName) Potion.finishTranslationKey(tag.get(DataComponentTypes.POTION_CONTENTS)?.potion ?: Optional.empty(), "") else ""

inline val ItemType.hasPotionEffects: Boolean
    get() = tag?.get(DataComponentTypes.POTION_CONTENTS)?.hasEffects() ?: false

inline val ItemType.hasCustomPotionEffects: Boolean
    get() = tag?.get(DataComponentTypes.POTION_CONTENTS)?.customEffects?.isNotEmpty() ?: false

inline val ItemType.potionEffects: List<StatusEffectInstance>
    get() = tag?.get(DataComponentTypes.POTION_CONTENTS)?.effects?.toList() ?: listOf()

inline val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

@Suppress("ObjectPropertyName")
inline val StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(Registries.STATUS_EFFECT.getId(this.effectType.value()).toString(),
                         this.amplifier,
                         this.duration)

data class PotionEffect(val effect: String,
                        val amplifier: Int,
                        val duration: Int) : Comparable<PotionEffect> {
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
        ItemGroups.getGroups().forEach {
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
        ItemGroups.getGroups().forEach {
            if (it.type == ItemGroupType.CATEGORY) {
                val content = it.displayName.content
                if (content is TranslatableTextContent) {
                    newValue = if (newValue != "") "$newValue, ${content.key}" else content.key
                }
            }
        }
        return newValue
    }

}

//endregion
