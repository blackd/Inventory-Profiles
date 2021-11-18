package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.anti_ad.mc.ipnext.config.ModSettings;
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
@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public ClientPlayerEntity player;

    @Shadow @Final
    public GameSettings gameSettings;

    @Inject(at = @At("HEAD"),
            method = "processKeyBinds()V")
    public void handleInputEvents(CallbackInfo info) {
        IMixinKeyBinding drop = (IMixinKeyBinding) gameSettings.keyBindDrop;
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && PlayerInventory.isHotbar(this.player.inventory.currentItem)
                && drop.getTimesPressed() > 0 ) {

            if (!LockSlotsHandler.INSTANCE.isQMoveActionAllowed(this.player.inventory.currentItem + 36, true, 0)) {

                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }
    }
}
