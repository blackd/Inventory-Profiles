package io.github.jsnimda.inventoryprofiles.mixin;

import com.mojang.blaze3d.platform.GlStateManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;

/**
 * MixinGlStateManager
 */
@Mixin(GlStateManager.class)
public class MixinGlStateManager {

  @ModifyVariable(method = "fogDensity(F)V", at = @At("HEAD"), argsOnly = true)
  private static float fogDensity(float fogDensity) {
    if (fogDensity == 2.0f && Tweaks.DISABLE_LAVA_FOG.getBooleanValue()) {
      return 0.02f;
    }
    return fogDensity;
  }

}