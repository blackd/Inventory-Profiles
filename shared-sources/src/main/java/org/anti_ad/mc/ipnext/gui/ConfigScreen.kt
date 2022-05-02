package org.anti_ad.mc.ipnext.gui

import org.anti_ad.mc.common.config.CategorizedMultiConfig
import org.anti_ad.mc.common.config.builder.ConfigDeclaration
import org.anti_ad.mc.common.config.builder.toMultiConfigList
import org.anti_ad.mc.common.gui.screen.ConfigScreenBase
import org.anti_ad.mc.common.gui.widgets.toListWidget
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.alias.getTranslatable
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.config.Configs
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Modpacks
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.event.AutoRefillHandler

private const val BUTTON_PREFIX = "inventoryprofiles.gui.config."
private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

class ConfigScreen(private val gui: Boolean = false) : ConfigScreenBase(getTranslatable("inventoryprofiles.gui.config.title",
                                                                                        ModInfo.MOD_VERSION)) {


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
        // hide debugs class
        val toRemove = mutableSetOf<ConfigDeclaration>()
        if (!ModSettings.DEBUG.booleanValue) toRemove.add(Debugs)
        if (!ModSettings.FOR_MODPACK_DEVS.booleanValue) toRemove.add(Modpacks)
        val configsToUse = Configs - toRemove
        configsToUse.toMultiConfigList().forEach { multi ->
                addNavigationButtonWithWidget(I18n.translate(BUTTON_PREFIX + multi.key)) { multi.toListWidget() }
            }
        selectedIndex = storedSelectedIndex
    }

    override fun closeScreen() {
        InfoManager.event(if (gui) "gui/" else {""} + "closeConfig")
        storedSelectedIndex = selectedIndex
        SaveLoadManager.save()
        AutoRefillHandler.init() // update
        super.closeScreen()
    }
}