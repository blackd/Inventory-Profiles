package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.widget.*
import io.github.jsnimda.common.gui.widget.FlowLayout.FlowDirection.TOP_DOWN
import io.github.jsnimda.common.vanilla.Text
import io.github.jsnimda.common.vanilla.VanillaRender
import kotlin.math.max

private const val COLOR_WHITE = 0xFFFFFFFF.toInt()

open class ConfigScreenBase(text: Text) : BaseScreen(text) {

  var openConfigMenuHotkeyWidget: ConfigOptionHotkeyWidget? = null
    private set(value) {
      field?.parent = null
      field = value?.apply {
        anchor = AnchorStyles.topRight
        this@ConfigScreenBase.widgets.add(this)
        size = Size(150, 20)
        top = 5
        right = 10 // do set right after add
      }
    }

  var openConfigMenuHotkey: ConfigHotkey? = null
    set(value) {
      field = value
      openConfigMenuHotkeyWidget = value?.toWidget()
    }

  val navigationButtonsContainer = Widget().apply {
    anchor = AnchorStyles.noRight
    this@ConfigScreenBase.widgets.add(this)
    top = 30
    left = 10
    bottom = 0
  }

  private val navigationButtonsFlowLayout = FlowLayout(navigationButtonsContainer, TOP_DOWN)

  var currentConfigList: Widget? = null
    set(value) {
      field?.parent = null
      field = value?.apply {
        anchor = AnchorStyles.all
        this@ConfigScreenBase.widgets.add(this)
        top = 30
        left = 10 + navigationButtonsContainer.width + 5
        right = 10
        bottom = 10
        zIndex = 1
      }
    }

  private val navigationButtonsInfo = mutableListOf<Pair<String, () -> Unit>>()

  var selectedIndex = -1
    set(value) {
      if (value < 0 || value >= navigationButtonsContainer.widgets.size) {
        field = -1
        updateButtonsActive()
        selectedIndexChanged()
      } else if (value != field) {
        field = value
        updateButtonsActive()
        navigationButtonsInfo[value].second()
        selectedIndexChanged()
      }
    }

  open fun selectedIndexChanged() {}

  private fun updateButtonsActive() {
    navigationButtonsContainer.widgets.forEachIndexed { index, child ->
      child.active = selectedIndex != index
    }
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    VanillaRender.renderVanillaScreenBackground()
    VanillaRender.drawString(this.titleString, 20, 10, COLOR_WHITE)
    super.render(mouseX, mouseY, partialTicks)
  }

  fun addNavigationButton(buttonText: String, action: () -> Unit) {
    val id = navigationButtonsContainer.widgets.size
    navigationButtonsContainer.apply {
      width = max(width, VanillaRender.getStringWidth(buttonText) + 20)
    }
    navigationButtonsInfo.add(Pair(buttonText, action))
    navigationButtonsFlowLayout.add(ButtonWidget { ->
      selectedIndex = id
    }.apply {
      text = buttonText
    }, 20)
    navigationButtonsFlowLayout.addSpace(2)
  }

  fun addNavigationButtonWithWidget(buttonText: String, widgetSupplier: () -> Widget?) {
    addNavigationButton(buttonText) { currentConfigList = widgetSupplier() }
  }

  fun addNavigationButton(buttonText: String) {
    addNavigationButtonWithWidget(buttonText) { null }
  }

}
