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

package org.anti_ad.mc.ipnext.forge

import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ExtensionPoint
import net.minecraftforge.fml.ModLoadingContext
import org.anti_ad.mc.common.forge.CommonForgeEventHandler
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper
import org.anti_ad.mc.ipnext.event.Sounds.Companion.registerAll
import org.anti_ad.mc.ipnext.gui.ConfigScreen
import org.apache.commons.lang3.tuple.Pair
import java.util.function.*
import org.anti_ad.mc.ipnext.init as inventoryProfilesInit

class KotlinClientInit: Runnable {

    override fun run() {
        MinecraftForge.EVENT_BUS.register(CommonForgeEventHandler())

        MinecraftForge.EVENT_BUS.register(ForgeEventHandler())

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST) {
            Pair.of(
                Supplier { "anything. i don't care" },
                BiPredicate { remoteversionstring: String?, networkbool: Boolean? -> networkbool!! })
        }

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY) {
            BiFunction {
                    x: Minecraft?, y: Screen? -> ConfigScreen()
            }
        }


        LockedSlotKeeper.doEverySecondTick = true
        inventoryProfilesInit()
        registerAll()
    }
}
