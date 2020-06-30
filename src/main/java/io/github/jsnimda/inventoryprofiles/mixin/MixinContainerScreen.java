package io.github.jsnimda.inventoryprofiles.mixin;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.common.gui.Tooltips;
import io.github.jsnimda.inventoryprofiles.gui.inject.ContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.gui.inject.GuiSortingButtons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.container.Container;
import net.minecraft.text.Text;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(ContainerScreen.class)
public abstract class MixinContainerScreen<T extends Container> extends Screen {

  protected MixinContainerScreen(Text text) {
    super(text);
  }

  @Inject(at = @At("RETURN"), method = "init()V")
  protected void init(CallbackInfo info) {
    addButton(ContainerScreenHandler.INSTANCE.getContainerInjector((ContainerScreen)(Object)this));
  }

  @Inject(at = @At("RETURN"), method = "render(IIF)V")
  public void render(int int_1, int int_2, float float_1, CallbackInfo info) {
    Tooltips.INSTANCE.renderAll();
  }

}