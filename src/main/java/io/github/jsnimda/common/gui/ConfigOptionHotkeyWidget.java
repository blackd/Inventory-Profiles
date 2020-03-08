package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.common.input.IKeybind;
import org.lwjgl.glfw.GLFW;

import io.github.jsnimda.common.config.options.ConfigHotkey;
import io.github.jsnimda.common.input.GlobalInputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class ConfigOptionHotkeyWidget extends ConfigOptionWidgetBase<ConfigHotkey> {

  private ButtonWidget setKeyButton = new ButtonWidget(10, 10, 200, 20, "", x -> {
    GlobalInputHandler.INSTANCE.setCurrentAssigningKeybind(configOption.getMainKeybind());
  });

  private static Identifier WIDGETS_TEXTURE = new Identifier("inventoryprofiles", "textures/gui/widgets.png");

  public boolean  isInConfigHotkeyOverlay = false;
  public IKeybind targetKeybind;

  protected ConfigOptionHotkeyWidget(ConfigHotkey configOption) {
    super(configOption);
    targetKeybind = configOption.getMainKeybind();
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);

    int textureX = 20 + ((targetKeybind.isSettingsModified() || !configOption.getAlternativeKeybinds().isEmpty()) ? 20 : 0);
    int textureY = 160 + targetKeybind.getSettings().activateOn.ordinal() * 20;
    if (targetKeybind.getKeyCodes().isEmpty()) {
      textureY = 140;
    }

    MinecraftClient.getInstance().getTextureManager().bindTexture(WIDGETS_TEXTURE);
    GlStateManager.disableDepthTest();
    blit(this.x, this.y, textureX, textureY, 20, 20, 256, 256);
    GlStateManager.enableDepthTest();

    if (VHLine.contains(this.x, this.y, this.x + 20, this.y + 20, mouseX, mouseY)) {
      // show Advanced Keybind Settings
      Tooltips.getInstance().addTooltip(getKeybindSettingsTooltip(), mouseX, mouseY);
    }

    setKeyButton.x = x + 20 + 2;
    setKeyButton.y = y;
    setKeyButton.setWidth(availableWidth - 20 - 2);
    String displayText = targetKeybind.getDisplayText();
    setKeyButton.setMessage(GlobalInputHandler.INSTANCE.getCurrentAssigningKeybind() == targetKeybind
      ? ("> §e" + displayText + "§r <") : displayText);
    setKeyButton.render(mouseX, mouseY, partialTicks);

  }

  private static String textPrefix = "inventoryprofiles.common.gui.config.";
  private static String translate(String suffix) {
    return I18n.translate(textPrefix + suffix);
  }
  private String getKeybindSettingsTooltip() {
    String yes = translate("yes");
    String no = translate("no");
    String s = "§n" + translate("advanced_keybind_settings");
    s += String.format("\n%s: %s", translate("activate_on"),      "§9" + targetKeybind.getSettings().activateOn.toString());
    s += String.format("\n%s: %s", translate("context"),          "§9" + targetKeybind.getSettings().context.toString());
    s += String.format("\n%s: %s", translate("allow_extra_keys"), "§6" + (targetKeybind.getSettings().allowExtraKeys ? yes : no));
    s += String.format("\n%s: %s", translate("order_sensitive"),  "§6" + (targetKeybind.getSettings().orderSensitive ? yes : no));
    s += "\n\n" + translate("keybind_settings_tips");
    return s;
  }

  protected void onClickKeybindSettingsIcon() {
    if (!isInConfigHotkeyOverlay) {
      MinecraftClient.getInstance().openScreen(new ConfigOptionHotkeyOverlayScreen(configOption));
    }
  }

  @Override
  protected void reset() {
    targetKeybind.resetKeyCodesToDefault();
  }

  @Override
  protected boolean resetButtonActive() {
    return targetKeybind.isKeyCodesModified();
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) {
    if (super.mouseClicked(d, e, i)) {
      return true;
    }
    if (VHLine.contains(this.x, this.y, this.x + 20, this.y + 20, (int)d, (int)e)) {
      if (i == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
        targetKeybind.resetSettingsToDefault();
      } else if (i == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
        onClickKeybindSettingsIcon();
      }
      return true;
    }
    return false;
  }

  @Override
  public List<? extends Element> children() {
    return Arrays.asList(resetButton, setKeyButton);
  }

  
}