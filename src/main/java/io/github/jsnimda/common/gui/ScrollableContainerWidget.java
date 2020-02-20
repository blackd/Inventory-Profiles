package io.github.jsnimda.common.gui;

import java.util.Optional;

import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;

public class ScrollableContainerWidget extends DrawableHelper implements Drawable, Element {

  private int contentHeight = 0;
  private int scrollY = 0;
  private int x;
  private int y;
  private int width;
  private int height;

  public boolean renderBorder = true;
  public int borderColor = COLOR_BORDER;
  public int padding = 3;
  private int scrollbarWidth = 6;

  private Optional<IContentRenderer> contentRenderer = Optional.empty();

  public void setContentRenderer(IContentRenderer contentRenderer) {
    this.contentRenderer = Optional.ofNullable(contentRenderer);
  }

  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    setScrollY(scrollY);
  }
  
  //#region bounds getter
  public int getX() {
    return x;
  }
  public int getY() {
    return y;
  }
  public int getWidth() {
    return width;
  }
  public int getHeight() {
    return height;
  }
  //#endregion
  
  //#region viewport getter
  public int getViewportX() {
    return x + padding;
  }
  public int getViewportY() {
    return y + padding;
  }
  public int getViewportWidth() {
    return width - scrollbarWidth - padding * 2;
  }
  public int getViewportHeight() {
    return height - padding * 2;
  }
  //#endregion
  
  public int getScrollYMax() {
    return Math.max(0, contentHeight - getViewportHeight());
  }
  public int getScrollbarThumbHeight() {
    int vh = getViewportHeight();
    if (getScrollYMax() == 0) return vh;
    return vh * vh / contentHeight;
  }
  public int getScrollbarYOffsetMax() {
    return getViewportHeight() - getScrollbarThumbHeight();
  }
  public int getScrollbarYOffset() {
    return scrollYToScrollbarYOffset(scrollY);
  }

  private int scrollbarYOffsetToScrollY(int scrollbarY) {
    if (getScrollbarYOffsetMax() == 0) return 0;
    return map(getScrollbarYOffsetMax(), getScrollYMax(), scrollbarY);
  }
  private int scrollYToScrollbarYOffset(int scrollY) {
    if (getScrollYMax() == 0) return 0;
    return map(getScrollYMax(), getScrollbarYOffsetMax(), scrollY);
  }
  private int map(int inputMax, int outputMax, int input) { // min = 0
    return (int)Math.round(1.0 * input * outputMax / inputMax);
  }

  public void setScrollY(int newScrollY) {
    int oldScrollY = scrollY;
    if (newScrollY < 0) scrollY = 0;
    else scrollY = newScrollY > getScrollYMax() ? getScrollYMax() : newScrollY;
    if (oldScrollY != newScrollY) {
      scrollYChanged(newScrollY, oldScrollY);
    }
  }
  private void scrollYChanged(int newScrollY, int oldScrollY) {
  }
  public void setContentHeight(int contentHeight) {
    this.contentHeight = contentHeight;
    setScrollY(scrollY);
  }

  public int getContentHeight() {
    return contentHeight;
  }
  public int getScrollY() {
    return scrollY;
  }

  // private static final int COLOR_WHITE              = 0xFFFFFFFF;
  private static final int COLOR_BORDER             = 0xFF999999;
  private static final int COLOR_SCROLLBAR_BG       = 0x80000000; // 0xFF000000; ref: EntryListWidget.render
  private static final int COLOR_SCROLLBAR_SHADOW   = 0xFF989898; // 0xFF808080;
  private static final int COLOR_SCROLLBAR          = 0xFFD8D8D8; // 0xFFC0C0C0;
  private static final int COLOR_SCROLLBAR_HOVER_SHADOW   = 0xFFC0C0C0;
  private static final int COLOR_SCROLLBAR_HOVER          = 0xFFFFFFFF;
  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    if (renderBorder) {
      VHLine.outline(x, y, x + width, y + height, borderColor);
    }

    // render scrollbar, ref: EntryListWidget.render
    if (isScrollbarVisible()) {
      int x1 = getViewportX() + getViewportWidth();
      int y1 = getViewportY();
      int x2 = x1 + scrollbarWidth;
      int y2 = y1 + getViewportHeight();
      int sy1 = y1 + getScrollbarYOffset();
      int sy2 = sy1 + getScrollbarThumbHeight();
      fill(x1, y1, x2, y2, COLOR_SCROLLBAR_BG);
      boolean hover = VHLine.contains(x1, sy1, x2, sy2, mouseX, mouseY) || scrolling;
      fill(x1, sy1, x2, sy2, hover ? COLOR_SCROLLBAR_HOVER_SHADOW : COLOR_SCROLLBAR_SHADOW);
      fill(x1, sy1, x2 - 1, sy2 - 1, hover ? COLOR_SCROLLBAR_HOVER : COLOR_SCROLLBAR);
      // fill(x1, sy1, x2, sy2, hover ? COLOR_SCROLLBAR_HOVER : COLOR_SCROLLBAR);
    }

    // render content
    if (contentRenderer.isPresent()) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0, 0, -400.0F); // ref: AdvancementsScreen widget
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(GL11.GL_GEQUAL); // 518

      // draw mask
      GlStateManager.disableAlphaTest();
      int vx = getViewportX();
      int vy = getViewportY();
      int vw = getViewportWidth();
      int vh = getViewportHeight();
      fill(vx, vy, vx + vw, vy + vh, 0);
      GlStateManager.enableAlphaTest();

      // exit draw mask
      GlStateManager.depthFunc(GL11.GL_LEQUAL); // 515, return default
      contentRenderer.get().render(mouseX, mouseY, partialTicks, vx, vy, vw, vh, scrollY);
  
      GlStateManager.popMatrix();
      GlStateManager.disableDepthTest();
    }

  }

  public boolean isScrollbarVisible() {
    return getScrollYMax() > 0;
  }

  private boolean hoverScrollbarThumb(int mouseX, int mouseY) {
    int x1 = getViewportX() + getViewportWidth();
    int y1 = getViewportY();
    int x2 = x1 + scrollbarWidth;
    int sy1 = y1 + getScrollbarYOffset();
    int sy2 = sy1 + getScrollbarThumbHeight();
    return VHLine.contains(x1, sy1, x2, sy2, mouseX, mouseY);
  }
  private boolean hoverScrollbar(int mouseX, int mouseY) {
    int x1 = getViewportX() + getViewportWidth();
    int y1 = getViewportY();
    int x2 = x1 + scrollbarWidth;
    int y2 = y1 + getViewportHeight();
    return VHLine.contains(x1, y1, x2, y2, mouseX, mouseY);
  }

  // scrolling logic / ui events
  
  private boolean scrolling = false;
  private double scrollingInitMouseY;
  private int scrollingInitScrollbarYOffset;
  @Override
  public boolean mouseClicked(double d, double e, int i) {
    if (isScrollbarVisible()) {
      scrolling = hoverScrollbar((int)d,(int)e);
      if (i == 0 && scrolling) { // ref: vscode scrollbar mechanic
        if (!hoverScrollbarThumb((int)d,(int)e)) {
          int y1 = getViewportY();
          // e = y1 + yoffset + sh/2
          int newYOffset = (int)e - y1 - getScrollbarThumbHeight() / 2;
          setScrollY(scrollbarYOffsetToScrollY(newYOffset));
        }
        scrollingInitMouseY = e;
        scrollingInitScrollbarYOffset = getScrollbarYOffset();
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean mouseReleased(double d, double e, int i) {
    scrolling = false;
    return false;
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
    if (button == 0 && scrolling) {
      double shiftY = mouseY - scrollingInitMouseY;
      setScrollY(scrollbarYOffsetToScrollY(scrollingInitScrollbarYOffset + (int)shiftY));
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseScrolled(double d, double e, double f) { // f = 1 or -1
    if (isScrollbarVisible()) {
      setScrollY(scrollY - (int) (f * 20));
    }
    return true;
  }

  @Override
  public boolean isMouseOver(double d, double e) {
    return VHLine.contains(x, y, x + width, y + height, (int)d, (int)e);
  }

  public boolean isMouseOverViewport(double d, double e) {
    int x1 = getViewportX();
    int y1 = getViewportY();
    int x2 = x1 + getViewportWidth();
    int y2 = y1 + getViewportHeight();
    return VHLine.contains(x1, y1, x2, y2, (int)d, (int)e);
  }

  public interface IContentRenderer {
    void render(int mouseX, int mouseY, float partialTicks, int viewportX, int viewportY, int viewportWidth, int viewportHeight, int scrollY);
  }
}