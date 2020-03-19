package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.gui.widget.FlowLayout.FlowDirection.TOP_DOWN
import io.github.jsnimda.common.gui.widget.Overflow.VISIBLE
import io.github.jsnimda.common.vanilla.VHLine.fill
import io.github.jsnimda.common.vanilla.VHLine.h
import io.github.jsnimda.common.vanilla.VHLine.outline
import io.github.jsnimda.common.vanilla.VanillaRender

private const val COLOR_ANCHOR_BORDER = -0x7f666667
private const val COLOR_ANCHOR_BG = 0x40999999
private const val COLOR_ANCHOR_BORDER_HOVER = -0x666667
private const val COLOR_ANCHOR_BG_HOVER = -0x3f666667
private const val COLOR_WHITE = -0x1
private const val COLOR_BORDER = -0x666667

private const val rowHeight = 13
private const val leastY = 7
private const val SEPARATOR_WIDTH = 10

open class AnchoredListWidget : Widget() {
  var anchorHeader = AnchorHeader()
  val container = ScrollableContainerWidget().apply {
    anchor = AnchorStyles.all
    this@AnchoredListWidget.widgets.add(this)
    top = anchorHeader.height
    right = 0
    bottom = 0
  }
  val containerContentFlowLayout = FlowLayout(container.contentContainer, TOP_DOWN)

  var renderBorder = true
  var borderColor = COLOR_BORDER

  init {
    overflow = VISIBLE
//    container.renderBorder = false
//    renderBorder = false
  }

  // ============
  // AnchorHeader
  // ============

  inner class AnchorHeader : Widget() {
    init {
      anchor = AnchorStyles.noBottom
      this@AnchoredListWidget.widgets.add(this)
      height = rowHeight
      right = 0
      zIndex = 1
    }

    fun addAnchor(displayText: String, toScrollY: Int = container.contentHeight) {
      anchorsManager.addAnchor(displayText, toScrollY)
    }

    private val dummyAnchor = Anchor("", 0, 0)

    inner class Anchor(var anchorDisplayText: String, val toScrollY: Int, var rowIndex: Int) {
      val textButtonWidget = TextButtonWidget(anchorDisplayText, { -> container.scrollY = toScrollY }).apply {
        hoverText = "§e§n$anchorDisplayText"
        inactiveText = "§e§n§l$anchorDisplayText"
        visible = false
        pressableMargin = 2
      }
    }

    val anchors: List<Anchor>
      get() = anchorsManager.anchors
    private var anchorsManager = AnchorsManager()

    private inner class AnchorsManager { // this class manage anchor adding
      private val ellipsisTextWidth = VanillaRender.getStringWidth(" ... ...")

      val width = this@AnchorHeader.width
      private val availableWidth
        get() = width - 20 - ellipsisTextWidth

      val anchors = mutableListOf<Anchor>()

      private var textLeft = 0
      private var textRowIndex = 0

      val textYPerRow
        get() = (rowHeight + 1) / 2 - 4
      val totalTextRow
        get() = textRowIndex + 1

      fun addAnchor(displayText: String, toScrollY: Int) {
        val textWidth = VanillaRender.getStringWidth("§n$displayText")
        if (textLeft + textWidth > availableWidth) {
          textLeft = 0
          textRowIndex++
        }
        textLeft += textWidth + SEPARATOR_WIDTH
        Anchor(displayText, toScrollY, textRowIndex).apply {
          anchors.add(this)
          this@AnchorHeader.widgets.add(this.textButtonWidget)
        }
      }
    }

    private var _expanded = false
    var expanded
      get() = _expanded
      set(value) {
        if (value) expand()
        else collaspe()
      }

    fun expand() {
      if (_expanded) return
      _expanded = true
      val leastTop = leastY - this@AnchoredListWidget.screenY
      this.top = (-rowHeight * highlightingAnchor.rowIndex).coerceAtLeast(leastTop)
      this.height = rowHeight * anchorsManager.totalTextRow + 1
      updateTexts()
    }

    fun collaspe() {
      if (!_expanded) return
      _expanded = false
      this.top = 0
      this.height = rowHeight
      updateTexts()
    }

    fun updateTexts() {
      val startLeft = 10
      var textLeft = startLeft
      var textTop = anchorsManager.textYPerRow
      var lastRowIndex = 0
      anchors.forEachIndexed { index, anchor ->
        anchor.textButtonWidget.visible = expanded || highlightingAnchor.rowIndex == anchor.rowIndex
        if (anchor.textButtonWidget.visible) anchor.textButtonWidget.apply {
          if (lastRowIndex != anchor.rowIndex) {
            lastRowIndex = anchor.rowIndex
            textLeft = startLeft
            textTop += if (expanded) rowHeight else 0
          }
          top = textTop
          left = textLeft
          textLeft += width + SEPARATOR_WIDTH
        }
      }
    }

    private var lastHighlightingAnchorIndex = 0
      set(value) {
        if (field != value) {
          field = value
          updateTexts()
        }
      }
    private val highlightingAnchorIndex: Int // highlight base on scrollY
      get() {
        val scrollY = container.scrollY
        anchors.forEachIndexed { index, anchor ->
          if (anchor.toScrollY >= scrollY) {
            return if (anchor.toScrollY - scrollY <= container.viewport.height / 2) {
              index
            } else {
              (index - 1).coerceAtLeast(0)
            }
          }
        }
        return anchors.size - 1
      }
    private val highlightingAnchor
      get() = if (anchors.isEmpty()) dummyAnchor else anchors[highlightingAnchorIndex]
    private val highlightingRowLastAnchor: Anchor
      get() = highlightingAnchorIndex.let { startIndex ->
        if (anchors.isEmpty()) return dummyAnchor
        for (i in startIndex + 1 until anchors.size) {
          if (anchors[i].rowIndex != anchors[startIndex].rowIndex) return anchors[i - 1]
        }
        return anchors.last()
      }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
      expanded = isMouseOver(mouseX, mouseY) && anchorsManager.totalTextRow > 1
      val (x1, y1, x2, y2) = absoluteBounds
      h(x1, x2 - 1, y1, if (expanded) COLOR_ANCHOR_BORDER_HOVER else COLOR_ANCHOR_BORDER)
      h(x1, x2 - 1, y2, if (expanded) COLOR_ANCHOR_BORDER_HOVER else COLOR_ANCHOR_BORDER)
      fill(x1, y1 + 1, x2, y2, if (expanded) COLOR_ANCHOR_BG_HOVER else COLOR_ANCHOR_BG)
      lastHighlightingAnchorIndex = highlightingAnchorIndex
      if (!expanded && anchorsManager.totalTextRow > 1) {
        highlightingRowLastAnchor.textButtonWidget.run {
          VanillaRender.drawString(" ... ...", absoluteBounds.right, screenY, COLOR_WHITE)
        }
      }
    }

    fun sizeChanged() { // re calc
      if (width != anchorsManager.width) {
        widgets.clear()
        anchorsManager.let { old ->
          anchorsManager = AnchorsManager().also { new ->
            old.anchors.forEach { new.addAnchor(it.anchorDisplayText, it.toScrollY) }
          }
        }
      }
    }
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    if (renderBorder) {
      outline(absoluteBounds, borderColor)
    }
    super.render(mouseX, mouseY, partialTicks)
  }

  fun addEntry(entry: Widget) {
    containerContentFlowLayout.add(entry, entry.height)
    container.contentHeight = containerContentFlowLayout.offset
  }

  fun addAnchor(displayText: String) {
    anchorHeader.addAnchor(displayText)
  }

  fun isOutOfContainer(entry: Widget): Boolean =
      container.scrollY > entry.bounds.bottom || container.scrollY + container.viewport.height < entry.top

}