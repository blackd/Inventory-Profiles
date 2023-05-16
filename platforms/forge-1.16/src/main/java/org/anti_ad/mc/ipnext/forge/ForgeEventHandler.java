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

import kotlin.Unit;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
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
    public void joinWorld(WorldEvent.Load event) {
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
    public void onInitGuiPost(InitGuiEvent.Post e) { // MixinAbstractContainerScreen.init
        ScreenEventHandler.INSTANCE.onScreenInit(e.getGui(), x -> {
            e.addWidget(x);
            return Unit.INSTANCE;
        });
    }

    @SubscribeEvent
    public void preScreenRender(GuiScreenEvent.DrawScreenEvent.Pre event) {

        ScreenEventHandler.INSTANCE.preRender(new NativeContext(event.getMatrixStack()));
    }

    // fabric GameRenderer.render() = forge updateCameraAndRender()
    // forge line 554
    @SubscribeEvent
    public void postScreenRender(DrawScreenEvent.Post e) {
        ScreenEventHandler.INSTANCE.postRender(new NativeContext(e.getMatrixStack()));
    }

    @SubscribeEvent
    public void onBackgroundRender(GuiContainerEvent.DrawBackground e) {
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender(new NativeContext(e.getMatrixStack()), e.getMouseX(), e.getMouseY());
    }

    @SubscribeEvent
    public void onForegroundRender(GuiContainerEvent.DrawForeground e) {
        ContainerScreenEventHandler.INSTANCE.onForegroundRender(new NativeContext(e.getMatrixStack()), e.getMouseX(), e.getMouseY());
    }

    // ============
    // old event
    // ============

    @SubscribeEvent
    public void onGuiKeyPressedPre(GuiScreenEvent.KeyboardKeyPressedEvent.Pre e) { // Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM
        if (!VanillaUtil.INSTANCE.inGame()) return;
        InputMappings.Input mouseKey = InputMappings.getInputByCode(e.getKeyCode(), e.getScanCode()); // getKey(e.getKeyCode(), e.getScanCode());
        if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()
                && (e.getKeyCode() == 256 || Vanilla.INSTANCE.mc().gameSettings.keyBindInventory // options.keyInventory
                .isActiveAndMatches(mouseKey))) {
            GeneralInventoryActions.INSTANCE.handleCloseContainer();
        }
    }

    //@SubscribeEvent
    public void onOverlayLayerPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            LockSlotsHandler.INSTANCE.preRenderHud(new NativeContext(event.getMatrixStack()));
        }
    }

    @SubscribeEvent
    public void onOverlayLayerPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            LockSlotsHandler.INSTANCE.postRenderHud(new NativeContext(event.getMatrixStack()));
        }
    }

}
