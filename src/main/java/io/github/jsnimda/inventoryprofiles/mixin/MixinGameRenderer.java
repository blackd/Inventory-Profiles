package io.github.jsnimda.inventoryprofiles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import net.minecraft.client.render.GameRenderer;

/**
 * MixinGameRenderer
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

  @Inject(at = @At("HEAD"), method = "bobViewWhenHurt(F)V", cancellable = true)
  public void bobViewWhenHurt(float float_1, CallbackInfo info) {
    if (Tweaks.DISABLE_SCREEN_SHAKING_ON_DAMAGE.getBooleanValue()) {
      info.cancel();
    }
  }

}