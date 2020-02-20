package io.github.jsnimda.common.gui;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.math.MathHelper;

public class RightClickableButtonWidget extends AbstractButtonWidget {

  protected final BiConsumer<RightClickableButtonWidget, Integer> pressAction;

  public RightClickableButtonWidget(int i, int j, int k, int l, String string, BiConsumer<RightClickableButtonWidget, Integer> pressAction) {
    super(i, j, k, l, string);
    this.pressAction = pressAction;
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
  public void renderButton(int i, int j, float f) { // ref: AbstractButtonWidget
    super.renderButton(i, j, f);
    // this.blit(this.x + this.width / 2, this.y, 200 - (this.width - this.width / 2), 46 + k * 20, this.width - this.width / 2, this.height); // fix odd number width
  }
  
}