package io.github.jsnimda.common.gui;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

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
  public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) { // ref: Widget
    Minecraft minecraft = Minecraft.getInstance();
    FontRenderer fontrenderer = minecraft.fontRenderer;
    minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
    int i = this.getYImage(this.isHovered());
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
    this.blit(this.x + this.width / 2, this.y, 200 - (this.width - this.width / 2), 46 + i * 20, this.width - this.width / 2, this.height); // fix odd number width
    this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
    int j = getFGColor();
    this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
  }
  
}