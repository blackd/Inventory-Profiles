package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.gui.BaseDebugScreen
import io.github.jsnimda.common.vanilla.ContainerScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.inventoryprofiles.inventory.InventoryUserActions
import io.github.jsnimda.inventoryprofiles.item.*

class DebugScreen : BaseDebugScreen() {
  override val strings: List<String>
    get() = super.strings + itemInfo().split("\n") +
        """
          |Hello~
          """.trimMargin().split("\n")

  val lastItemType: ItemType

  init {
    if (VanillaState.inGame()) {
      lastItemType = Vanilla.playerInventory().cursorStack?.`|itemType|` ?: ItemType.EMPTY
      InventoryUserActions.handleCloseContainer()
    } else {
      lastItemType = ItemType.EMPTY
    }
  }

  fun itemInfo(): String {
    if (!VanillaState.inGame()) return ""
    parent.let { parent ->
      if (parent !is ContainerScreen<*>) return ""
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
          |potionEffectValue: $potionEffectValue
          """.trimMargin()
      }
    }
  }
}