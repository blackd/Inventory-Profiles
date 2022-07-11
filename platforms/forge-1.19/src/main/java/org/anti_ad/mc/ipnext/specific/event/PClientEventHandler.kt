/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.specific.event


import org.anti_ad.mc.common.moreinfo.SemVer

import org.anti_ad.mc.common.vanilla.alias.Formatting
import org.anti_ad.mc.common.vanilla.alias.createHoverEventText
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.ClickEvent
import org.anti_ad.mc.common.vanilla.alias.ClickEventAction
import org.anti_ad.mc.common.vanilla.alias.MutableText
import org.anti_ad.mc.common.vanilla.alias.Style
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.alias.getLiteral

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
                            .withUnderlined(true)
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
                            .withUnderlined(true)
                            .withClickEvent(ClickEvent(ClickEventAction.OPEN_URL,
                                                       "https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                            .withHoverEvent(createHoverEventText("https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                    })
}
