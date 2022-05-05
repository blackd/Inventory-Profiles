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
