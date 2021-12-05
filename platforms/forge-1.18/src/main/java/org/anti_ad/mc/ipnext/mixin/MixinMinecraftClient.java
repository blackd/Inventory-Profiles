package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
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
    public LocalPlayer player;

    @Shadow @Final
    public Options options;

    @Inject(at = @At("HEAD"),
            method = "handleKeybinds()V")
    public void handleInputEvents(CallbackInfo info) {
        IMixinKeyBinding drop = (IMixinKeyBinding) options.keyDrop;
        if(LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && Inventory.isHotbarSlot(this.player.getInventory().selected)
                && drop.getTimesPressed() > 0 ) {

            if (!LockSlotsHandler.INSTANCE.isHotbarQMoveActionAllowed(this.player.getInventory().selected + 36, true)) {

                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }
    }
}
