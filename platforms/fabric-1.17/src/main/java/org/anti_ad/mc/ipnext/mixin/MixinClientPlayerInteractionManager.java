package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.anti_ad.mc.ipnext.config.Tweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * MixinClientPlayerInteractionManager
 */
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow
    private int blockBreakingCooldown;

    @Inject(at = @At("HEAD"), method = "updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
    public void updateBlockBreakingProgress(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
        if (this.blockBreakingCooldown > 0 && Tweaks.INSTANCE.getDISABLE_BLOCK_BREAKING_COOLDOWN().getBooleanValue()) {
            this.blockBreakingCooldown = 0;
        }
    }

    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;"
            + "breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"),
            method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
    public void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
        if (Tweaks.INSTANCE.getINSTANT_MINING_COOLDOWN().getBooleanValue()) {
            this.blockBreakingCooldown = 5;
        }
    }

}