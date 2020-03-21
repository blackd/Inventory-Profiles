package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.config.CategorizedConfigOptions
import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.gui.Tooltips
import io.github.jsnimda.common.vanilla.I18n
import io.github.jsnimda.common.vanilla.VanillaRender

private const val COLOR_WHITE = -0x1
private const val textY = 6

fun CategorizedConfigOptions.toWidget(displayNamePrefix: String, descriptionPrefix: String): ListConfigOptionsWidget =
  ListConfigOptionsWidget(displayNamePrefix, descriptionPrefix).apply {
    this@toWidget.categories.forEach { (categoryNameKey, configOptions) ->
      I18n.translate(categoryNameKey).let { categoryName ->
        addAnchor(categoryName)
        addEntry(CategoryEntry(categoryName))
        configOptions.forEach { addEntry(ConfigOptionEntry(it)) }
      }
    }
  }

class ListConfigOptionsWidget(private val displayNamePrefix: String, private val descriptionPrefix: String) :
  AnchoredListWidget() {

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.render(mouseX, mouseY, partialTicks)
    Tooltips.renderAll()
  }

  inner class ConfigOptionEntry(val configOption: IConfigOption) : Entry() {
    val displayName
      get() = I18n.translate(displayNamePrefix + configOption.key)
    val description
      get() = I18n.translate(descriptionPrefix + configOption.key)

    val optionWidget: ConfigOptionBaseWidget<*> = configOption.toWidget().apply {
      anchor = AnchorStyles.all
      this@ConfigOptionEntry.widgets.add(this)
      top = 0
      left = this@ConfigOptionEntry.width / 2
      right = 0
      bottom = 0
    }

    val displayNameTextWidget = TextButtonWidget(displayName).apply {
      clickThrough = true
      this@ConfigOptionEntry.widgets.add(this)
      top = textY
      left = 2
      zIndex = 1
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
      if (outOfContainer) return
      super.render(mouseX, mouseY, partialTicks)
      if (displayNameTextWidget.isMouseOver(mouseX, mouseY) && !anchorHeader.isMouseOver(mouseX, mouseY)) {
        Tooltips.addTooltip(description, mouseX, mouseY, VanillaRender.screenWidth * 2 / 3)
      }
    }

    init {
      sizeChanged += {
        optionWidget.left = width / 2
      }
    }
  }

  inner class CategoryEntry(private val categoryName: String) : Entry() {
    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
      if (outOfContainer) return
      VanillaRender.drawCenteredString(categoryName, screenX + width / 2, screenY + textY, COLOR_WHITE)
    }
  }

  open inner class Entry : Widget() {
    init {
      height = 20
    }

    val outOfContainer: Boolean
      get() = isOutOfContainer(this)
  }

}