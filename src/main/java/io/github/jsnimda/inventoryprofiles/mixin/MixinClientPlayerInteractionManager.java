package io.github.jsnimda.inventoryprofiles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * MixinClientPlayerInteractionManager
 */
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

  @Shadow
  private int field_3716;

  @Inject(at = @At("HEAD"), method = "method_2902(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
  public void method_2902(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
    if (this.field_3716 > 0 && Tweaks.DISABLE_BLOCK_BREAKING_COOLDOWN.getBooleanValue()) {
      this.field_3716 = 0;
    }
  }

  @Inject(at = @At(value = "INVOKE_ASSIGN", target="Lnet/minecraft/client/network/ClientPlayerInteractionManager;"
      + "breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"), 
      method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
  public void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
    if (Tweaks.INSTANT_MINING_COOLDOWN.getBooleanValue()) {
      this.field_3716 = 5;
    }
  }

}