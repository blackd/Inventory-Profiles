package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.gui.Tooltips
import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.gui.widgets.TextButtonWidget
import io.github.jsnimda.common.gui.widgets.toConfigWidget
import io.github.jsnimda.common.input.ConfigKeybindSettings
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.TranslatableText
import io.github.jsnimda.common.vanilla.render.rDrawCenteredText
import io.github.jsnimda.common.vanilla.render.rMeasureText
import io.github.jsnimda.common.vanilla.render.rScreenWidth
import kotlin.math.max

private const val COLOR_WHITE = -0x1

class ConfigOptionHotkeyDialog(val configHotkey: ConfigHotkey) :
  BaseDialog(TranslatableText("inventoryprofiles.common.gui.config.advanced_keybind_settings")) {
  private val keybindSettingElement =
    with(configHotkey.mainKeybind) { ConfigKeybindSettings(defaultSettings, settings) }
  val configs = keybindSettingElement.getConfigOptionList()

  private val IConfigOption.displayName
    get() = I18n.translate("inventoryprofiles.common.gui.config.$key")
  private val IConfigOption.description
    get() = I18n.translate("inventoryprofiles.common.gui.config.description.$key")

  private val maxTextWidth = configs.map { rMeasureText(it.displayName) }.maxOrNull() ?: 0

  var showTooltips = false

  init {
    val dialogHeight = (configs.size + 1) * 20 + 2 + 10
    val dialogWidth = max(maxTextWidth + 150 + 2, rMeasureText("§l$titleString")) + 20
    dialogWidget.size = Size(dialogWidth, dialogHeight)
    configs.forEachIndexed { index, configOption ->
      val baseTop = 2 + 20 + index * 20
      configOption.toConfigWidget().apply {
        anchor = AnchorStyles.none
        dialogWidget.addChild(this)
        width = 150
        right = 10
        top = baseTop
      }
      object : TextButtonWidget(configOption.displayName) {
        override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
          super.render(mouseX, mouseY, partialTicks)
          if (showTooltips && contains(mouseX, mouseY)) {
            Tooltips.addTooltip(configOption.description, mouseX, mouseY, rScreenWidth * 2 / 3)
          }
        }
      }.apply {
        dialogWidget.addChild(this)
        left = 10
        top = baseTop + 6
        zIndex = 1
      }
    }
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.render(mouseX, mouseY, partialTicks)
//    Diffuse disable()
    configHotkey.mainKeybind.settings = keybindSettingElement.settings
    rDrawCenteredText(
      "§l$titleString",
      dialogWidget.screenX + dialogWidget.width / 2,
      dialogWidget.screenY + 2 + 6,
      COLOR_WHITE
    )
    Tooltips.renderAll()
  }

}