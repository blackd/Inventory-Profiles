package io.github.jsnimda.inventoryprofiles.mixin;

import io.github.jsnimda.inventoryprofiles.gui.inject.ScreenEventHandler;
import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {
//  @Inject(at= @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V")
//  public void renderTooltip(MatrixStack matrixStack, List<? extends StringRenderable> list, int i, int j, CallbackInfo ci) {
//    ClientEventHandler.INSTANCE.preRenderTooltip();
//  }
//
//  @Inject(at= @At("HEAD"), method = "render")
//  public void render(MatrixStack matrixStack, int i, int j, float f, CallbackInfo ci) {
//    ClientEventHandler.INSTANCE.preScreenRender();
//  }

  @Shadow
  protected abstract <T extends AbstractButtonWidget> T addButton(T abstractButtonWidget);

  @Inject(at = @At("RETURN"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
  public void init(MinecraftClient minecraftClient, int i, int j, CallbackInfo ci) {
    Screen self = (Screen) (Object) this;
    ScreenEventHandler.INSTANCE.onScreenInit(self, x -> {
      addButton(x);
      return Unit.INSTANCE;
    });
  }
}
