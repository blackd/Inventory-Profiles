package io.github.jsnimda.inventoryprofiles.mixin;


import io.github.jsnimda.inventoryprofiles.gui.inject.ScreenEventHandler;
import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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

//  @Shadow
//  protected abstract <T extends Element & Selectable> T addSelectableChild(T child);

  @Shadow
  private final List<Element> children = null;
  @Shadow
  private final List<Selectable> selectables = null;

  @Inject(at = @At("RETURN"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
  public void init(MinecraftClient minecraftClient, int i, int j, CallbackInfo ci) {
    Screen self = (Screen) (Object) this;
    ScreenEventHandler.INSTANCE.onScreenInit(self, x -> {
      addSelectableChild(x);
      return Unit.INSTANCE;
    });
  }

  protected <T extends Element & Selectable> T addSelectableChild(T child) {
    this.children.add(child);
    this.selectables.add((Selectable)child);
    return child;
  }
}
