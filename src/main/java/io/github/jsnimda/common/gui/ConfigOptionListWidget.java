package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.jsnimda.common.config.CategorizedConfigOptions;
import io.github.jsnimda.common.config.IConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.resources.I18n;

public class ConfigOptionListWidget extends AnchoredListWidget<ConfigOptionListWidget.Entry> {

  private static final int COLOR_WHITE          = 0xFFFFFFFF;

  private String displayNamePrefix;
  private String descriptionPrefix;

  public ConfigOptionListWidget(String displayNamePrefix, String descriptionPrefix) {
    super(0, 0, 100, 100);
    this.displayNamePrefix = displayNamePrefix;
    this.descriptionPrefix = descriptionPrefix;
  }

  public static ConfigOptionListWidget from(CategorizedConfigOptions optionGroup, String displayNamePrefix, String descriptionPrefix) {
    ConfigOptionListWidget c = new ConfigOptionListWidget(displayNamePrefix, descriptionPrefix);
    optionGroup.getCategories().forEach(x -> {
      String categoryNameKey = x.getLeft();
      String categoryName = I18n.format(categoryNameKey);
      c.addAnchor(categoryName);
      c.addEntry(c.new CategoryEntry(categoryName));
      x.getRight().forEach(y -> {
        c.addEntry(c.new ConfigOptionEntry(y));
      });
    });
    return c;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    Tooltips.getInstance().renderAll();
  }

  public class ConfigOptionEntry extends ConfigOptionListWidget.Entry {

    private IConfigOption option;
    private ConfigOptionWidgetBase<?> optionWidget;
    private int textY = 6;
    private ConfigOptionEntry(IConfigOption option) {
      this.option = option;
      this.optionWidget = ConfigOptionWidgetBase.of(option);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
      return Arrays.asList(optionWidget, optionWidget);
    }

    @Override
    public int getHeight() {
      return 20;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, int offsetX, int offsetY, int viewportWidth) {
      boolean outOfContainer = !getContainer().isMouseOverViewport(mouseX, mouseY) || anchorHeader.isMouseOver(mouseX, mouseY);

      int xMid = offsetX + viewportWidth / 2;
      optionWidget.x = xMid;
      optionWidget.y = offsetY;
      optionWidget.width = offsetX + viewportWidth - 2 - xMid;
      optionWidget.render(outOfContainer ? -1 : mouseX, outOfContainer ? -1 : mouseY, partialTicks);

      String displayName = I18n.format(displayNamePrefix + option.getKey());
      int tx = offsetX + 2;
      int ty = offsetY + textY;
      int tw = Minecraft.getInstance().fontRenderer.getStringWidth(displayName);
      drawString(Minecraft.getInstance().fontRenderer, displayName, tx, ty, COLOR_WHITE);
      if (VHLine.contains(tx, ty - 1, tx + tw, ty + 9 + 1, mouseX, mouseY)) {
        Tooltips.getInstance().addTooltip(I18n.format(descriptionPrefix + option.getKey()), mouseX, mouseY, x -> x * 2 / 3);
      }

    }

  }
  public class CategoryEntry extends ConfigOptionListWidget.Entry {

    private String categoryName;
    private int textY = 6;

    private CategoryEntry(String categoryName) {
      this.categoryName = categoryName;
    }

    @Override
    public int getHeight() {
      return 20;
    }

    @Override
    public List<? extends IGuiEventListener> children() {
      return Collections.emptyList();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, int offsetX, int offsetY, int viewportWidth) {
      drawCenteredString(Minecraft.getInstance().fontRenderer, categoryName, offsetX + viewportWidth / 2, offsetY + textY, COLOR_WHITE);
    }
  }

  public abstract static class Entry extends AbstractGui implements AnchoredListWidget.Entry, INestedGuiEventHandler {
    @Nullable
    private IGuiEventListener focused;
    private boolean dragging;

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean bl) {
        this.dragging = bl;
    }

    public void setFocused(@Nullable IGuiEventListener element) {
        this.focused = element;
    }

    @Nullable
    public IGuiEventListener getFocused() {
        return this.focused;
    }
  }
}