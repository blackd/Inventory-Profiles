package io.github.jsnimda.inventoryprofiles.mixin;

import io.github.jsnimda.inventoryprofiles.event.GameEventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen {
  @Inject(at= @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V")
  public void renderTooltip(MatrixStack matrixStack, List<? extends StringRenderable> list, int i, int j, CallbackInfo ci) {
    GameEventHandler.INSTANCE.preRenderTooltip();
  }

  @Inject(at= @At("HEAD"), method = "render")
  public void render(MatrixStack matrixStack, int i, int j, float f, CallbackInfo ci) {
    GameEventHandler.INSTANCE.preScreenRender();
  }
}
