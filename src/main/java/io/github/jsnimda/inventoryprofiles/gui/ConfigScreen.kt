package io.github.jsnimda.inventoryprofiles.gui

import io.github.jsnimda.common.gui.screen.ConfigScreenBase
import io.github.jsnimda.common.gui.widget.ListConfigOptionsWidget
import io.github.jsnimda.common.gui.widget.toWidget
import io.github.jsnimda.common.vanilla.I18n
import io.github.jsnimda.common.vanilla.TranslatableText
import io.github.jsnimda.inventoryprofiles.config.Configs
import io.github.jsnimda.inventoryprofiles.config.Configs.*

private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

private val modSettings = { ModSettings::class.java.toListWidget() }
private val guiSettings = { GuiSettings::class.java.toListWidget() }
private val editProfiles = {
  ListConfigOptionsWidget(DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX).apply {
    addAnchor(I18n.translate("inventoryprofiles.config.category.coming_soon"))
  }
}
private val hotkeys = { Hotkeys::class.java.toListWidget() }
private val tweaks = { Tweaks::class.java.toListWidget() }

private fun <T> Class<T>.toListWidget() =
    Configs.getConfigs(this).toWidget(DISPLAY_NAME_PREFIX, DESCRIPTION_PREFIX)

private fun translate(key: String) =
    I18n.translate("inventoryprofiles.gui.config.$key")

class ConfigScreen : ConfigScreenBase(TranslatableText("inventoryprofiles.gui.config.title")) {
  companion object {
    var selectedIndex = 0
  }

  override fun selectedIndexChanged() {
    Companion.selectedIndex = selectedIndex
  }

  init {
    openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
    addNavigationButtonWithWidget(translate("ModSettings"), modSettings)
    addNavigationButtonWithWidget(translate("GuiSettings"), guiSettings)
    addNavigationButtonWithWidget(translate("EditProfiles"), editProfiles)
    addNavigationButtonWithWidget(translate("Hotkeys"), hotkeys)
    addNavigationButtonWithWidget(translate("Tweaks"), tweaks)
    selectedIndex = Companion.selectedIndex
  }

  override fun onClose() {
    Configs.saveLoadManager.save()
    super.onClose()
  }

}