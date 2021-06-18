package org.anti_ad.mc.ipnext.mixin;

import org.anti_ad.mc.ipnext.config.Tweaks;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinGameRenderer
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
  @Inject(at = @At("HEAD"), method = "bobViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", cancellable = true)
  public void bobViewWhenHurt(MatrixStack matrixStack_1, float float_1, CallbackInfo info) {
    if (Tweaks.INSTANCE.getDISABLE_SCREEN_SHAKING_ON_DAMAGE().getBooleanValue()) {
      info.cancel();
    }
  }

//  @Inject(at = @At("RETURN"), method = "render")
//  public void render(float f, long l, boolean bl, CallbackInfo ci) {
//    ClientEventHandler.INSTANCE.postScreenRender();
//  }

  @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
      "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"), method = "render")
  public void preScreenRender(float f, long l, boolean bl, CallbackInfo ci) {
    ScreenEventHandler.INSTANCE.preRender();
  }

  @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
      "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.AFTER), method = "render")
  public void postScreenRender(float f, long l, boolean bl, CallbackInfo ci) {
    ScreenEventHandler.INSTANCE.postRender();
  }
}