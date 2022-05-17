package org.anti_ad.mc.ipnext.item

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.alias.Enchantment
import org.anti_ad.mc.common.vanilla.alias.EnchantmentHelper
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.ItemGroup
import org.anti_ad.mc.common.vanilla.alias.Items
import org.anti_ad.mc.common.vanilla.alias.PotionUtil
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.items.BucketItem
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
    tag?.keys?.forEach {
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

fun ItemType.isEmpty(): Boolean {
    return (item == Items.AIR).ifTrue {
        if (tag != null) Log.warn("Informal item type $this")
    }
}

inline val ItemType.maxCount: Int
    get() = vanillaStack.maxCount

inline val ItemType.vanillaStack: VanillaItemStack
    get() = VanillaItemStack(this.item).apply { nbt = this@vanillaStack.tag } // nbt was tag

fun ItemType.vanillaStackWithCount(count: Int): VanillaItemStack =
    VanillaItemStack(this.item,
                     count).apply { nbt = this@vanillaStackWithCount.tag } // nbt was tag

inline val ItemType.identifier: Identifier
    get() = Registry.ITEM.`(getIdentifier)`(item)

inline val ItemType.namespace: String
    get() = identifier.namespace

//region ItemType String Relative

inline val ItemType.hasCustomName: Boolean
    get() = vanillaStack.hasCustomName()
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

inline val ItemType.groupIndex: Int
    get() = item.group?.index ?: when {
        item === Items.ENCHANTED_BOOK -> ItemGroup.TOOLS.index
        namespace == "minecraft" -> ItemGroup.MISC.index
        else -> ItemGroup.GROUPS.size
    }
inline val ItemType.rawId: Int
    get() = Registry.ITEM.`(getRawId)`(item)
inline val ItemType.damage: Int
    get() = vanillaStack.damage
inline val ItemType.enchantmentsScore: Double
    get() = EnchantmentHelper.get(vanillaStack).toList().fold(0.0) { acc, (enchantment, level) ->
        acc + if (enchantment.isCursed) -0.001 else level.toDouble() / enchantment.maxLevel
    } // cursed enchantments +0 scores

inline val ItemType.enchantments: MutableMap<Enchantment, Int>
    get() = EnchantmentHelper.get(vanillaStack)

inline val ItemType.isDamageable: Boolean
    get() = vanillaStack.isDamageable
inline val ItemType.maxDamage: Int
    get() = vanillaStack.maxDamage
inline val ItemType.durability: Int
    get() = maxDamage - damage

inline val ItemType.isBucket: Boolean
    get() = item is BucketItem || item is MilkBucketItem
inline val ItemType.isStew: Boolean
    get() = item is MushroomStewItem || item is SuspiciousStewItem
//endregion

//region ItemType Potion Relative

inline val ItemType.hasPotionName: Boolean
    get() = tag?.contains("Potion",
                          8) ?: false
inline val ItemType.potionName: String
    get() = if (hasPotionName) PotionUtil.getPotion(tag).finishTranslationKey("") else ""
inline val ItemType.hasPotionEffects: Boolean
    get() = PotionUtil.getPotionEffects(tag).isNotEmpty()
inline val ItemType.hasCustomPotionEffects: Boolean
    get() = PotionUtil.getCustomPotionEffects(tag).isNotEmpty()
inline val ItemType.potionEffects: List<StatusEffectInstance>
    get() = PotionUtil.getPotionEffects(tag)
inline val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

@Suppress("ObjectPropertyName")
inline val StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(
        Registry.STATUS_EFFECT.getId(this.effectType).toString(),
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
