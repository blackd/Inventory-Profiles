package io.github.jsnimda.common.gui;

import java.util.Collections;
import java.util.List;

import io.github.jsnimda.common.config.IConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class ConfigOptionWidget extends AbstractParentElement implements Drawable {

  public int x;
  public int y;
  public int width;
  public int height;
  private ButtonWidget resetButton = new ButtonWidget(10, 10, 200, 20, "", x -> reset());
  public boolean showResetButton = true;
  public int resetButtonGap = 2;
  private IConfigOption configOption;

  private ConfigOptionWidget(IConfigOption configOption) {
    this.configOption = configOption;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    if (showResetButton) {
      String s = I18n.translate("inventoryprofiles.common.gui.config.reset");
      int w = MinecraftClient.getInstance().textRenderer.getStringWidth(s);
      int bw = w + 15;
      resetButton.setWidth(bw);
      resetButton.setMessage(s);
      resetButton.x = x + width - bw;
      resetButton.y = y;
      resetButton.render(mouseX, mouseY, partialTicks);
    }
  }

  public static ConfigOptionWidget of(IConfigOption configOption) {
    return new ConfigOptionWidget(configOption);
  }

  protected void reset() {
  }


  @Override
  public List<? extends Element> children() {
    return Collections.emptyList();
  }
  

}