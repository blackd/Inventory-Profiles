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

package org.anti_ad.mc.ipnext.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.anti_ad.mc.common.forge.CommonForgeEventHandler;
import org.anti_ad.mc.common.vanilla.VanillaSound;
import org.anti_ad.mc.ipnext.InventoryProfilesKt;
import org.anti_ad.mc.ipnext.event.Sounds;
import org.anti_ad.mc.ipnext.gui.ConfigScreen;
import org.apache.commons.lang3.tuple.Pair;


public class ClientInit implements Runnable {
    @Override
    public void run() {
        MinecraftForge.EVENT_BUS.register(new CommonForgeEventHandler());

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()->Pair.of(()->"anything. i don't care", (remoteversionstring,networkbool)->networkbool));
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (x, y) -> new ConfigScreen());

        InventoryProfilesKt.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        VanillaSound.INSTANCE.getREGISTER().register(bus);
        Sounds.Companion.registerAll();
    }
}
