package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.anti_ad.mc.common.Log;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Shadow private MinecraftClient client;
    @Shadow private ClientWorld world;

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ClientEventHandler.INSTANCE.onJoinWorld();
    }

    @Inject(method = "onInventory", at = @At("RETURN"))
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        Log.INSTANCE.trace("Received Inventory Packet");
    }

}
