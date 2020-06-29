package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.gui.debug.BaseDebugScreen
import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Slot
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.util.*

class DebugScreen : BaseDebugScreen() {
  class PageContainer : Page("Container") {
    override val content: List<String>
      get() {
        val slot = slot
        val a = "slot: ${slot?.javaClass?.simpleName}"
        slot ?: return listOf(a)
        val b =
          slot.run {
            """invSlot: $`(invSlot)` id: $`(id)`
              |inventory: ${`(inventory)`.javaClass.simpleName}
              |x: $`(x)` y: $`(y)`
              |
              """.trimMargin()
          }
        val c = "itemType: $itemType"
        val d =
          itemType.run {
            listOf(
              ::identifier,
              ::namespace,
              ::hasCustomName,
              ::customName,
              ::displayName,
              ::translatedName,
              ::itemId,
              ::translationKey,
              ::groupIndex,
              ::rawId,
              ::damage,
              ::enchantmentsScore,
              ::hasPotionEffects,
              ::hasCustomPotionEffects,
              ::hasPotionName,
              ::potionName,
              ::potionEffects,
              ::potionEffectValues,
            ).joinToString("\n") { "${it.name}: ${it.get()}" }
          }
        return listOf(a, b, c, d).joinToString("\n").split("\n")
      }
    val slot: Slot? = VanillaInGame.focusedSlot()
    val itemType: ItemType = slot?.`(itemStack)`?.itemType ?: ItemType.EMPTY
  }

  init {
    if (parent is ContainerScreen<*>) {
      pages.add(PageContainer())
    }
    switchPage(storedPageIndex)
  }

  override fun closeScreen() {
    storedPageIndex = pageIndex
    super.closeScreen()
  }

  companion object {
    var storedPageIndex = 0
  }
}