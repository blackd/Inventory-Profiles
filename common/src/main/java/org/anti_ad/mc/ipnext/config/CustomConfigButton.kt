package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import java.net.URL
import java.nio.file.Path

private val configFolder = VanillaUtil.configDirectory("inventoryprofilesnext")

var __glue_profileFilePath: () -> Path = { TODO("Glue failed to apply!") }

val profileFilePath: Path
    get() = __glue_profileFilePath()




object OpenConfigFolderButtonInfo : ConfigButtonInfo() {
    override val buttonText: String
        get() = I18n.translate("inventoryprofiles.gui.config.button.open_config_folder")

    override fun onClick(widget: ButtonWidget) {
        VanillaUtil.open(configFolder.toFile())
    }
}

object OpenProfilesHelpButtonInfo : ConfigButtonInfo() {
    override val buttonText: String
        get() = I18n.translate("inventoryprofiles.gui.config.profiles_help_button")

    override fun onClick(widget: ButtonWidget) {
        VanillaUtil.open(URL("https://inventory-profiles-next.github.io/profiles/"))
    }
}

object OpenProfilesConfigButtonInfo : ConfigButtonInfo() {
    override val buttonText: String
        get() = I18n.translate("inventoryprofiles.gui.config.profiles_config_button")

    override fun onClick(widget: ButtonWidget) {
        if (VanillaUtil.inGame()) {
            VanillaUtil.open(profileFilePath.toFile())
        }
    }
}
