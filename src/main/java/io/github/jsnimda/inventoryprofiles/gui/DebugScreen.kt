package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.gui.debug.BaseDebugScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Slot
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.util.*

class DebugScreen : BaseDebugScreen() {
  inner class PageContainer : Page("Container") {
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
        val itemType = itemType
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
    val slot: Slot?
      get() = VanillaInGame.focusedSlot(parent)
    val itemType: ItemType
      get() = slot?.`(itemStack)`?.itemType ?: ItemType.EMPTY
  }

  inner class PageContainerScreen : Page("Container Screen") {
    override val content: List<String>
      get() {
        val screen = parent
        if (screen !is ContainerScreen<*>) return listOf()
        return screen.`(containerBounds)`.run {
          """
            |container
            |x: $x y: $y
            |width: $width height: $height
            """.trimMargin().split("\n")
        }
      }
  }

  init {
    if (parent is ContainerScreen<*>) {
      pages.add(PageContainer())
      pages.add(PageContainerScreen())
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