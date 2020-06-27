package io.github.jsnimda.inventoryprofiles.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.jsnimda.inventoryprofiles.config.Tweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * MixinGlStateManager
 */
@Mixin(GlStateManager.class)
public class MixinGlStateManager {

  @ModifyVariable(method = "fogDensity(F)V", at = @At("HEAD"), argsOnly = true)
  private static float fogDensity(float fogDensity) {
    if (fogDensity == 2.0f && Tweaks.INSTANCE.getDISABLE_LAVA_FOG().getBooleanValue()) {
      return 0.02f;
    }
    return fogDensity;
  }

}