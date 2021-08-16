package org.anti_ad.mc.ipnext.forge;

import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.anti_ad.mc.common.vanilla.glue.IVanillaUtilKt;
import org.anti_ad.mc.ipnext.config.Tweaks;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * ForgeEventHandler
 */
public class ForgeEventHandler {

    @SubscribeEvent
    public void clientClick(ClientTickEvent e) {
        if (e.phase == Phase.START) {
            ClientEventHandler.INSTANCE.onTickPre();
        } else { // e.phase == Phase.END
            ClientEventHandler.INSTANCE.onTick();
        }
    }

    @SubscribeEvent
    public void joinWorld(WorldEvent.Load event) {
        if (IVanillaUtilKt.getVanillaUtil().isOnClientThread()) {
            ClientEventHandler.INSTANCE.onJoinWorld();
        }
    }

    @SubscribeEvent
    public void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        ClientEventHandler.INSTANCE.onCrafted();
    }

    // ============
    // screen render
    // ============

    @SubscribeEvent
    public void onInitGuiPost(InitGuiEvent.Post e) { // MixinAbstractContainerScreen.init
        ScreenEventHandler.INSTANCE.onScreenInit(e.getGui(), x -> {
            e.addWidget(x);
            return Unit.INSTANCE;
        });
    }

    @SubscribeEvent
    public void preScreenRender(GuiScreenEvent.DrawScreenEvent.Pre event) {
        ScreenEventHandler.INSTANCE.preRender();
    }

    // fabric GameRenderer.render() = forge updateCameraAndRender()
    // forge line 554
    @SubscribeEvent
    public void postScreenRender(DrawScreenEvent.Post e) {
        ScreenEventHandler.INSTANCE.postRender();
    }

    @SubscribeEvent
    public void onBackgroundRender(GuiContainerEvent.DrawBackground e) {
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender();
    }

    @SubscribeEvent
    public void onForegroundRender(GuiContainerEvent.DrawForeground e) {
        ContainerScreenEventHandler.INSTANCE.onForegroundRender();
    }

    // ============
    // old event
    // ============

    @SubscribeEvent
    public void onGuiKeyPressedPre(GuiScreenEvent.KeyboardKeyPressedEvent.Pre e) { // Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM
        if (!IVanillaUtilKt.getVanillaUtil().inGame()) return;
        InputMappings.Input mouseKey = InputMappings.getInputByCode(e.getKeyCode(), e.getScanCode()); // getKey(e.getKeyCode(), e.getScanCode());
        if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()
                && (e.getKeyCode() == 256 || Vanilla.INSTANCE.mc().gameSettings.keyBindInventory // options.keyInventory
                .isActiveAndMatches(mouseKey))) {
            GeneralInventoryActions.INSTANCE.handleCloseContainer();
        }
    }

}