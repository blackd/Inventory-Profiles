package io.github.jsnimda.common.gui;

import org.lwjgl.glfw.GLFW;

import io.github.jsnimda.common.config.IConfigOptionToggleable;
import net.minecraft.client.gui.FontRenderer;

public class ConfigOptionToggleableButtonWidget extends RightClickableButtonWidget {

  public boolean drawShadow = true;

  public ConfigOptionToggleableButtonWidget(int i, int j, int k, int l, IConfigOptionToggleable configOptionToggleable) {
    super(i, j, k, l, "", (x, b) -> {
      if (b == GLFW.GLFW_MOUSE_BUTTON_LEFT) configOptionToggleable.toggleNext();
      else if (b == GLFW.GLFW_MOUSE_BUTTON_RIGHT) configOptionToggleable.togglePrevious();
    });
  }

  @Override
  public void drawCenteredString(FontRenderer textRenderer, String string, int i, int j, int k) {
    if (drawShadow) {
      textRenderer.drawStringWithShadow(string, (float) (i - textRenderer.getStringWidth(string) / 2), (float) j, k);
    } else {
      textRenderer.drawString(string, (float)(i - textRenderer.getStringWidth(string) / 2), (float)j, k);
    }
  }

}