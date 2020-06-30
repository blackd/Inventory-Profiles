package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.gui.debug.BaseDebugScreen
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Slot
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.item.*

class DebugScreen : BaseDebugScreen() {
  inner class PageContainer : Page("Container") {
    override val content: List<String>
      get() {
        val slot = slot
        val a = if (slot == slot2) slot.content else "${slot.content}\n${slot2.content}"
        slot ?: return listOf(a)
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
        return listOf(a, c, d).joinToString("\n").split("\n")
      }
    val slot: Slot?
      get() = (parent as? ContainerScreen<*>)?.`(rawFocusedSlot)`
    val slot2: Slot?
      get() = parent?.`(focusedSlot)`
    val itemType: ItemType
      get() = slot?.`(itemStack)`?.itemType ?: ItemType.EMPTY
    val Slot?.content: String
      get() {
        val slot = this
        val a = "slot: ${slot?.javaClass?.simpleName}"
        slot ?: return a
        val b =
          slot.run {
            """invSlot: $`(invSlot)` id: $`(id)`
              |inventory: ${`(inventory)`.javaClass.simpleName}
              |x: $`(left)` y: $`(top)`
              |
              """.trimMargin()
          }
        return "$a\n$b"
      }
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