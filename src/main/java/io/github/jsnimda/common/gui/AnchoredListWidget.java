package io.github.jsnimda.common.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public class AnchoredListWidget<E extends AnchoredListWidget.Entry> extends AbstractParentElement implements Drawable {

  private List<Element> children = new ArrayList<>();
  private List<E> entries = new ArrayList<>();
  private int entriesTotalHeight = 0;
  private int x;
  private int y;
  private int width;
  private int height;
  public boolean renderBorder = true;
  public boolean showAnchorsHeader = true;
  protected int anchorHeaderHeight = 13;

  private ScrollableContainerWidget container = new ScrollableContainerWidget();

  protected AnchorHeader anchorHeader = new AnchorHeader();

  private static final int COLOR_ANCHOR_BORDER      = 0x80999999;
  private static final int COLOR_ANCHOR_BG          = 0x40999999;
  private static final int COLOR_ANCHOR_BORDER_HOVER      = 0xFF999999;
  private static final int COLOR_ANCHOR_BG_HOVER          = 0xC0999999;
  protected class AnchorHeader extends AbstractParentElement implements Drawable {
    protected class Anchor {
      public String anchorDisplayText;
      public TextButtonWidget textButtonWidget;
      public int toScrollY;
      public int rowIndex;
      public Anchor(String displayText, int toScrollY, int rowIndex) {
        this.anchorDisplayText = displayText;
        this.toScrollY = toScrollY;
        this.rowIndex = rowIndex;
        textButtonWidget = new TextButtonWidget(0, 0, displayText, x -> {
          container.setScrollY(toScrollY);
        });
        textButtonWidget.setHoverText("§e§n" + displayText); // underline
        textButtonWidget.setInactiveText("§e§n§l" + displayText); // underline + bold
        textButtonWidget.visible = false;
        textButtonWidget.pressableMargin = 2;
      }
    }
    private List<Anchor> anchors = new ArrayList<>();
    private boolean expanded = false;
    private final int SEPARATOR_WIDTH = 10;
    private final int DOT_DOT_DOT_WIDTH = MinecraftClient.getInstance().textRenderer.getWidth(" ... ...");
    private int rowHeight = 13;

    private int currentTextX = 0;
    private int currentTextRow = 0;

    private int hoveringY = -1; // reset to -1 if unhover

    private int getTextOffsetY() {
      return (rowHeight + 1) / 2 - 4;
    }

    private int getTotalTextRow() {
      return currentTextRow + 1;
    }

    private int getViewingTextRow() {
      if (anchors.isEmpty()) {
        return 0;
      }
      int index = getViewingAnchorIndex();
      return anchors.get(index).rowIndex;
    }

    private int getExpandedHeight() {
      return rowHeight * getTotalTextRow() + 1;
    }

    private int getExpandedY() {
      if (hoveringY == -1) {
        hoveringY = Math.max(7, getHiddenStartY());
      }
      return hoveringY;
    }

    private int getHiddenStartY() {
      return y - getViewingTextRow() * rowHeight;
    }

    private int getAvailableStringWidth() {
      return width - 20 - DOT_DOT_DOT_WIDTH;
    }

    private int getStartX() {
      return x + 10;
    }

    public void addAnchor(String displayText) {
      addAnchor(displayText, entriesTotalHeight);
    }
    public void addAnchor(String displayText, int toScrollY) {
      int w = MinecraftClient.getInstance().textRenderer.getWidth("§n" + displayText);
      if (w + currentTextX > getAvailableStringWidth()) {
        currentTextX = 0;
        currentTextRow++;
      }
      currentTextX += w + SEPARATOR_WIDTH;
      anchors.add(new Anchor(displayText, toScrollY, currentTextRow));
    }

    private int getViewingAnchorIndex() {
      int scrollY = container.getScrollY();
      for (int i = 0; i < anchors.size(); i++) {
        if (anchors.get(i).toScrollY >= scrollY) {
          if (anchors.get(i).toScrollY - scrollY <= container.getViewportHeight() / 2) {
            return i;
          } else {
            return Math.max(0, i - 1);
          }
        }
      }
      return anchors.size() - 1;
    }

    @Override
    public List<? extends Element> children() {
      return anchors.stream().map(x -> x.textButtonWidget).collect(Collectors.toList());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
      isMouseOver(mouseX, mouseY); // update expanded state
      if (expanded) {
        int y1 = getExpandedY();
        int y2 = getExpandedY() + getExpandedHeight();
        VHLine.h(matrices, x, x + width - 1, y1, COLOR_ANCHOR_BORDER_HOVER);
        VHLine.h(matrices, x, x + width - 1, y2, COLOR_ANCHOR_BORDER_HOVER);
        fill(matrices, x, y1 + 1, x + width, y2, COLOR_ANCHOR_BG_HOVER);
      } else {
        VHLine.h(matrices, x, x + width - 1, y, COLOR_ANCHOR_BORDER);
        VHLine.h(matrices, x, x + width - 1, y + anchorHeaderHeight, COLOR_ANCHOR_BORDER);
        fill(matrices, x, y + 1, x + width, y + anchorHeaderHeight, COLOR_ANCHOR_BG);
      }
      if (!anchors.isEmpty()) {
        int startY = expanded ? getExpandedY() : getHiddenStartY();
        int viewingAnchorIndex = getViewingAnchorIndex();
        int viewingRowIndex = anchors.get(viewingAnchorIndex).rowIndex;
        int offsetX = 0;
        int lastRowIndex = 0;
        for (int i = 0; i < anchors.size(); i++) {
          Anchor anchor = anchors.get(i);
          if (anchor.rowIndex != lastRowIndex) {
            offsetX = 0;
            lastRowIndex = anchor.rowIndex;
          }
          TextButtonWidget textWidget =  anchor.textButtonWidget;
          textWidget.active = i != viewingAnchorIndex;
          textWidget.visible = expanded || anchor.rowIndex == viewingRowIndex;
          textWidget.x = getStartX() + offsetX;
          textWidget.y = startY + getTextOffsetY() + anchor.rowIndex * rowHeight;
          textWidget.render(matrices, mouseX, mouseY, partialTicks);
          offsetX += textWidget.getWidth() + SEPARATOR_WIDTH;
          if (!expanded && getTotalTextRow() > 1 && anchor.rowIndex == viewingRowIndex
              && (i + 1 >= anchors.size() || anchor.rowIndex != anchors.get(i + 1).rowIndex)) {
            // this method doesn't exist anymore, thanks 1.16
            //drawString(MinecraftClient.getInstance().textRenderer, " ... ...", getStartX() + offsetX - SEPARATOR_WIDTH, y + getTextOffsetY(), COLOR_WHITE);
            MinecraftClient.getInstance().textRenderer.draw(matrices, " ... ...", getStartX() + offsetX - SEPARATOR_WIDTH, y + getTextOffsetY(), COLOR_WHITE);
          }
        }
      }
    }

    @Override
    public boolean isMouseOver(double d, double e) {
      boolean result;
      if (!expanded) {
        result = VHLine.contains(x, y, x + width, y + anchorHeaderHeight + 1, (int)d, (int)e);
      } else {
        int y1 = getExpandedY();
        result = VHLine.contains(x, y1, x + width, y1 + getExpandedHeight(), (int)d, (int)e);
      }
      expanded = result && getTotalTextRow() > 1;
      if (!expanded) {
        hoveringY = -1;
      }
      return result;
    }

    public void sizeChanged() {
      // recalc
      List<Anchor> a = anchors;
      anchors = new ArrayList<>();
      currentTextX = 0;
      currentTextRow = 0;
      a.forEach(x -> addAnchor(x.anchorDisplayText, x.toScrollY));
    }
  }

  public AnchoredListWidget(int x, int y, int width, int height) {
    children.add(anchorHeader);
    children.add(container);
    setBounds(x, y, width, height);
    container.setContentRenderer((matrices, mx, my, p, vx, vy, vw, vh, sy) -> {
      int dy = vy - sy;
      for (E e : entries) {
        int eh = e.getHeight();
        if (dy + eh > vy && dy < vy + vh) {
          e.render(matrices, mx, my, p, vx, dy, vw);
        }
        dy += eh;
      }
    });
    container.renderBorder = false;
    renderBorder = false;
  }

  public ScrollableContainerWidget getContainer() {
    return container;
  }

  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    container.setBounds(x, y + anchorHeaderHeight, width, height - anchorHeaderHeight);
    anchorHeader.sizeChanged();
  }

  @Override
  public List<Element> children() {
    return children;
  }

  private static final int COLOR_WHITE              = 0xFFFFFFFF;
  private static final int COLOR_BORDER             = 0xFF999999;
  public int borderColor = COLOR_BORDER;
  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    if (renderBorder) {
      VHLine.outline(matrices, x, y, x + width, y + height, borderColor);
    }

    container.render(matrices, mouseX, mouseY, partialTicks);

    // render header
    anchorHeader.render(matrices, mouseX, mouseY, partialTicks);
  }


  public void addEntry(E entry) {
    entries.add(entry);
    children.add(entry);
    entriesTotalHeight += entry.getHeight();
    container.setContentHeight(entriesTotalHeight);
  }

  public void addAnchor(String displayText) {
    anchorHeader.addAnchor(displayText);
  }

  @Override
  public boolean isMouseOver(double d, double e) {
    return VHLine.contains(x, y, x + width, y + height, (int)d, (int)e) || anchorHeader.isMouseOver(d, e);
  }

  @Override
  public boolean mouseReleased(double d, double e, int i) {
    if (super.mouseReleased(d, e, i)) {
      return true;
    }
    return container.mouseReleased(d, e, i);
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) {
    if (!isMouseOver(d, e)) {
      return false;
    }
    if (anchorHeader.isMouseOver(d, e)) {
      anchorHeader.mouseClicked(d, e, i);
      return true;
    }
    if (container.isMouseOverViewport(d, e)) {
      return super.mouseClicked(d, e, i);
    }
    if (container.isMouseOver(d, e)) {
      if (container.mouseClicked(d, e, i)) {
        this.setFocused(container); // super.mouseClicked
        if (i == 0) {
           this.setDragging(true);
        }
        return true;
      }
    }
    return false;
  }

  public static interface Entry extends Element {
    int getHeight();
    void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, int offsetX, int offsetY, int viewportWidth);
  }


}