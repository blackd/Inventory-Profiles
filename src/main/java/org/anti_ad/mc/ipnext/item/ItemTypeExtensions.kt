package org.anti_ad.mc.ipnext.item

import org.anti_ad.mc.ipnext.ingame.`(getIdentifier)`
import org.anti_ad.mc.ipnext.ingame.`(getRawId)`
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.vanilla.alias.items.*
import org.anti_ad.mc.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

fun ItemType.toNamespacedString(): String { // like ItemType.toString() but with namespace
    return itemId + (tag ?: "")
}

val ItemType.Companion.EMPTY
    get() = ItemType(org.anti_ad.mc.common.vanilla.alias.Items.AIR,
                     null)

fun ItemType.isEmpty() =
    item == org.anti_ad.mc.common.vanilla.alias.Items.AIR

val ItemType.maxCount: Int
    get() = vanillaStack.maxStackSize

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
    get() = vanillaStack.hasCustomHoverName()
val ItemType.customName: String
    get() = if (hasCustomName) displayName else ""
val ItemType.displayName: String
    get() = vanillaStack.displayName.string
val ItemType.translatedName: String
    get() = I18n.translate(translationKey)
val ItemType.itemId: String
    get() = identifier.toString()
val ItemType.translationKey: String
    get() = vanillaStack.descriptionId

//endregion

//region ItemType Number Relative

val ItemType.groupIndex: Int
    get() = item.itemCategory?.id ?: when {
        item === org.anti_ad.mc.common.vanilla.alias.Items.ENCHANTED_BOOK -> org.anti_ad.mc.common.vanilla.alias.ItemGroup.TAB_TOOLS.id
        namespace == "minecraft" -> org.anti_ad.mc.common.vanilla.alias.ItemGroup.TAB_MISC.id
        else -> org.anti_ad.mc.common.vanilla.alias.ItemGroup.TABS.size
    }
val ItemType.rawId: Int
    get() = Registry.ITEM.`(getRawId)`(item)
val ItemType.damage: Int
    get() = vanillaStack.damageValue
val ItemType.enchantmentsScore: Double
    //  get() = EnchantmentHelper.get(vanillaStack).toList().fold(0.0) { acc, (enchantment, level) ->
    get() = org.anti_ad.mc.common.vanilla.alias.EnchantmentHelper.getEnchantments(vanillaStack).toList()
        .fold(0.0) { acc, (enchantment, level) ->
            acc + if (enchantment.isCurse) -0.001 else level.toDouble() / enchantment.maxLevel
        } // cursed enchantments +0 scores

val ItemType.isDamageable: Boolean
    get() = vanillaStack.isDamageableItem
val ItemType.maxDamage: Int
    get() = vanillaStack.maxDamage
val ItemType.durability: Int
    get() = maxDamage - damage

val ItemType.isBucket: Boolean
    get() = item is BucketItem || item is MilkBucketItem || item is FishBucketItem
val ItemType.isStew: Boolean
    get() = item is MushroomStewItem || item is SuspiciousStewItem // SoupItem = MushroomStewItem
//endregion

//region ItemType Potion Relative

val ItemType.hasPotionName: Boolean
    get() = tag?.contains("Potion",
                          8) ?: false
val ItemType.potionName: String
    // getPotionTypeFromNBT().getNamePrefixed() = getPotion().finishTranslationKey()
    get() = if (hasPotionName) org.anti_ad.mc.common.vanilla.alias.PotionUtil.getPotion(tag).getName("") else ""
val ItemType.hasPotionEffects: Boolean
    get() = org.anti_ad.mc.common.vanilla.alias.PotionUtil.getCustomEffects(tag)
        .isNotEmpty() // forge getEffectsFromTag() = getPotionEffects()
val ItemType.hasCustomPotionEffects: Boolean
    get() = org.anti_ad.mc.common.vanilla.alias.PotionUtil.getAllEffects(tag)
        .isNotEmpty() // getCustomPotionEffects() = getFullEffectsFromTag
val ItemType.potionEffects: List<org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance>
    get() = org.anti_ad.mc.common.vanilla.alias.PotionUtil.getCustomEffects(tag)
val ItemType.comparablePotionEffects: List<PotionEffect>
    get() = potionEffects.map { it.`(asComparable)` }

val org.anti_ad.mc.common.vanilla.alias.StatusEffectInstance.`(asComparable)`: PotionEffect
    get() = PotionEffect(
        Registry.MOB_EFFECT.getId(this.effect).toString(), // forge EFFECTS = STATUS_EFFECT | effectType = potion
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
