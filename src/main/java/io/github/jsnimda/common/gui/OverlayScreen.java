package io.github.jsnimda.common.gui;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class OverlayScreen extends Screen {

  @Nullable
  protected Screen parent;
  protected OverlayScreen() {
    super(new LiteralText(""));
    this.parent = MinecraftClient.getInstance().currentScreen;
  }
  protected OverlayScreen(Text text) {
    super(text);
    this.parent = MinecraftClient.getInstance().currentScreen;
  }
  protected OverlayScreen(Text text, Screen parent) {
    super(text);
    this.parent = parent;
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    if (parent != null) {
      parent.render(matrices, mouseX, mouseY, partialTicks);
    }
  }

  @Override
  public void onClose() {
    this.client.openScreen(parent);
  }

  @Override
  public void resize(MinecraftClient minecraftClient, int i, int j) {
    if (parent != null) {
      parent.resize(minecraftClient, i, j);
    }
    super.resize(minecraftClient, i, j);
  }

}