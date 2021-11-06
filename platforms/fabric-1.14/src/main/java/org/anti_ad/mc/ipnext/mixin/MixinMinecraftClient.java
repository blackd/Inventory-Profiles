package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
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
    private int itemUseCooldown;

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
        ClientEventHandler.INSTANCE.onJoinWorld(clientWorld);
    }
}
