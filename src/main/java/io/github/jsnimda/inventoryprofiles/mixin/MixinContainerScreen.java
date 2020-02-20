package io.github.jsnimda.inventoryprofiles.mixin;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.gui.ToolTips;
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

  @Shadow
  protected int containerWidth;
  @Shadow
  protected int containerHeight;
  @Shadow
  protected int x;
  @Shadow
  protected int y;
  @Shadow
  protected final T container;

  protected MixinContainerScreen(Text text_1) {
    super(text_1);
    container = null;
    // Auto-generated constructor stub
  }

  @Inject(at = @At("RETURN"), method = "init()V")
  protected void init(CallbackInfo info) {
    List<AbstractButtonWidget> buttons = GuiSortingButtons.gets(this, container, x, y, containerWidth, containerHeight);
    buttons.forEach(x -> this.addButton(x));
  }

  @Inject(at = @At("RETURN"), method = "render(IIF)V")
  public void render(int int_1, int int_2, float float_1, CallbackInfo info) {
    if (!ToolTips.current.isEmpty()) {
      GlStateManager.pushMatrix();
      ToolTips.renderAll();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
    }
  }


}