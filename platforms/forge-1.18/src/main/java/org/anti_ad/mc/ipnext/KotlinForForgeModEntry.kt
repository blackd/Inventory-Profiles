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

package org.anti_ad.mc.ipnext

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.ipnext.event.ClientInitHandler.register
import org.anti_ad.mc.ipnext.forge.KotlinClientInit
import org.anti_ad.mc.ipnext.forge.KotlinServerInit

@Mod(ModInfo.MOD_ID)
object KotlinForForgeModEntry {

    private val toInit: Runnable = if (FMLEnvironment.dist === Dist.CLIENT) KotlinClientInit() else KotlinServerInit()

    init {
        try {
            toInit.run()
            Log.info("Inventory Profiles Next - init in Kotlin")
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        register {
            ModInfo.MOD_VERSION = ModInfo.getModVersion()
        }

    }
}

fun specificInit() {

}
