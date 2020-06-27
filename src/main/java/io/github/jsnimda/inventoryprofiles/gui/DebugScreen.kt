package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.gui.debug.BaseDebugScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Slot
import io.github.jsnimda.inventoryprofiles.inventory.GeneralInventoryActions
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.util.`(id)`
import io.github.jsnimda.inventoryprofiles.util.`(invSlot)`
import io.github.jsnimda.inventoryprofiles.util.focusedSlot

class DebugScreen : BaseDebugScreen() {
  override val strings: List<String>
    get() = super.strings + itemInfo().split("\n") +
        """
          |slot: ${currentSlot?.run { "invSlot: $`(invSlot)` id: $`(id)`" }
        }
          """.trimMargin().split("\n")

  val lastItemType: ItemType
  val currentSlot: Slot?

  init {
    if (VanillaState.inGame()) {
      lastItemType = Vanilla.playerInventory().cursorStack?.`(itemType)` ?: ItemType.EMPTY
      GeneralInventoryActions.handleCloseContainer()
    } else {
      lastItemType = ItemType.EMPTY
    }
    currentSlot = VanillaInGame.focusedSlot()
  }

  var mode = 0

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
    super.mouseClicked(d, e, i).also { if (i == 1) mode = (mode + 1) % 2 }

  fun itemInfo(): String {
    if (!VanillaState.inGame()) return ""
    parent.let { parent ->
      if (parent !is ContainerScreen<*>) return ""
      if (mode != 0) return ""
      return lastItemType.run {
        """this: $this
          |identifier: $identifier
          |namespace: $namespace
          |hasCustomName: $hasCustomName
          |customName: $customName
          |displayName: $displayName
          |translatedName: $translatedName
          |itemId: $itemId
          |translationKey: $translationKey
          |groupIndex: $groupIndex
          |rawId: $rawId
          |damage: $damage
          |enchantmentsScore: $enchantmentsScore
          |hasPotionEffects: $hasPotionEffects
          |hasCustomPotionEffects: $hasCustomPotionEffects
          |hasPotionName: $hasPotionName
          |potionName: $potionName
          |potionEffects: $potionEffects
          |potionEffectValues: $potionEffectValues
          """.trimMargin()
      }
    }
  }
}