package org.anti_ad.mc.ipnext.item


import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.alias.EnchantmentHelper
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.ItemGroup
import org.anti_ad.mc.common.vanilla.alias.Items
import org.anti_ad.mc.common.vanilla.alias.PotionUtil
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.items.BucketItem
import org.anti_ad.mc.common.vanilla.alias.items.FishBucketItem
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

val ItemType.Companion.EMPTY
    get() = ItemType(Items.AIR,
                     null)

fun ItemType.isEmpty(): Boolean {
    return (item == Items.AIR).ifTrue {
        if (tag != null) Log.warn("Informal item type $this")
    }
}

val ItemType.maxCount: Int
    get() = vanillaStack.maxCount

val ItemType.vanillaStack: VanillaItemStack
    get() = VanillaItemStack(this.item).apply { tag = this@vanillaStack.tag }

fun ItemType.vanillaStackWithCount(count: Int): VanillaItemStack =
    VanillaItemStack(this.item,
                     count).apply { tag = this@vanillaStackWithCount.tag }

val ItemType.identifier: Identifier
    get() = Registry.ITEM.`(getIdentifier)`(item)

val ItemType.namespace: String
    get() = identifier.namespace

//region ItemType String Relative

val ItemType.hasCustomName: Boolean
    get() = vanillaStack.hasCustomName()
val ItemType.customName: String
    get() = if (hasCustomName) displayName else ""
val ItemType.displayName: String
    get() = vanillaStack.name.string
val ItemType.translatedName: String
    get() = I18n.translate(translationKey)
val ItemType.itemId: String
    get() = identifier.toString()
val ItemType.translationKey: String
    get() = vanillaStack.translationKey
val ItemType.isStackable: Boolean
    get() = vanillaStack.isStackable

//endregion

//region ItemType Number Relative

val ItemType.groupIndex: Int
    get() = item.group?.index ?: when {
        item === Items.ENCHANTED_BOOK -> ItemGroup.TOOLS.index
        namespace == "minecraft" -> ItemGroup.MISC.index
        else -> ItemGroup.GROUPS.size
    }
val ItemType.rawId: Int
    get() = Registry.ITEM.`(getRawId)`(item)
val ItemType.damage: Int
    get() = vanillaStack.damage
val ItemType.enchantmentsScore: Double
    get() = EnchantmentHelper.getEnchantments(vanillaStack).toList().fold(0.0) { acc, (enchantment, level) ->
        acc + if (enchantment.isCursed) -0.001 else level.toDouble() / enchantment.maximumLevel
    } // cursed enchantments +0 scores

val ItemType.isDamageable: Boolean
    get() = vanillaStack.isDamageable
val ItemType.maxDamage: Int
    get() = vanillaStack.maxDamage
val ItemType.durability: Int
    get() = maxDamage - damage

val ItemType.isBucket: Boolean
    get() = item is BucketItem || item is MilkBucketItem || item is FishBucketItem
val ItemType.isStew: Boolean
    get() = item is MushroomStewItem || item is SuspiciousStewItem
//endregion

//region ItemType Potion Relative

val ItemType.hasPotionName: Boolean
    get() = tag?.contains("Potion",
                          8) ?: false
val ItemType.potionName: String
    get() = if (hasPotionName) PotionUtil.getPotion(tag).finishTranslationKey("") else ""
val ItemType.hasPotionEffects: Boolean
    get() = PotionUtil.getPotionEffects(tag).isNotEmpty()
val ItemType.hasCustomPotionEffects: Boolean
    get() = PotionUtil.getCustomPotionEffects(tag).isNotEmpty()
val ItemType.potionEffects: List<StatusEffectInstance>
    get() = PotionUtil.getPotionEffects(tag)
val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

val StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(
        Registry.STATUS_EFFECT.getId(this.effectType).toString(),
        this.amplifier,
        this.duration
    )

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

//endregion
