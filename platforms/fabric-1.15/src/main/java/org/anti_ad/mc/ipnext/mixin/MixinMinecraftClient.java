package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
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

    @Inject(at = @At("HEAD"),
            method = "handleInputEvents()V")
    public void handleInputEvents(CallbackInfo info) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && PlayerInventory.isValidHotbarIndex(this.player.inventory.selectedSlot)
                && (MinecraftClient.getInstance().options.keyDrop.isPressed() || MinecraftClient.getInstance().options.keyDrop.wasPressed())) {

            if (!LockSlotsHandler.INSTANCE.isQMoveActionAllowed(this.player.inventory.selectedSlot + 36, true, 0)) {
                IMixinKeyBinding drop = (IMixinKeyBinding) MinecraftClient.getInstance().options.keyDrop;
                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }
    }
}
