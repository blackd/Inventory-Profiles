package io.github.jsnimda.common.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import org.lwjgl.glfw.GLFW;

import io.github.jsnimda.common.config.IConfigOptionToggleable;
import net.minecraft.client.font.TextRenderer;

public class ConfigOptionToggleableButtonWidget extends RightClickableButtonWidget {

  public boolean drawShadow = true;

  public ConfigOptionToggleableButtonWidget(int i, int j, int k, int l, IConfigOptionToggleable configOptionToggleable) {
    super(i, j, k, l, VHLine.EMPTY_TEXT, (x, b) -> {
      if (b == GLFW.GLFW_MOUSE_BUTTON_LEFT) configOptionToggleable.toggleNext();
      else if (b == GLFW.GLFW_MOUSE_BUTTON_RIGHT) configOptionToggleable.togglePrevious();
    });
  }

  // why doesn't this method delegate to drawCenteredString? :mojank:
  @Override
  public void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, StringRenderable stringRenderable, int i, int j, int k) {
    if (drawShadow) {
      textRenderer.drawWithShadow(matrices, stringRenderable, (float)(i - textRenderer.getWidth(stringRenderable) / 2), (float)j, k);
    } else {
      textRenderer.draw(matrices, stringRenderable, (float)(i - textRenderer.getWidth(stringRenderable) / 2), (float)j, k);
    }
  }

  @Override
  public void drawCenteredString(MatrixStack matrices, TextRenderer textRenderer, String string, int i, int j, int k) {
    if (drawShadow) {
      textRenderer.drawWithShadow(matrices, string, (float)(i - textRenderer.getWidth(string) / 2), (float)j, k);
    } else {
      textRenderer.draw(matrices, string, (float)(i - textRenderer.getWidth(string) / 2), (float)j, k);
    }
  }

}