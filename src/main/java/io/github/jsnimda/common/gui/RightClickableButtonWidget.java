package io.github.jsnimda.common.gui;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

public class RightClickableButtonWidget extends Widget {

  protected final BiConsumer<RightClickableButtonWidget, Integer> pressAction;

  public RightClickableButtonWidget(int i, int j, int k, int l, String string, BiConsumer<RightClickableButtonWidget, Integer> pressAction) {
    super(i, j, k, l, string);
    this.pressAction = pressAction;
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) { // ref: Widget
    if (this.active && this.visible) {
      boolean bl = this.clicked(d, e);
      if (bl) {
        this.playDownSound(Minecraft.getInstance().getSoundHandler());
        this.pressAction.accept(this, i);
        return true;
      }

      return false;
    } else {
      return false;
    }
  }

  @Override
  public boolean keyPressed(int i, int j, int k) { // ref: AbstractPressableButtonWidget
    if (this.active && this.visible) {
      if (i != 257 && i != 32 && i != 335) {
        return false;
      } else {
        this.playDownSound(Minecraft.getInstance().getSoundHandler());
        this.pressAction.accept(this, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        return true;
      }
    } else {
      return false;
    }
  }

  @Override
  public void renderButton(int i, int j, float f) { // ref: Widget
    Minecraft minecraftClient = Minecraft.getInstance();
    FontRenderer textRenderer = minecraftClient.fontRenderer;
    minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
    int k = this.getYImage(this.isHovered());
    GlStateManager.enableBlend();
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    this.blit(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
    this.blit(this.x + this.width / 2, this.y, 200 - (this.width - this.width / 2), 46 + k * 20, this.width - this.width / 2, this.height); // fix odd number width
    this.renderBg(minecraftClient, i, j);
    int l = 14737632;
    if (!this.active) {
      l = 10526880;
    } else if (this.isHovered()) {
      l = 16777120;
    }

    this.drawCenteredString(textRenderer, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l | MathHelper.ceil(this.alpha * 255.0F) << 24);
  }
  
}