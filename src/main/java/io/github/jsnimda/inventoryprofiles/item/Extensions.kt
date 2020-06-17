package io.github.jsnimda.inventoryprofiles.item

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.min
import net.minecraft.item.ItemStack as VanillaItemStack

// ==========
// #! Vanilla mapping dependence
// ==========

val VanillaItemStack.`(itemType)`
  get() = ItemType(item, tag)
val VanillaItemStack.`(itemStack)`
  get() = if (isEmpty) ItemStack.EMPTY else ItemStack(`(itemType)`, count)

// ============
// ItemStack
// ============

val ItemStack.Companion.EMPTY
  get() = ItemStack(ItemType.EMPTY, 0)

fun ItemStack.isEmpty() =
  itemType.isEmpty() || count <= 0

fun ItemStack.setEmpty() {
  itemType = ItemType.EMPTY
  count = 0
}

fun ItemStack.isFull() =
  count >= itemType.maxCount

val ItemStack.room
  get() = itemType.maxCount - count // todo need check empty?

fun ItemStack.swapWith(another: ItemStack) {
  itemType = another.itemType.also { another.itemType = itemType }
  count = another.count.also { another.count = count }
}

fun ItemStack.stackableWith(b: ItemStack) =
  itemType == b.itemType || isEmpty() || b.isEmpty()

fun ItemStack.transferTo(another: ItemStack) = transferNTo(another, count)
fun ItemStack.transferOneTo(another: ItemStack) = transferNTo(another, 1)
fun ItemStack.transferNTo(another: ItemStack, n: Int) {
  if (!stackableWith(another)) return
  if (isEmpty()) return
  if (another.isEmpty()) {
    another.itemType = itemType
    another.count = 0
  }
  val transferableCount = n.coerceAtMost(min(count, another.room)).coerceAtLeast(0)
  count -= transferableCount
  another.count += transferableCount
  if (isEmpty()) setEmpty()
  if (another.isEmpty()) another.setEmpty()
}

fun ItemStack.splitHalfTo(cursor: ItemStack) { // for odd count, cursor more target less
  transferNTo(cursor, count - count / 2)
}

// ============
// ItemType
// ============

val ItemType.Companion.EMPTY
  get() = ItemType(Items.AIR, null)

fun ItemType.isEmpty() =
  item == Items.AIR

val ItemType.maxCount
  get() = vanillaStack.maxCount

// TODO need to verify

val ItemType.vanillaStack: VanillaItemStack
  get() = VanillaItemStack(this.item).apply { tag = this@vanillaStack.tag }

val ItemType.identifier: Identifier
  get() = Registry.ITEM.getId(item)
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
  get() = vanillaStack.let { item.getName(it) }.string
val ItemType.itemId: String
  get() = identifier.toString()
val ItemType.translationKey: String
  get() = vanillaStack.translationKey

//endregion

//region ItemType Number Relative

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

//region ItemType Potion Relative

val ItemType.hasPotionName: Boolean
  get() = tag?.contains("Potion", 8) ?: false
val ItemType.potionName: String
  get() = if (hasPotionName) PotionUtil.getPotion(tag).finishTranslationKey("") else ""
val ItemType.hasPotionEffects: Boolean
  get() = PotionUtil.getPotionEffects(tag).isNotEmpty()
val ItemType.hasCustomPotionEffects: Boolean
  get() = PotionUtil.getCustomPotionEffects(tag).isNotEmpty()
val ItemType.potionEffects: List<StatusEffectInstance>
  get() = PotionUtil.getPotionEffects(tag)
val ItemType.potionEffectValue: List<EffectValue>
  get() = potionEffects.map { it.`(effectValue)` }
val StatusEffectInstance.`(effectValue)`: EffectValue
  get() = EffectValue(
    Registry.STATUS_EFFECT.getId(this.effectType).toString(),
    this.amplifier,
    this.duration
  )

data class EffectValue(val effect: String, val amplifier: Int, val duration: Int) : Comparable<EffectValue> {
  override fun compareTo(other: EffectValue): Int { // stronger first
    this.effect.compareTo(other.effect).let { if (it != 0) return it }
    other.amplifier.compareTo(this.amplifier).let { if (it != 0) return it }
    other.duration.compareTo(this.duration).let { if (it != 0) return it }
    return 0
  }
}

//endregion
