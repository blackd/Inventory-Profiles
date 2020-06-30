package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.config.CategorizedMultiConfig
import io.github.jsnimda.common.config.builder.toMultiConfigList
import io.github.jsnimda.common.gui.screen.ConfigScreenBase
import io.github.jsnimda.common.gui.widgets.toListWidget
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.TranslatableText
import io.github.jsnimda.inventoryprofiles.ModInfo
import io.github.jsnimda.inventoryprofiles.config.*
import io.github.jsnimda.inventoryprofiles.event.AutoRefillHandler

private const val BUTTON_PREFIX = "inventoryprofiles.gui.config."
private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

class ConfigScreen : ConfigScreenBase(TranslatableText("inventoryprofiles.gui.config.title", ModInfo.MOD_VERSION)) {
  companion object {
    var storedSelectedIndex = 0
  }

  private fun CategorizedMultiConfig.toListWidget() =
    this.toListWidget(
      { I18n.translateOrElse(DISPLAY_NAME_PREFIX + it) { it } },
      { I18n.translateOrEmpty(DESCRIPTION_PREFIX + it) },
      { I18n.translateOrElse(it) { it.substringAfterLast('.') } }
    )

  init {
    openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
    (Configs - if (ModSettings.DEBUG.booleanValue) listOf() else listOf(Debugs)) // hide debugs class
      .toMultiConfigList().forEach { multi ->
        addNavigationButtonWithWidget(I18n.translate(BUTTON_PREFIX + multi.key)) { multi.toListWidget() }
      }
    selectedIndex = storedSelectedIndex
  }

  override fun closeScreen() {
    storedSelectedIndex = selectedIndex
    SaveLoadManager.save()
    AutoRefillHandler.init() // update
    super.closeScreen()
  }
}