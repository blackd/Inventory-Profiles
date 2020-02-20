package io.github.jsnimda.common.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;

public class Tooltips {

  private static final Tooltips INSTANCE = new Tooltips();

  public static Tooltips getInstance() {
    return INSTANCE;
  }

  public static class Tooltip extends AbstractGui {
    public List<String> strings;
    public int mouseX;
    public int mouseY;

    public Tooltip(String string, int mouseX, int mouseY) {
      this.strings = Arrays.asList(string.split("\n"));
      this.mouseX = mouseX;
      this.mouseY = mouseY;
    }

    public Tooltip(List<String> strings, int mouseX, int mouseY) {
      this.strings = strings;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
    }

    public void render() {
      // renderTooltip(false);
      Minecraft.getInstance().currentScreen.renderTooltip(strings, mouseX, mouseY);
    }

    private void renderTooltip(boolean firstLineGap) { // ref: Screen.renderTooltip
      List<String> list = strings;
      int width = Minecraft.getInstance().currentScreen.width;
      int height = Minecraft.getInstance().currentScreen.height;
      FontRenderer font = Minecraft.getInstance().fontRenderer;
      ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
      if (!list.isEmpty()) {
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        int maxStringWidth = 0;
        Iterator<String> var5 = list.iterator();

        while(var5.hasNext()) {
           String string = var5.next();
           int l = font.getStringWidth(string);
           if (l > maxStringWidth) {
              maxStringWidth = l;
           }
        }

        int p = 8;
        if (list.size() > 1) {
           p += (firstLineGap ? 2 : 0) + (list.size() - 1) * 10;
        }

        int textX = mouseX + 4;
        int textY = mouseY - p - 6;
        if (textX + maxStringWidth + 4 + 5 > width) {
           textX -= 7 + maxStringWidth;
        }
        if (textX - 4 - 5 < 0) {
          textX = width - (maxStringWidth + 4 + 5);
        }

        if (textY + p + 6 > height) {
          textY = height - p - 6;
        }
        if (textY - 6 < 0) {
            textY = 6;
        }

        this.blitOffset = 300;
        itemRenderer.zLevel = 300.0F;
        int COLOR_BG = 0xF0100010;
        this.fillGradient(textX - 3, textY - 4, textX + maxStringWidth + 3, textY - 3, COLOR_BG, COLOR_BG);
        this.fillGradient(textX - 3, textY + p + 3, textX + maxStringWidth + 3, textY + p + 4, COLOR_BG, COLOR_BG);
        this.fillGradient(textX - 3, textY - 3, textX + maxStringWidth + 3, textY + p + 3, COLOR_BG, COLOR_BG);
        this.fillGradient(textX - 4, textY - 3, textX - 3, textY + p + 3, COLOR_BG, COLOR_BG);
        this.fillGradient(textX + maxStringWidth + 3, textY - 3, textX + maxStringWidth + 4, textY + p + 3, COLOR_BG, COLOR_BG);
        int COLOR_OUTLINE_TOP = 0x505000FF;
        int COLOR_OUTLINE_BOTTOM = 0x5028007F;
        this.fillGradient(textX - 3, textY - 3 + 1, textX - 3 + 1, textY + p + 3 - 1, COLOR_OUTLINE_TOP, COLOR_OUTLINE_BOTTOM);
        this.fillGradient(textX + maxStringWidth + 2, textY - 3 + 1, textX + maxStringWidth + 3, textY + p + 3 - 1, COLOR_OUTLINE_TOP, COLOR_OUTLINE_BOTTOM);
        this.fillGradient(textX - 3, textY - 3, textX + maxStringWidth + 3, textY - 3 + 1, COLOR_OUTLINE_TOP, COLOR_OUTLINE_TOP);
        this.fillGradient(textX - 3, textY + p + 2, textX + maxStringWidth + 3, textY + p + 3, COLOR_OUTLINE_BOTTOM, COLOR_OUTLINE_BOTTOM);

        for(int t = 0; t < list.size(); ++t) {
           String string2 = (String)list.get(t);
           font.drawStringWithShadow(string2, (float)textX, (float)textY, -1);
           if (t == 0) {
              textY += (firstLineGap ? 2 : 0);
           }

           textY += 10;
        }

        this.blitOffset = 0;
        itemRenderer.zLevel = 0.0F;
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
      }
    }
  }

  public List<Tooltip> tooltips = new ArrayList<>();

  public void addTooltip(String string, int mouseX, int mouseY) {
    this.addTooltip(Arrays.asList(string.split("\n")), mouseX, mouseY);
  }
  public void addTooltip(List<String> strings, int x, int y) {
    tooltips.add(new Tooltip(strings, x, y));
  }
  public void renderAll() {
    tooltips.forEach(x -> x.render());
    tooltips.clear();
  }

  public void addTooltip(String string, int mouseX, int mouseY, int maxWidth) {
    this.addTooltip(Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(string, maxWidth), mouseX, mouseY);
  }

  public void addTooltip(String string, int mouseX, int mouseY, Function<Integer, Integer> maxWidthProvider) {
    this.addTooltip(Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(string, maxWidthProvider.apply(
      Minecraft.getInstance().currentScreen.width
    )), mouseX, mouseY);
  }

}