package org.anti_ad.mc.ipnext;

import kotlin.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
        //MixinBootstrap.init();

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
