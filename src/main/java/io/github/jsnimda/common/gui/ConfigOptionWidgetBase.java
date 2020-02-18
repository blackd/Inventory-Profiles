package io.github.jsnimda.common.gui;

import java.util.Collections;
import java.util.List;

import io.github.jsnimda.common.config.IConfigOption;
import io.github.jsnimda.common.config.IConfigOptionNumeric;
import io.github.jsnimda.common.config.options.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public abstract class ConfigOptionWidgetBase<E extends IConfigOption> extends AbstractParentElement implements Drawable {

  public int x;
  public int y;
  public int width;
  public int height;
  protected ButtonWidget resetButton = new ButtonWidget(10, 10, 200, 20, "", x -> reset());
  public boolean showResetButton = true;
  public int resetButtonGap = 2;
  protected final E configOption;
  protected int availableWidth;

  protected ConfigOptionWidgetBase(E configOption) {
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
      resetButton.active = configOption.isModified();
      resetButton.render(mouseX, mouseY, partialTicks);
    }
    availableWidth = width - (showResetButton ? resetButton.getWidth() + resetButtonGap : 0);
  }

  public static ConfigOptionWidgetBase<?> of(IConfigOption configOption) {
    if (configOption instanceof ConfigBoolean) {
      return new ConfigOptionBooleanWidget((ConfigBoolean)configOption);
    }
    if (configOption instanceof IConfigOptionNumeric) {
      return new ConfigOptionNumericWidget((IConfigOptionNumeric<?>)configOption);
    }

    return new ConfigOptionWidgetBase<IConfigOption>(configOption) {};
  }

  protected void reset() {
    configOption.resetToDefault();
  }

  @Override
  public List<? extends Element> children() {
    return Collections.emptyList();
  }

}