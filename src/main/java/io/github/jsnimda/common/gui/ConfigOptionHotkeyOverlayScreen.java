package io.github.jsnimda.common.gui;

import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.common.config.IConfigOption;
import io.github.jsnimda.common.config.options.ConfigHotkey;
import io.github.jsnimda.common.input.ConfigElementKeybindSetting;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ConfigOptionHotkeyOverlayScreen extends OverlayScreen {

  private ConfigHotkey configHotkey;
  private ConfigElementKeybindSetting keybindSettingElement;
  List<? extends IConfigOption> configs;
  List<ConfigOptionWidgetBase<?>> configWidgets;

  private int dialogX;
  private int dialogY;
  private int dialogWidth;
  private int dialogHeight;
  protected ConfigOptionHotkeyOverlayScreen(ConfigHotkey configHotkey) {
    super(new TranslatableText("inventoryprofiles.common.gui.config.advanced_keybind_settings"));
    this.configHotkey = configHotkey;
    this.keybindSettingElement = new ConfigElementKeybindSetting(configHotkey.getMainKeybind().getDefaultSettings(), configHotkey.getMainKeybind().getSettings());
    configs = keybindSettingElement.getConfigOptions();
    configWidgets = configs.stream().map(ConfigOptionWidgetBase::of).collect(Collectors.toList());
  }

  private static final int COLOR_WHITE  = 0xFFFFFFFF;
  private static final int COLOR_BORDER = 0xFF999999;
  private static final int COLOR_BG     = 0xFF000000;
  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    DiffuseLighting.disable();
    GlStateManager.disableDepthTest();
    GlStateManager.pushMatrix();
    GlStateManager.translatef(0, 0, 400);
    this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    configHotkey.getMainKeybind().setSettings(keybindSettingElement.getSettings());

    dialogHeight = 5 * 20 + 2 + 10;
    int maxTextW = configs.stream().mapToInt(x -> this.textRenderer.getWidth(I18n.translate("inventoryprofiles.common.gui.config." + x.getKey()))).max().orElse(0);
    dialogWidth = maxTextW + 150 + 2 + 20;
    dialogWidth = Math.max(this.textRenderer.getWidth("§l" + this.title.asString()) + 20, dialogWidth);
    dialogX = (this.width - dialogWidth) / 2;
    dialogY = (this.height - dialogHeight) / 2;

    fill(matrices, dialogX, dialogY, dialogX + dialogWidth, dialogY + dialogHeight, COLOR_BG);
    VHLine.outline(matrices, dialogX, dialogY, dialogX + dialogWidth, dialogY + dialogHeight, COLOR_BORDER);

    int y0 = dialogY + 2;
    drawCenteredString(matrices, this.textRenderer, "§l" + this.title.asString(), dialogX + dialogWidth / 2, y0 + 6, COLOR_WHITE);

    for (ConfigOptionWidgetBase<?> widget : configWidgets) {
      y0 += 20;
      String displayName = I18n.translate("inventoryprofiles.common.gui.config." + widget.configOption.getKey());
      int tx = dialogX + 10;
      int ty = y0 + 6;
      int tw = this.textRenderer.getWidth(displayName);
      // drawString(this.textRenderer, displayName, tx, ty, COLOR_WHITE);
      this.textRenderer.draw(matrices, displayName, tx, ty, COLOR_WHITE);
      if (VHLine.contains(tx, ty - 1, tx + tw, ty + 9 + 1, mouseX, mouseY)) {
        Tooltips.getInstance().addTooltip(I18n.translate("inventoryprofiles.common.gui.config.description." + widget.configOption.getKey()), mouseX, mouseY, k -> k * 2 / 3);
      }
      widget.x = dialogX + maxTextW + 2 + 10;
      widget.y = y0;
      widget.width = 150;
      widget.render(matrices, mouseX, mouseY, partialTicks);
    }

    Tooltips.getInstance().renderAll();

    GlStateManager.popMatrix();
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) {
    if (super.mouseClicked(d, e, i)) {
      return true;
    }
    if (!VHLine.contains(dialogX, dialogY, dialogX + dialogWidth, dialogY + dialogHeight, (int)d, (int)e)) { // click outside dialog
      this.onClose();
      return true;
    }
    return false;
  }

  @Override
  protected void init() {
    children.addAll(configWidgets);
  }

}