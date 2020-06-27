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
    var selectedIndex = 0
  }

  private fun CategorizedMultiConfig.toListWidget() =
    this.toListWidget(
      { I18n.translate(DISPLAY_NAME_PREFIX + it) },
      { I18n.translate(DESCRIPTION_PREFIX + it) },
      I18n::translate
    )

  init {
    openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
    (Configs - if (ModSettings.DEBUG_LOGS.booleanValue) listOf() else listOf(Debugs)) // hide debugs class
      .toMultiConfigList().forEach { multi ->
        addNavigationButtonWithWidget(I18n.translate(BUTTON_PREFIX + multi.key)) { multi.toListWidget() }
      }
    selectedIndex = Companion.selectedIndex
  }

  override fun selectedIndexChanged() {
    Companion.selectedIndex = selectedIndex
  }

  override fun closeScreen() {
    SaveLoadManager.save()
    AutoRefillHandler.init() // update
    super.closeScreen()
  }
}