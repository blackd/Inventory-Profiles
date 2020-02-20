package io.github.jsnimda.common.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class TextButtonWidget extends ButtonWidget {

  private String text = "";
  private String hoverText = "";
  private String inactiveText = "";
  public int pressableMargin = 0;

  public TextButtonWidget(int i, int j, String text, PressAction pressAction) {
    super(i, j, getStringWidth(text), 9, "", pressAction);
    this.text = text;
    hoverText = text;
    inactiveText = text;
  }

  public void setText(String text) {
    this.text = text;
    updateWidth();
  }

  public void setHoverText(String hoverText) {
    this.hoverText = hoverText;
    updateWidth();
  }

  public void setInactiveText(String inactiveText) {
    this.inactiveText = inactiveText;
    updateWidth();
  }
  private static int getStringWidth(String text) {
    return MinecraftClient.getInstance().textRenderer.getStringWidth(text);
  }


  private void updateWidth() {
    this.setWidth(getStringWidth(getDisplayText()));
  }
  private String getDisplayText() {
    return !this.active ? inactiveText : this.isHovered() ? hoverText : text;
  }

  boolean lastActive = true;
  boolean lastHovered = false;
  public void checkUpdateWidth() {
    if (lastHovered != isHovered() || lastActive != this.active) {
      lastHovered = isHovered();
      lastActive = this.active;
      updateWidth();
    }
  }

  @Override
  public int getWidth() {
    checkUpdateWidth();
    return super.getWidth();
  }

  @Override
  public void renderButton(int i, int j, float f) {
    isHovered = isMouseOver(i, j);
    checkUpdateWidth();
    drawString(MinecraftClient.getInstance().textRenderer, getDisplayText(), x, y, 0xFFFFFFFF);
  }

  @Override
  public boolean isMouseOver(double d, double e) {
    int x1 = x - pressableMargin;
    int y1 = y - pressableMargin;
    int x2 = x + width + pressableMargin;
    int y2 = y + height + pressableMargin;
    return this.active && this.visible && VHLine.contains(x1, y1, x2, y2, (int)d, (int)e);
  }

  @Override
  protected boolean clicked(double d, double e) {
    return isMouseOver(d, e);
  }

}