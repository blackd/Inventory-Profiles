/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.compat.integrations

import earth.terrarium.chipped.menus.ChippedMenu
import net.minecraft.client.player.LocalPlayer
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.ipnext.ingame.`(syncId)`

typealias ChippedMenu = ChippedMenu

fun ChippedMenu.`(selectRecipe)`(id: Int) {
    clickMenuButton(Vanilla.player(), id)
    Vanilla.mc().gameMode?.handleInventoryButtonClick(`(syncId)` , id)
}
