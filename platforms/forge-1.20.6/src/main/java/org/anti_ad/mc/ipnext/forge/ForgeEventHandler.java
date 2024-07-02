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

import com.mojang.blaze3d.platform.InputConstants;
import kotlin.Unit;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenEvent.Render;
import net.minecraftforge.client.event.ScreenEvent.Init;
/*
import net.minecraftforge.client.event.RenderGuiOverlayEvent;// RenderGameOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay ;// ForgeIngameGui;
*/
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.anti_ad.mc.common.vanilla.VanillaUtil;
import org.anti_ad.mc.ipnext.config.Tweaks;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions;

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
    public void joinWorld(LevelEvent.Load event) {
        if (VanillaUtil.INSTANCE.isOnClientThread()) {
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
    public void onInitGuiPost(Init.Post e) { // MixinAbstractContainerScreen.init
        ScreenEventHandler.INSTANCE.onScreenInit(e.getScreen(), x -> {
            e.addListener(x);
            return Unit.INSTANCE;
        });
    }

    @SubscribeEvent
    public void preScreenRender(ScreenEvent.Render.Pre event) {
        ScreenEventHandler.INSTANCE.preRender(new NativeContext(event.getGuiGraphics()));
    }

    // fabric GameRenderer.render() = forge updateCameraAndRender()
    // forge line 554
    @SubscribeEvent
    public void postScreenRender(Render.Post e) {
        ScreenEventHandler.INSTANCE.postRender(new NativeContext(e.getGuiGraphics()));
    }

    @SubscribeEvent
    public void onBackgroundRender(ContainerScreenEvent.Render.Background e) {
        var context = new NativeContext(e.getGuiGraphics());
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender(context, e.getMouseX(), e.getMouseY(), 0);
    }

    @SubscribeEvent
    public void onForegroundRender(ContainerScreenEvent.Render.Foreground e) {
        var context = new NativeContext(e.getGuiGraphics());
        context.setOverlay(true);
        ContainerScreenEventHandler.INSTANCE.onForegroundRender(context, e.getMouseX(), e.getMouseY(), 0);
    }

    // ============
    // old event
    // ============

    @SubscribeEvent
    public void onGuiKeyPressedPre(ScreenEvent.KeyPressed.Pre e) { // Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM
        if (!VanillaUtil.INSTANCE.inGame()) return;
        InputConstants.Key mouseKey = InputConstants.getKey(e.getKeyCode(), e.getScanCode()); //getInputByCode(e.getKeyCode(), e.getScanCode()); // getKey(e.getKeyCode(), e.getScanCode());
        if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()
                && (e.getKeyCode() == 256 || Vanilla.INSTANCE.mc().options.keyInventory //gameSettings.keyBindInventory // options.keyInventory
                .isActiveAndMatches(mouseKey))) {
            GeneralInventoryActions.INSTANCE.handleCloseContainer();
        }
    }

    @SubscribeEvent
    public void onScreenClose(ScreenEvent.Closing event) { // Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM

        if (!VanillaUtil.INSTANCE.inGame()) return;
        ScreenEventHandler.INSTANCE.onScreenRemoved(event.getScreen());
    }

/*
    // this event is disabled.
    //@SubscribeEvent
    public void onOverlayLayerPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            LockSlotsHandler.INSTANCE.preRenderHud(new NativeContext(event.getGuiGraphics()));
        }
    }


    @SubscribeEvent
    public void onOverlayLayerPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            LockSlotsHandler.INSTANCE.postRenderHud(new NativeContext(event.getGuiGraphics()));
        }
    }
*/

}
