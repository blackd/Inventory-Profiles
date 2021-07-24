package org.anti_ad.mc.ipnext;

import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import org.anti_ad.mc.common.forge.CommonForgeEventHandler;
import org.anti_ad.mc.ipnext.event.ClientInitHandler;
import org.anti_ad.mc.ipnext.forge.ForgeEventHandler;
import org.anti_ad.mc.ipnext.gui.ConfigScreen;

import java.util.function.BiFunction;
import java.util.function.Supplier;


/**
 * InventoryProfilesNext
 */
@Mod(ModInfo.MOD_ID)
public class ForgeModEntry {

    public ForgeModEntry() {

        MinecraftForge.EVENT_BUS.register(new CommonForgeEventHandler());

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
                new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> new ConfigScreen()));

//    GlobalInputHandler.getInstance().registerInputHandler(new InputHandler());
//
//    Configs.saveLoadManager.load();

        InventoryProfilesKt.init();

        ClientInitHandler.INSTANCE.register(() -> {
            ModInfo.MOD_VERSION = ModInfo.getModVersion();
            return Unit.INSTANCE;
        });

//    GlobalInitHandler.INSTANCE.onInit();
        // ^^ let do it on first tick event
    }

}
