package io.github.jsnimda.inventoryprofiles.item

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.item.ItemStack as VanillaItemStack

// ==========
// #! Vanilla mapping dependence
// ==========

// TODO need to verify

val ItemType.vanillaStack: VanillaItemStack
  get() = VanillaItemStack(this.item).apply { tag = this@vanillaStack.tag }

val ItemType.identifier: Identifier
  get() = Registry.ITEM.getId(item)
val ItemType.namespace: String
  get() = identifier.namespace

//region String Relative

val ItemType.hasCustomName: Boolean
  get() = vanillaStack.hasCustomName()
val ItemType.customName: String
  get() = if (hasCustomName) displayName else ""
val ItemType.displayName: String
  get() = vanillaStack.name.string
val ItemType.translatedName: String
  get() = vanillaStack.let { item.getName(it) }.string
val ItemType.itemId: String
  get() = identifier.toString()
val ItemType.translationKey: String
  get() = vanillaStack.translationKey

//endregion

//region Number Relative

val ItemType.groupIndex: Int
  get() = item.group?.index ?: when {
    item === Items.ENCHANTED_BOOK -> ItemGroup.TOOLS.index
    namespace == "minecraft" -> ItemGroup.MISC.index
    else -> ItemGroup.GROUPS.size
  }
val ItemType.rawId: Int
  get() = Item.getRawId(item)
val ItemType.damage: Int
  get() = vanillaStack.damage
val ItemType.enchantmentsScore: Double
  get() = EnchantmentHelper.getEnchantments(vanillaStack).toList().fold(0.0) { acc, (key, value) ->
    acc + if (key.isCursed) 0.0 else value / key.maximumLevel.toDouble()
  } // cursed enchantments +0 scores

//endregion

//region Potion Relative

val ItemType.hasPotionEffects: Boolean
  get() = PotionUtil.getPotionEffects(tag).isEmpty()
val ItemType.hasCustomPotionEffects: Boolean
  get() = PotionUtil.getCustomPotionEffects(tag).isEmpty()
val ItemType.hasPotionName: Boolean
  get() = tag?.contains("Potion", 8) ?: false
val ItemType.potionName: String
  get() = if (hasPotionName) PotionUtil.getPotion(tag).finishTranslationKey("") else ""
// todo potion_effects PotionUtil.getPotionEffects compareEffects

//endregion
