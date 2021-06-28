package org.anti_ad.mc.common.gui.debug

import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.extensions.mod
import org.anti_ad.mc.common.extensions.runIf
import org.anti_ad.mc.common.gui.screen.BaseOverlay
import org.anti_ad.mc.common.gui.widget.AnchorStyles
import org.anti_ad.mc.common.gui.widget.fillParent
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.render.*
import org.anti_ad.mc.common.vanilla.render.glue.*
import kotlin.math.sign

/*
  text bounds: (2 + rMeasureText(s)) x 9
      offset x 1 y 1
  margin 1
 */
open class BaseDebugScreen : BaseOverlay() {
    var textPosition // 0-3: top-left / top-right / bottom-left / bottom-right
            by detectable(0) { _, _ -> updateWidgets() }
    val isTop
        get() = textPosition in 0..1
    val isLeft
        get() = textPosition % 2 == 0

    var pageIndex = 0
    val pages = mutableListOf<Page>()
    val page: Page?
        get() = pages.getOrNull(pageIndex)

    val defaultPageNameWidget = HudText("[-1] null")
    var pageNameWidget = defaultPageNameWidget
    val hudTextContainer = Widget()

    abstract class Page(val name: String) {
        abstract val content: List<String>
        open fun preRender(mouseX: Int,
                           mouseY: Int,
                           partialTicks: Float) {
        } // evaluate before hud text

        open val widget: Widget // draw extra content, add after hud text
            get() = Widget()
    }

    class HudText(text: String) : Widget() {
        init {
            this.text = text
            size = Size(2 + rMeasureText(text),
                        9)
        }

        override fun render(mouseX: Int,
                            mouseY: Int,
                            partialTicks: Float) {
            if (text.isEmpty()) return
            rFillRect(absoluteBounds,
                      COLOR_HUD_TEXT_BG)
            rDrawText(text,
                      screenX + 1,
                      screenY + 1,
                      COLOR_HUD_TEXT)
        }
    }

    fun hudTextContains(mouseX: Int,
                        mouseY: Int): Boolean {
        return hudTextContainer.any {
            it.contains(mouseX,
                        mouseY)
        }
    }

    fun updateHudText() {
        val page = page ?: return
        hudTextContainer.clearChildren()
//    val texts = page.content.map { HudText(it) }
        val content = rWrapText(page.content.joinToString("\n"),
                                glue_rScreenWidth)
        val texts = content.lines().map { HudText(it) }
        texts.forEach { hudTextContainer.addChild(it) }
        val hudTexts = hudTextContainer.children.runIf(!isTop) { asReversed() }
        var dy = 1
        for (hudText in hudTexts) {
            hudText.anchor = AnchorStyles(isTop,
                                          !isTop,
                                          isLeft,
                                          !isLeft)
            if (isLeft) hudText.left = 1 else hudText.right = 1
            if (isTop) hudText.top = dy else hudText.bottom = dy
            dy += hudText.height
        }
    }

    fun updateWidgets() {
        clearWidgets()
        pageNameWidget = HudText(if (isLeft) "${page?.name} [$pageIndex]" else "[$pageIndex] ${page?.name}")
        addWidget(pageNameWidget)
        pageNameWidget.anchor = AnchorStyles.topOnly.copy(left = !isLeft,
                                                          right = isLeft)
        pageNameWidget.top = 1
        if (isLeft) // 0 or 2
            pageNameWidget.right = 1 else pageNameWidget.left = 1 // opposite side
        addWidget(hudTextContainer); hudTextContainer.fillParent()
        page?.widget?.let { addWidget(it); it.fillParent() } // page.widget
    }

    fun switchPage(index: Int) {
        if (pages.isEmpty()) return
        pageIndex = if (index in 0 until pages.size) index else 0
        updateWidgets()
    }

    override fun mouseClicked(d: Double,
                              e: Double,
                              i: Int): Boolean {
        val inc = if (VanillaUtil.shiftDown()) -1 else 1
        if (i == 1) switchPage((pageIndex + inc) mod pages.size) // right click
        return super.mouseClicked(d,
                                  e,
                                  i)
    }

    override fun mouseScrolled(d: Double,
                               e: Double,
                               f: Double): Boolean {
        textPosition = (textPosition + sign(-f).toInt()) mod 4
        return super.mouseScrolled(d,
                                   e,
                                   f)
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        page?.preRender(mouseX,
                        mouseY,
                        partialTicks)
        updateHudText()
//    if (hudTextContains(mouseX, mouseY)) {
//      textPosition = (textPosition + 1) % 2 // (textPosition + 1) % 4
//    }
        super.render(mouseX,
                     mouseY,
                     partialTicks)
    }

    // ============
    // Page 1
    // ============
    init {
        val page1 = object : Page("Input") {
            override val content: List<String>
                get() = DebugInfos.asTexts /*+
            """
              |
              |mouseX ${VanillaUtil.mouseX} mouseY ${VanillaUtil.mouseY}
              |lastMouseX ${VanillaUtil.lastMouseX} lastMouseY ${VanillaUtil.lastMouseY}
            """.trimMargin().lines()*/

            override fun preRender(mouseX: Int,
                                   mouseY: Int,
                                   partialTicks: Float) {
                DebugInfos.mouseX = mouseX
                DebugInfos.mouseY = mouseY
            }

            var toggleColor = 0
            override val widget = object : Widget() {
                init {
                    zIndex = 0
                }

                override fun render(mouseX: Int,
                                    mouseY: Int,
                                    partialTicks: Float) {
                    if (toggleColor < 2) {
                        val color = if (toggleColor == 0) COLOR_WHITE else COLOR_BLACK
                        rDrawVerticalLine(mouseX,
                                          1,
                                          height - 2,
                                          color)
                        rDrawHorizontalLine(1,
                                            width - 2,
                                            mouseY,
                                            color)
                    }
                }

                override fun mouseClicked(x: Int,
                                          y: Int,
                                          button: Int): Boolean {
                    if (button == 0) {
                        toggleColor = (toggleColor + 1) % 3
                    }
                    return super.mouseClicked(x,
                                              y,
                                              button)
                }
            }
        }
        pages.add(page1)
        switchPage(0)
    }
}