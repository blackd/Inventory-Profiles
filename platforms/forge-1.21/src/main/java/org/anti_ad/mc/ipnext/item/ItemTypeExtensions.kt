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

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceKey
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraftforge.common.extensions.IForgeFluid
import net.minecraftforge.registries.ForgeRegistries
import org.anti_ad.mc.alias.component.ComponentChanges
import org.anti_ad.mc.alias.component.ComponentMapImpl
import org.anti_ad.mc.alias.component.DataComponentTypes
import org.anti_ad.mc.alias.component.type.FoodComponent
import org.anti_ad.mc.alias.entity.effect.StatusEffectInstance
import org.anti_ad.mc.alias.fluid.Fluids
import org.anti_ad.mc.alias.item.BucketItem
import org.anti_ad.mc.alias.item.EntityBucketItem
import org.anti_ad.mc.alias.item.ItemGroup
import org.anti_ad.mc.alias.item.ItemGroupType
import org.anti_ad.mc.alias.item.ItemGroups
import org.anti_ad.mc.alias.item.Items
import org.anti_ad.mc.alias.item.MilkBucketItem
import org.anti_ad.mc.alias.item.PowderSnowBucketItem
import org.anti_ad.mc.alias.item.SuspiciousStewItem
import org.anti_ad.mc.alias.nbt.NbtElement
import org.anti_ad.mc.alias.nbt.NbtOps
import org.anti_ad.mc.alias.potion.Potion
import org.anti_ad.mc.alias.registry.Registries
import org.anti_ad.mc.alias.registry.tag.EnchantmentTags
import org.anti_ad.mc.alias.util.Identifier
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.Vanilla

import org.anti_ad.mc.common.vanilla.alias.glue.I18n

import org.anti_ad.mc.ipnext.config.CreativeMenuSortOrder
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(getRawId)`
import org.anti_ad.mc.ipnext.ingame.`(itemType)`
import org.anti_ad.mc.ipnext.mixinhelpers.IMixinItemGroup
import java.util.*
import kotlin.Comparator
import org.anti_ad.mc.alias.item.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

fun ItemType.fullItemInfoAsJson(): String {
    var res = "{" + "\n\t\"id\" : \"" + this.itemId + "\",\n"
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
                     ComponentMapImpl(Items.AIR.components()),
                     ComponentChanges.EMPTY,
                     { false })

fun ItemType.isEmpty() : Boolean {
    return item == Items.AIR
}

inline val ItemType.maxCount: Int
    get() = vanillaStack.maxStackSize

inline val ItemType.searchItemStack: VanillaItemStack
    get() {
        return VanillaItemStack(this.item, 1)
    }

inline val ItemType.vanillaStack: VanillaItemStack
    get() = VanillaItemStack(this.item, 1, this@vanillaStack.tag ?: ComponentMapImpl.EMPTY as ComponentMapImpl)

fun ItemType.vanillaStackWithCount(count: Int): VanillaItemStack =
        VanillaItemStack(this.item, count, this@vanillaStackWithCount.tag ?: ComponentMapImpl.EMPTY as ComponentMapImpl)

inline val ItemType.identifier: Identifier
    get() = ForgeRegistries.ITEMS.getKey(item)!! // `(getIdentifier)`(item)

inline val ItemType.namespace: String
    get() = identifier.namespace

inline val ItemType.itemClass
    get() = item.javaClass

//region ItemType String Relative

inline val ItemType.hasCustomName: Boolean
    get() = vanillaStack.components[DataComponentTypes.CUSTOM_NAME] != null
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

        val index = ItemGroups.searchTab().searchTabDisplayItems.indexOfFirst { it ->
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
    get() = item.components().get(DataComponentTypes.FOOD) ?: error("this shouldn't happen")

inline val ItemType.`(isFood)`: Boolean
    get() = item.components().get(DataComponentTypes.FOOD) != null


inline val FoodComponent.`(statusEffects)`: List<Pair<StatusEffectInstance, Float>>
    get() = mutableListOf<Pair<StatusEffectInstance, Float>>().also { ls ->
        this.effects.forEach {
            ls.add(Pair(it.effect, it.probability))
        }
    }

inline val FoodComponent.`(isHarmful)`: Boolean
    get() = run {
        var res = false
        run fastEnd@{
            this.`(statusEffects)`.forEach {
                if (it.first.effect.value().category == MobEffectCategory.HARMFUL) {
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
                stack -> stack?.componentsPatch
        }).apply(instance) { changes: ComponentChanges ->
            VanillaItemStack(ForgeRegistries.ITEMS.getHolder(Items.AIR).get(), 0, changes)
        }
    }
}

fun VanillaItemStack.`(componentsToCodecList)`(): MutableList<Codec<*>> {
    //PairCodec<ComponentType<*>, Optional<*>>
    val res = mutableListOf<Codec<*>>()
    if (!this.componentsPatch.isEmpty) {
        this.componentsPatch.entrySet().forEach {
            res.add(it.key.codecOrThrow())
        }
    }
    return res
}


inline val ItemType.`(componentsToNbt)`: NbtElement?
    get() = if (!this.isEmpty()) {
        COMPONENTS_CHANGES_CODEC.encodeStart(Vanilla.world().registryAccess().createSerializationContext(NbtOps.INSTANCE), this.vanillaStack).getOrThrow();
    } else {
        null
    }

@Suppress("DEPRECATION")
inline val ItemType.rawId: Int
    get() = Registries.ITEM.`(getRawId)`(item)
inline val ItemType.damage: Int
    get() = vanillaStack.damageValue //damage // .damageValue
inline val ItemType.`(enchantmentsScore)`: Double
    //  get() = EnchantmentHelper.get(vanillaStack).toList().fold(0.0) { acc, (enchantment, level) ->
    get() = EnchantmentHelper.getEnchantmentsForCrafting(vanillaStack).entrySet().fold(0.0) { acc, (enchantment, level) ->
        acc + if (enchantment.`is`(EnchantmentTags.CURSE)) -0.001 else level.toDouble() / enchantment.value().maxLevel
    } // cursed enchantments +0 scores

inline val ItemType.`(enchantments)`: MutableMap<ResourceKey<Enchantment>, Int>
    get() = EnchantmentHelper.getEnchantmentsForCrafting(vanillaStack).entrySet().associateTo (mutableMapOf()) {
        it.key.unwrapKey().get() to it.intValue
    }

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
    get() = this.`(isFood)` && vanillaStack.components[DataComponentTypes.FOOD]?.`(convertsToBawl)` ?: false

//region ItemType Potion Relative

inline val ItemType.hasPotionName: Boolean
    get() = tag?.has(DataComponentTypes.POTION_CONTENTS) ?: false

inline val ItemType.potionName: String
    get() = if (tag != null && hasPotionName) Potion.getName(tag.get(DataComponentTypes.POTION_CONTENTS)?.potion ?: Optional.empty(), "") else ""

inline val ItemType.hasPotionEffects: Boolean
    get() = tag?.get(DataComponentTypes.POTION_CONTENTS)?.hasEffects() ?: false

inline val ItemType.hasCustomPotionEffects: Boolean
    get() = tag?.get(DataComponentTypes.POTION_CONTENTS)?.customEffects?.isNotEmpty() ?: false

inline val ItemType.potionEffects: List<StatusEffectInstance>
    get() = tag?.get(DataComponentTypes.POTION_CONTENTS)?.allEffects?.toList() ?: listOf()

inline val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

@Suppress("ObjectPropertyName")
inline val StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(ForgeRegistries.MOB_EFFECTS.getKey(this.effect.value()).toString(),
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
