package org.anti_ad.mc.ipnext.specific.event

import net.minecraft.text.Text
import org.anti_ad.mc.common.moreinfo.SemVer

import org.anti_ad.mc.common.vanilla.alias.Formatting
import org.anti_ad.mc.common.vanilla.alias.createHoverEventText
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.ClickEvent
import org.anti_ad.mc.common.vanilla.alias.ClickEventAction
import org.anti_ad.mc.common.vanilla.alias.MutableText
import org.anti_ad.mc.common.vanilla.alias.Style

interface PClientEventHandler {

    fun createChatMessage(new: SemVer): MutableText = Text.literal("")
        .append(Text.literal("Inventory Profiles Next:")
                    .apply {
                        style = Style.EMPTY
                            .withBold(true)
                            .withColor(Formatting.AQUA)
                    }
               )
        .append(Text.literal(I18n.translate("inventoryprofiles.update.version"))
                    .apply {
                        style = Style.EMPTY
                    })
        .append(Text.literal("'$new'")
                    .apply {
                        style = Style.EMPTY
                            .withBold(true)
                            .withColor(Formatting.DARK_GREEN)
                    })
        .append(Text.literal(I18n.translate("inventoryprofiles.update.available"))
                    .apply {
                        style = Style.EMPTY
                    })
        .append(I18n.translate("inventoryprofiles.update.get"))
        .append(Text.literal("\"Modrinth\"")
                    .apply {
                        style = Style.EMPTY
                            .withBold(true)
                            .withColor(Formatting.DARK_GREEN)
                            .withUnderline(true)
                            .withClickEvent(ClickEvent(ClickEventAction.OPEN_URL, "https://modrinth.com/mod/inventory-profiles-next"))
                            .withHoverEvent(createHoverEventText("https://modrinth.com/mod/inventory-profiles-next"))
                    })
        .append(Text.literal(I18n.translate("inventoryprofiles.update.or"))
                    .apply { style = Style.EMPTY })
        .append(Text.literal("\"CurseForge\"")
                    .apply {
                        style = Style.EMPTY
                            .withBold(true)
                            .withColor(Formatting.DARK_RED)
                            .withUnderline(true)
                            .withClickEvent(ClickEvent(ClickEventAction.OPEN_URL,
                                                       "https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                            .withHoverEvent(createHoverEventText("https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                    })
}