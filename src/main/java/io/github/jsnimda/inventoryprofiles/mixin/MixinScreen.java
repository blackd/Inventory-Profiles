package io.github.jsnimda.inventoryprofiles.mixin;

import io.github.jsnimda.inventoryprofiles.event.GameEventHandler;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen {
  @Inject(at= @At("HEAD"), method = "renderTooltip(Ljava/util/List;II)V")
  public void renderTooltip(List<String> list, int i, int j, CallbackInfo ci) {
    GameEventHandler.INSTANCE.preRenderTooltip();
  }

  @Inject(at= @At("HEAD"), method = "render")
  public void render(int i, int j, float f, CallbackInfo ci) {
    GameEventHandler.INSTANCE.preScreenRender();
  }
}
