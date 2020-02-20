package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import io.github.jsnimda.common.config.IConfigOptionToggleable;
import net.minecraft.client.gui.IGuiEventListener;

public class ConfigOptionToggleableWidget<T extends IConfigOptionToggleable> extends ConfigOptionWidgetBase<T> {

  private ConfigOptionToggleableButtonWidget toggleButton;
  public Function<T, String> displayText;

  protected ConfigOptionToggleableWidget(T configOption, Function<T, String> displayText) {
    super(configOption);
    toggleButton = new ConfigOptionToggleableButtonWidget(10, 10, 200, 20, configOption);
    this.displayText = displayText;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    toggleButton.x = x;
    toggleButton.y = y;
    toggleButton.setWidth(availableWidth);
    if (displayText != null) {
      toggleButton.setMessage(displayText.apply(configOption));
    }
    toggleButton.render(mouseX, mouseY, partialTicks);
  }

  @Override
  public List<? extends IGuiEventListener> children() {
    return Arrays.asList(resetButton, toggleButton);
  }
}