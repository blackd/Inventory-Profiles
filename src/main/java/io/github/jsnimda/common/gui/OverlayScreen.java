package io.github.jsnimda.common.gui;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

public class OverlayScreen extends Screen {

  @Nullable
  protected Screen parent;
  protected OverlayScreen() {
    super(new StringTextComponent(""));
    this.parent = Minecraft.getInstance().currentScreen;
  }
  protected OverlayScreen(TextComponent text) {
    super(text);
    this.parent = Minecraft.getInstance().currentScreen;
  }
  protected OverlayScreen(TextComponent text, Screen parent) {
    super(text);
    this.parent = parent;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    if (parent != null) {
      parent.render(mouseX, mouseY, partialTicks);
    }
  }

  @Override
  public void onClose() {
    this.minecraft.displayGuiScreen(parent);
  }

  @Override
  public void resize(Minecraft minecraftClient, int i, int j) {
    if (parent != null) {
      parent.resize(minecraftClient, i, j);
    }
    super.resize(minecraftClient, i, j);
  }

}