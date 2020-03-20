package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.config.builder.RegisteredConfigSingleton
import io.github.jsnimda.common.gui.screen.ConfigScreenBase
import io.github.jsnimda.common.gui.screen.addToConfigScreen
import io.github.jsnimda.common.vanilla.TranslatableText
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager

private const val BUTTON_TEXT_KEY_PREFIX = "inventoryprofiles.gui.config."
private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

//private val modSettings = { ModSettings::class.java.toListWidget() }
//private val guiSettings = { GuiSettings::class.java.toListWidget() }
//private val editProfiles = {
//  ListConfigOptionsWidget(DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX).apply {
//    addAnchor(I18n.translate("inventoryprofiles.config.category.coming_soon"))
//  }
//}
//private val hotkeys = { Hotkeys::class.java.toListWidget() }
//private val tweaks = { Tweaks::class.java.toListWidget() }

class ConfigScreen : ConfigScreenBase(TranslatableText("inventoryprofiles.gui.config.title")) {
  companion object {
    var selectedIndex = 0
  }

  init {
    openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
    RegisteredConfigSingleton.configsList.addToConfigScreen(
      this, BUTTON_TEXT_KEY_PREFIX, DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX
    )
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