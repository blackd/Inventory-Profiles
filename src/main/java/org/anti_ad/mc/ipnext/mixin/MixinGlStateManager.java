package org.anti_ad.mc.ipnext.mixin;

import org.anti_ad.mc.ipnext.config.Tweaks;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//TODO rename the mixin to reflect the target

/**
 * MixinGlStateManager
 */
@Mixin(GL11.class)
public class MixinGlStateManager {

  @ModifyVariable(method = "glFogf(IF)V", at = @At("HEAD"), argsOnly = true, remap = false)
  private static float glFogf(float fogDensity) {
    if (fogDensity == 2.0f && Tweaks.INSTANCE.getDISABLE_LAVA_FOG().getBooleanValue()) {
      return 0.02f;
    }
    return fogDensity;
  }

}