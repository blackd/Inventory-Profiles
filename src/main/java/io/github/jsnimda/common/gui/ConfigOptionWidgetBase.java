package io.github.jsnimda.common.gui;

import java.util.Collections;
import java.util.List;

import io.github.jsnimda.common.config.IConfigOption;
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric;
import io.github.jsnimda.common.config.options.ConfigBoolean;
import io.github.jsnimda.common.config.options.ConfigEnum;
import io.github.jsnimda.common.config.options.ConfigHotkey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.IGuiEventListener;

public abstract class ConfigOptionWidgetBase<T extends IConfigOption> extends FocusableGui implements IRenderable {

  public int x;
  public int y;
  public int width;
  public int height;
  protected Button resetButton = new Button(10, 10, 200, 20, "", x -> reset());
  public boolean showResetButton = true;
  public int resetButtonGap = 2;
  protected final T configOption;
  protected int availableWidth;

  protected ConfigOptionWidgetBase(T configOption) {
    this.configOption = configOption;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    if (showResetButton) {
      String s = I18n.format("inventoryprofiles.common.gui.config.reset");
      int w = Minecraft.getInstance().fontRenderer.getStringWidth(s);
      int bw = w + 15;
      resetButton.x = x + width - bw;
      resetButton.y = y;
      resetButton.setWidth(bw);
      resetButton.setMessage(s);
      resetButton.active = resetButtonActive();
      resetButton.render(mouseX, mouseY, partialTicks);
    }
    availableWidth = width - (showResetButton ? resetButton.getWidth() + resetButtonGap : 0);
  }

  public static ConfigOptionWidgetBase<?> of(IConfigOption configOption) {
    if (configOption instanceof ConfigBoolean) {
      return new ConfigOptionBooleanWidget((ConfigBoolean)configOption);
    }
    if (configOption instanceof IConfigOptionPrimitiveNumeric) {
      return new ConfigOptionNumericWidget((IConfigOptionPrimitiveNumeric<?>)configOption);
    }
    if (configOption instanceof ConfigEnum) {
      return new ConfigOptionToggleableWidget<ConfigEnum<?>>((ConfigEnum<?>)configOption, x -> x.getValue().toString());
    }
    if (configOption instanceof ConfigHotkey) {
      return new ConfigOptionHotkeyWidget((ConfigHotkey)configOption);
    }

    return new ConfigOptionWidgetBase<IConfigOption>(configOption) {};
  }

  protected void reset() {
    configOption.resetToDefault();
  }

  protected boolean resetButtonActive() {
    return configOption.isModified();
  }

  @Override
  public List<? extends IGuiEventListener> children() {
    return Collections.emptyList();
  }

}