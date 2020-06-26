package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.List;

import io.github.jsnimda.common.config.options.ConfigBoolean;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ConfigOptionBooleanWidget extends ConfigOptionWidgetBase<ConfigBoolean> {
  private ConfigOptionToggleableButtonWidget booleanButton;
  public String trueTextKey = "inventoryprofiles.common.gui.config.true";
  public String falseTextKey = "inventoryprofiles.common.gui.config.false";
  protected ConfigOptionBooleanWidget(ConfigBoolean configOption) {
    super(configOption);
    this.booleanButton = new ConfigOptionToggleableButtonWidget(10, 10, 200, 20, configOption);
    // booleanButton.drawShadow = false;
  }
  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    super.render(matrices, mouseX, mouseY, partialTicks);
    booleanButton.x = x;
    booleanButton.y = y;
    booleanButton.setWidth(availableWidth);
    booleanButton.setMessage(new TranslatableText(configOption.getBooleanValue() ? trueTextKey : falseTextKey));
    booleanButton.render(matrices, mouseX, mouseY, partialTicks);
  }

  @Override
  public List<? extends Element> children() {
    return Arrays.asList(resetButton, booleanButton);
  }
}