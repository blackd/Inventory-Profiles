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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
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

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo info) {
        ClientEventHandler.INSTANCE.onTickPre();
    }

    @Inject(at = @At("RETURN"), method = "tick()V")
    public void tick2(CallbackInfo info) {
        ClientEventHandler.INSTANCE.onTick();
    }

    @Inject(at = @At("RETURN"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
    public void joinWorld(ClientWorld clientWorld, CallbackInfo info) {
        ClientEventHandler.INSTANCE.onJoinWorld();
    }

    @Inject(at = @At("HEAD"),
            method = "handleInputEvents()V")
    public void handleInputEvents(CallbackInfo info) {
        if(LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && PlayerInventory.isValidHotbarIndex(this.player.getInventory().selectedSlot)
                && (MinecraftClient.getInstance().options.keyDrop.isPressed() || MinecraftClient.getInstance().options.keyDrop.wasPressed())) {

            if (!LockSlotsHandler.INSTANCE.isHotbarQMoveActionAllowed(this.player.getInventory().selectedSlot + 36, true)) {
                IMixinKeyBinding drop = (IMixinKeyBinding) MinecraftClient.getInstance().options.keyDrop;
                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }
    }
}
