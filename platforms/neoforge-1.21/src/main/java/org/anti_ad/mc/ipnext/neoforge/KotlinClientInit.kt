/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.neoforge

import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModLoadingContext
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.client.gui.ModListScreen
import net.neoforged.neoforge.common.NeoForge
import org.anti_ad.mc.alias.client.gui.screen.Screen
import org.anti_ad.mc.ipnext.gui.ConfigScreen
import org.anti_ad.mc.ipnext.init as inventoryProfilesInit

class KotlinClientInit: Runnable {

    override fun run() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { container: ModContainer, sc: Screen ->
                ConfigScreen()
            }
        }

        NeoForge.EVENT_BUS.register(ForgeEventHandler())

        inventoryProfilesInit()
    }
}
