package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.Tooltips
import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.gui.widget.TextButtonWidget
import io.github.jsnimda.common.gui.widget.toWidget
import io.github.jsnimda.common.input.ConfigElementKeybindSetting
import io.github.jsnimda.common.vanilla.I18n
import io.github.jsnimda.common.vanilla.TranslatableText
import io.github.jsnimda.common.vanilla.VanillaRender
import io.github.jsnimda.common.vanilla.render.measureText
import kotlin.math.max

private const val COLOR_WHITE = -0x1

class ConfigOptionHotkeyDialog(val configHotkey: ConfigHotkey) :
  BaseDialog(TranslatableText("inventoryprofiles.common.gui.config.advanced_keybind_settings")) {
  private val keybindSettingElement =
    with(configHotkey.mainKeybind) { ConfigElementKeybindSetting(defaultSettings, settings) }
  val configs = keybindSettingElement.getConfigOptionsList()

  private val IConfigOption.displayName
    get() = I18n.translate("inventoryprofiles.common.gui.config.$key")
  private val IConfigOption.description
    get() = I18n.translate("inventoryprofiles.common.gui.config.description.$key")

  private val maxTextWidth = configs.map { measureText(it.displayName) }.max() ?: 0

  var showTooltips = false

  init {
    val dialogHeight = 5 * 20 + 2 + 10
    val dialogWidth = max(maxTextWidth + 150 + 2, measureText("§l$titleString")) + 20
    dialogWidget.size = Size(dialogWidth, dialogHeight)
    configs.forEachIndexed { index, configOption ->
      val baseTop = 2 + 20 + index * 20
      configOption.toWidget().apply {
        anchor = AnchorStyles.none
        dialogWidget.widgets.add(this)
        width = 150
        right = 10
        top = baseTop
      }
      object : TextButtonWidget(configOption.displayName) {
        override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
          super.render(mouseX, mouseY, partialTicks)
          if (showTooltips && isMouseOver(mouseX, mouseY)) {
            Tooltips.addTooltip(configOption.description, mouseX, mouseY, VanillaRender.screenWidth * 2 / 3)
          }
        }
      }.apply {
        dialogWidget.widgets.add(this)
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
    drawCenteredString(
      font,
      "§l$titleString",
      dialogWidget.screenX + dialogWidget.width / 2,
      dialogWidget.screenY + 2 + 6,
      COLOR_WHITE
    )
    Tooltips.renderAll()
  }

}