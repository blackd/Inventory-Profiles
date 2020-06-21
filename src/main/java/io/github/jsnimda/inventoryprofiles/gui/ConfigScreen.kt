package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.config.builder.toConfigList
import io.github.jsnimda.common.gui.screen.ConfigScreenBase
import io.github.jsnimda.common.gui.widget.toWidget
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.TranslatableText
import io.github.jsnimda.inventoryprofiles.config.Configs
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager

private const val BUTTON_PREFIX = "inventoryprofiles.gui.config."
private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

class ConfigScreen : ConfigScreenBase(TranslatableText("inventoryprofiles.gui.config.title")) {
  companion object {
    var selectedIndex = 0
  }

  init {
    openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
    Configs.toConfigList().forEach {
      addNavigationButtonWithWidget(I18n.translate(BUTTON_PREFIX + it.key)) {
        it.toWidget(DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX)
      }
    }
    selectedIndex = Companion.selectedIndex
  }

  override fun selectedIndexChanged() {
    Companion.selectedIndex = selectedIndex
  }

  override fun onClose() {
    SaveLoadManager.save()
    super.onClose()
  }

}