package io.github.jsnimda.common.gui;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.math.MathHelper;

public class RightClickableButtonWidget extends AbstractButtonWidget {

  protected final BiConsumer<RightClickableButtonWidget, Integer> pressAction;

  public RightClickableButtonWidget(int x, int y, int width, int height, Text text, BiConsumer<RightClickableButtonWidget, Integer> pressAction) {
    super(x, y, width, height, text);
    this.pressAction = pressAction;
  }

  /**
   * @deprecated Use {@link #RightClickableButtonWidget(int, int, int, int, Text, BiConsumer)} instead.
   */
  @Deprecated
  public RightClickableButtonWidget(int i, int j, int k, int l, String string, BiConsumer<RightClickableButtonWidget, Integer> pressAction) {
    this(i, j, k, l, new LiteralText(string), pressAction);
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) { // ref: AbstractButtonWidget
    if (this.active && this.visible) {
      boolean bl = this.clicked(d, e);
      if (bl) {
        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
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
        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
        this.pressAction.accept(this, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        return true;
      }
    } else {
      return false;
    }
  }

  @Override
  public void renderButton(MatrixStack matrices, int i, int j, float f) { // ref: AbstractButtonWidget
    MinecraftClient minecraftClient = MinecraftClient.getInstance();
    TextRenderer textRenderer = minecraftClient.textRenderer;
    minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
    int k = this.getYImage(this.isHovered());
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    this.drawTexture(matrices, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
    this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - (this.width - this.width / 2), 46 + k * 20, this.width - this.width / 2, this.height); // fix odd number width
    this.renderBg(matrices, minecraftClient, i, j);
    int l = this.active ? 16777215 : 10526880;
    this.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l | MathHelper.ceil(this.alpha * 255.0F) << 24);
  }
  
}