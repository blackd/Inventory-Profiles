package io.github.jsnimda.common.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.common.config.options.ConfigHotkey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class ConfigOptionHotkeyWidget extends ConfigOptionWidgetBase<ConfigHotkey> {

  private ButtonWidget setKeyButton = new ButtonWidget(10, 10, 200, 20, "", x -> {});

  private static Identifier WIDGETS_TEXTURE = new Identifier("inventoryprofiles", "textures/gui/widgets.png");

  protected ConfigOptionHotkeyWidget(ConfigHotkey configOption) {
    super(configOption);
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    setKeyButton.x = x + 20 + 2;
    setKeyButton.y = y;
    setKeyButton.setWidth(availableWidth - 20 - 2);
    setKeyButton.setMessage(configOption.getMainKeybind().getDisplayText());
    setKeyButton.render(mouseX, mouseY, partialTicks);

    int textureX = 20 + ((configOption.getMainKeybind().isSettingsModified() || !configOption.getAlternativeKeybinds().isEmpty()) ? 20 : 0);
    int textureY = 160 + configOption.getMainKeybind().getSettings().activateOn.ordinal() * 20;
    if (configOption.getMainKeybind().getKeyCodes().isEmpty()) {
      textureY = 140;
    }
    
    MinecraftClient.getInstance().getTextureManager().bindTexture(WIDGETS_TEXTURE);
    GlStateManager.disableDepthTest();
    blit(this.x, this.y, textureX, textureY, 20, 20, 256, 256);
    GlStateManager.enableDepthTest();
  }

  
}