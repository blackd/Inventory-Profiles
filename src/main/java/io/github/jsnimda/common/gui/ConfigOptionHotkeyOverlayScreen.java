package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.jsnimda.common.config.IConfigOption;
import io.github.jsnimda.common.config.options.ConfigHotkey;
import io.github.jsnimda.common.input.ConfigElementKeybindSetting;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class ConfigOptionHotkeyOverlayScreen extends OverlayScreen {

  private ConfigHotkey configHotkey;
  private ConfigElementKeybindSetting keybindSettingElement;
  List<? extends IConfigOption> configs;
  List<ConfigOptionWidgetBase<?>> configWidgets;
  protected ConfigOptionHotkeyOverlayScreen(ConfigHotkey configHotkey) {
    super(new TranslatableText("inventoryprofiles.common.gui.config.advanced_keybind_settings"));
    this.configHotkey = configHotkey;
    this.keybindSettingElement = new ConfigElementKeybindSetting(configHotkey.getMainKeybind().getDefaultSettings(), configHotkey.getMainKeybind().getSettings());
    configs = keybindSettingElement.getConfigOptions();
    configWidgets = configs.stream().map(x -> ConfigOptionWidgetBase.of(x)).collect(Collectors.toList());
  }

  private static final int COLOR_WHITE  = 0xFFFFFFFF;
  private static final int COLOR_BORDER = 0xFF999999;
  private static final int COLOR_BG     = 0xFF000000;
  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    GuiLighting.disable();
    renderBackground(0);
    configHotkey.getMainKeybind().setSettings(keybindSettingElement.getSettings());

    int h = 5 * 20 + 2 + 10;
    int maxTextW = configs.stream().mapToInt(x -> this.font.getStringWidth(I18n.translate("inventoryprofiles.common.gui.config." + x.getKey()))).max().orElse(0);
    int w = maxTextW + 150 + 2 + 20;
    w = Math.max(this.font.getStringWidth("§l" + this.title.asFormattedString()) + 20, w);
    int x = (this.width - w) / 2;
    int y = (this.height - h) / 2;

    fill(x, y, x + w, y + h, COLOR_BG);
    VHLine.outline(x, y, x + w, y + h, COLOR_BORDER);

    int y0 = y + 2;
    drawCenteredString(this.font, "§l" + this.title.asFormattedString(), x + w / 2, y0 + 6, COLOR_WHITE);

    for (ConfigOptionWidgetBase<?> widget : configWidgets) {
      y0 += 20;
      String displayName = I18n.translate("inventoryprofiles.common.gui.config." + widget.configOption.getKey());
      int tx = x + 10;
      int ty = y0 + 6;
      int tw = this.font.getStringWidth(displayName);
      drawString(this.font, displayName, tx, ty, COLOR_WHITE);
      if (VHLine.contains(tx, ty - 1, tx + tw, ty + 9 + 1, mouseX, mouseY)) {
        Tooltips.getInstance().addTooltip(I18n.translate("inventoryprofiles.common.gui.config.description." + widget.configOption.getKey()), mouseX, mouseY, k -> k * 2 / 3);
      }
      widget.x = x + maxTextW + 2 + 10;
      widget.y = y0;
      widget.width = 150;
      widget.render(mouseX, mouseY, partialTicks);
    }

    Tooltips.getInstance().renderAll();

  }

  @Override
  protected void init() {
    configWidgets.forEach(x -> children.add(x));
  }

}