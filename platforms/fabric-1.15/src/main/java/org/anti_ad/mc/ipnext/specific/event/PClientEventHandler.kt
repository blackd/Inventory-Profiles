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
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.createHoverEventText
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.ClickEvent
import org.anti_ad.mc.common.vanilla.alias.ClickEventAction
import org.anti_ad.mc.common.vanilla.alias.Style
import org.anti_ad.mc.common.vanilla.alias.Text

interface PClientEventHandler {

    fun createChatMessage(new: SemVer): Text = LiteralText("")
        .append(LiteralText("Inventory Profiles Next:")
                    .apply {
                        style = Style()
                            .setBold(true)
                            .setColor(Formatting.AQUA)
                    }
               )
        .append(LiteralText(I18n.translate("inventoryprofiles.update.version"))
                    .apply {
                        style = Style()
                    })
        .append(LiteralText("'$new'")
                    .apply {
                        style = Style()
                            .setBold(true)
                            .setColor(Formatting.DARK_GREEN)
                    })
        .append(LiteralText(I18n.translate("inventoryprofiles.update.available"))
                    .apply {
                        style = Style()
                    })
        .append(I18n.translate("inventoryprofiles.update.get"))
        .append(LiteralText("\"Modrinth\"")
                    .apply {
                        style = Style()
                            .setBold(true)
                            .setColor(Formatting.DARK_GREEN)
                            .setUnderline(true)
                            .setClickEvent(ClickEvent(ClickEventAction.OPEN_URL,
                                                      "https://modrinth.com/mod/inventory-profiles-next"))
                            .setHoverEvent(createHoverEventText("https://modrinth.com/mod/inventory-profiles-next"))
                    })
        .append(LiteralText(I18n.translate("inventoryprofiles.update.or"))
                    .apply { style = Style() })
        .append(LiteralText("\"CurseForge\"")
                    .apply {
                        style = Style()
                            .setBold(true)
                            .setColor(Formatting.DARK_RED)
                            .setUnderline(true)
                            .setClickEvent(ClickEvent(ClickEventAction.OPEN_URL,
                                                      "https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                            .setHoverEvent(createHoverEventText("https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                    })
}
