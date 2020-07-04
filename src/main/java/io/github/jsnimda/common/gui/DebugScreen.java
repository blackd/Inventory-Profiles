package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.input.KeyCodes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class DebugScreen extends OverlayScreen {

  private static final int COLOR_TEXT_BG = 0x90505050;
  private static final int COLOR_TEXT = 0xE0E0E0;
  private static final int COLOR_WHITE = 0xFFFFFFFF;
  private static final int COLOR_BLACK = 0xFF000000;

  private int textPosition = 0; // 0-3: top-left / top-right / bottom-right / bottom-left
  private int toggleColor = 0;

  public static class DebugInfos {
    public static int width;
    public static int height;
    public static int mouseX;
    public static int mouseY;

    public static int[] keys = new int[0];
    public static int[] buttons = new int[0];

    public static int key = -1;

    public static void onKey(int key, int scanCode, int action, int modifiers) {
      keys = new int[] { key, scanCode, action, modifiers };
      DebugInfos.key = key;
    }
    public static void onMouseButton(int button, int action, int mods) {
      buttons = new int[] { button, action, mods };
      key = button - 100;
    }

    public static List<String> getString() {
      String s = "";
      s += String.format("x: %s , y: %s\nw: %s , h: %s", mouseX, mouseY, width, height);
      s += "\nonKey: " + Arrays.stream(keys).mapToObj(String::valueOf).collect(Collectors.joining(", "));
      s += "\nonMouse: " + Arrays.stream(buttons).mapToObj(String::valueOf).collect(Collectors.joining(", "));
      String name = KeyCodes.getKeyName(key);
      s += String.format("\nKey: %s (%s)", name, KeyCodes.getFriendlyName(name));
      s += "\nPressing keys: " + GlobalInputHandler.INSTANCE.getPressedKeys().stream().map(x -> KeyCodes.getFriendlyName(x)).collect(Collectors.joining(" + "));
      return Arrays.asList(s.trim().split("\n"));
    }
  }

  private List<String> getStrings() {
    DebugInfos.width = width;
    DebugInfos.height = height;
    return DebugInfos.getString();
  }

  private void drawTexts() {
    List<String> strings = getStrings();
    int bgh = 9;
    int y1 = textPosition < 2 ? 1 : this.height - 1 - bgh * strings.size(); // is top
    for (String s : strings) {
      int w = this.font.getStringWidth(s);
      int bgw = w + 2;
      int x1 = textPosition % 3 == 0 ? 1 : this.width - bgw - 1; // is left
      int x2 = x1 + bgw;
      int y2 = y1 + bgh;
      fill(x1, y1, x2, y2, COLOR_TEXT_BG);
      this.font.drawString(s, x1 + 1, y1 + 1, COLOR_TEXT);
      y1 += bgh;
    }
  }

  private boolean textBoundingBoxContains(int x, int y) {
    List<String> strings = getStrings();
    int bgh = 9;
    int y1 = textPosition < 2 ? 1 : this.height - 1 - bgh * strings.size(); // is top
    for (String s : strings) {
      int w = this.font.getStringWidth(s);
      int bgw = w + 2;
      int x1 = textPosition % 3 == 0 ? 1 : this.width - bgw - 1; // is left
      int x2 = x1 + bgw;
      int y2 = y1 + bgh;
      if (VHLine.contains(x1, y1, x2, y2, x, y)) {
        return true;
      }
      y1 += bgh;
    }
    return false;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    
    DebugInfos.mouseX = mouseX;
    DebugInfos.mouseY = mouseY;

    if (textBoundingBoxContains(mouseX, mouseY)) {
      textPosition = (textPosition + 1) % 4;
    }

    // GuiLighting.disable();
    GlStateManager.disableLighting();
    GlStateManager.disableDepthTest();

    drawTexts();

    if (toggleColor < 2) {
      int color = toggleColor == 0 ? COLOR_WHITE : COLOR_BLACK;
      VHLine.v(mouseX, 1, this.height - 2, color);
      VHLine.h(1, this.width - 2, mouseY, color);
    }
  }

  public static void open() {
    if (Minecraft.getInstance().currentScreen instanceof DebugScreen) return;
    DebugScreen d = new DebugScreen(Minecraft.getInstance().currentScreen);
    Minecraft.getInstance().displayGuiScreen(d);
  }

  public static boolean isOpened() {
    return Minecraft.getInstance().currentScreen instanceof DebugScreen;
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) {
    if (i == 0) {
      toggleColor = (toggleColor + 1) % 3;
    }
    return super.mouseClicked(d, e, i);
  }

  private DebugScreen(Screen parent) {
    super(new StringTextComponent(""), parent);
  }

}