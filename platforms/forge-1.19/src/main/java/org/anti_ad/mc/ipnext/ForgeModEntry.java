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

package org.anti_ad.mc.ipnext;

import kotlin.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.anti_ad.mc.ipnext.event.ClientInitHandler;
import org.anti_ad.mc.ipnext.forge.ClientInit;
import org.anti_ad.mc.ipnext.forge.ServerInit;


/**
 * InventoryProfilesNext
 */
@Mod(ModInfo.MOD_ID)
public class ForgeModEntry {

    private static Runnable toInit = FMLEnvironment.dist == Dist.CLIENT ? new ClientInit() : new ServerInit();

    public ForgeModEntry() {
        try {
            toInit.run();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        ClientInitHandler.INSTANCE.register(() -> {
            ModInfo.MOD_VERSION = ModInfo.getModVersion();
            return Unit.INSTANCE;
        });

//    GlobalInitHandler.INSTANCE.onInit();
        // ^^ let do it on first tick event
    }

}
