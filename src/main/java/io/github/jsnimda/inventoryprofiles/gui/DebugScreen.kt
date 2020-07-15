package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.extensions.usefulName
import io.github.jsnimda.common.gui.debug.BaseDebugScreen
import io.github.jsnimda.common.gui.debug.DebugInfos
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.Container
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
              ::comparablePotionEffects,
              ::isDamageable,
              ::maxDamage,
              ::durability,
            ).joinToString("\n") { "${it.name}: ${it.get()}" }
          }
        return listOf(a, c, d).joinToString("\n").lines()
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
        val a = "slot: ${slot?.javaClass?.usefulName}"
        slot ?: return a
        val b =
          slot.run {
            """invSlot: $`(invSlot)` id: $`(id)`
              |inventory: ${`(inventory)`.javaClass.usefulName}
              |x: $`(left)` y: $`(top)`
              |
              """.trimMargin()
          }
        return "$a\n$b"
      }
  }

  inner class PageScreenInfo : Page("ScreenInfo") {
    override val content: List<String>
      get() = listOf(screen, focusedSlot, screenContainer, container).joinToString("\n").lines()
    val screen: String
      get() = "screen: ${parent?.javaClass?.name}"
    val focusedSlot: String
      get() = "focusedSlot: ${parent?.`(focusedSlot)`?.javaClass?.name}"
    val screenContainer: String
      get() = (parent as? ContainerScreen<*>)?.let { containerStringOf(it.`(container)`, "screenContainer") }
        ?: "screenContainer: null"
    val container: String
      get() = containerStringOf(Vanilla.container(), "container")

    fun containerStringOf(container: Container, title: String): String {
      return "$title: ${container.javaClass.name}"
    }
  }

  fun addContent(additionalContent: Page.() -> List<String>, page: Page): Page {
    return object : Page(page.name) {
      override val content: List<String>
        get() = page.content + page.additionalContent()

      override fun preRender(mouseX: Int, mouseY: Int, partialTicks: Float) {
        page.preRender(mouseX, mouseY, partialTicks)
      }

      override val widget: Widget
        get() = page.widget
    }
  }

  init {
    val parent = parent
    if (parent is ContainerScreen<*>) {
      val page0Plus = addContent({
        parent.`(containerBounds)`.run {
          """
            |
            |container
            |x: $x y: $y
            |width: $width height: $height
            |relative mouse
            |x: ${DebugInfos.mouseX - x} y: ${DebugInfos.mouseY - y}
            """.trimMargin().lines()
        }
      }, pages[0]) // todo better code
      pages[0] = page0Plus
      pages.add(PageContainer())
      pages.add(PageScreenInfo())
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