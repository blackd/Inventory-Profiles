package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.config.builder.toConfigList
import io.github.jsnimda.common.gui.screen.ConfigScreenBase
import io.github.jsnimda.common.gui.widgets.toListWidget
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.TranslatableText
import io.github.jsnimda.inventoryprofiles.ModInfo
import io.github.jsnimda.inventoryprofiles.config.*

private const val BUTTON_PREFIX = "inventoryprofiles.gui.config."
private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

class ConfigScreen : ConfigScreenBase(TranslatableText("inventoryprofiles.gui.config.title", ModInfo.MOD_VERSION)) {
  companion object {
    var selectedIndex = 0
  }

  init {
    openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
    (Configs - if (ModSettings.DEBUG_LOGS.booleanValue) listOf() else listOf(Debugs))
      .toConfigList().forEach {
        addNavigationButtonWithWidget(I18n.translate(BUTTON_PREFIX + it.key)) {
          it.toListWidget(DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX)
        }
      }
    selectedIndex = Companion.selectedIndex
  }

  override fun selectedIndexChanged() {
    Companion.selectedIndex = selectedIndex
  }

  override fun closeScreen() {
    SaveLoadManager.save()
    super.closeScreen()
  }
}