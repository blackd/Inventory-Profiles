package io.github.jsnimda.inventoryprofiles.mixin;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.gui.ToolTips;
import io.github.jsnimda.inventoryprofiles.gui.inject.GuiSortingButtons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(HandledScreen.class)
public abstract class MixinContainerScreen<T extends ScreenHandler> extends Screen {

  @Shadow
  protected int backgroundWidth;
  @Shadow
  protected int backgroundHeight;
  @Shadow
  protected int x;
  @Shadow
  protected int y;
  @Final @Shadow
  protected T handler;

  protected MixinContainerScreen(Text text_1) {
    super(text_1);
    // handler = null; // ...what?
    // Auto-generated constructor stub
  }

  @Inject(at = @At("RETURN"), method = "init()V")
  protected void init(CallbackInfo info) {
    List<AbstractButtonWidget> buttons = GuiSortingButtons.gets(this, handler, x, y, backgroundWidth,
                                                                backgroundHeight);
    buttons.forEach(this::addButton);
  }

  @Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
  public void render(MatrixStack matrices, int int_1, int int_2, float float_1, CallbackInfo info) {
    if (!ToolTips.current.isEmpty()) {
      matrices.push();
      ToolTips.renderAll();
      RenderSystem.disableLighting();
      matrices.pop();
    }
  }


}