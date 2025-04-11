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

package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinMinecraftClient
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public ClientPlayerEntity player;

    @Shadow @Final public GameOptions options;

    @Shadow public abstract boolean isPaused();

    @Shadow @Final private Window window;

    @Shadow @Nullable public Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo info) {
        ClientEventHandler.INSTANCE.onTickPre();
    }

    @Inject(at = @At("RETURN"), method = "tick()V")
    public void tick2(CallbackInfo info) {
        ClientEventHandler.INSTANCE.onTick();
    }
    @Inject(at = @At("RETURN"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$WorldEntryReason;)V")
    public void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            ClientEventHandler.INSTANCE.onJoinWorld();
        }
    }

    @Inject(at = @At("HEAD"),
            method = "handleInputEvents()V")
    public void handleInputEventsPre(CallbackInfo info) {
        if(LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && PlayerInventory.isValidHotbarIndex(this.player.getInventory().selectedSlot)
                && (options.dropKey.isPressed() || options.dropKey.wasPressed())) {

            if (!LockSlotsHandler.INSTANCE.isHotbarQMoveActionAllowed(this.player.getInventory().selectedSlot + 36, true)) {
                IMixinKeyBinding drop = (IMixinKeyBinding) options.dropKey;
                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }

        if(LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_EMPTY_HOTBAR_AS_SEMI_LOCKED().getValue()) {
            KeyBinding keySwapHands = options.swapHandsKey;
            IMixinKeyBinding keySwapHandsAccessor = (IMixinKeyBinding) keySwapHands;

            if (this.currentScreen == null
                    && PlayerInventory.isValidHotbarIndex(this.player.getInventory().selectedSlot)
                    && keySwapHandsAccessor.getTimesPressed() > 0) {

                LockedSlotKeeper.INSTANCE.setPickingItem(true);
                LockedSlotKeeper.INSTANCE.ignoreSelectedHotbarSlotForHandSwap();
                LockedSlotKeeper.INSTANCE.setPickingItem(false);
            }
        }
    }

    @Inject(at = @At("TAIL"),
            method = "handleInputEvents()V")
    public void handleInputEventsPost(CallbackInfo info) {


    }

    @Inject(at = @At("HEAD"), method = "doItemPick()V")
    private void doItemPickPre(CallbackInfo ci) {
        LockedSlotKeeper.INSTANCE.setPickingItem(true);
    }

    @Inject(at = @At("TAIL"), method = "doItemPick()V")
    private void doItemPickPost(CallbackInfo ci) {
        LockedSlotKeeper.INSTANCE.setPickingItem(false);
    }

}
