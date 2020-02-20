package io.github.jsnimda.common.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextComponent;

public class ConfigScreenBase extends Screen {

  private static final int COLOR_WHITE          = 0xFFFFFFFF;
  private List<Pair<String, Runnable>> navigationButtons = new ArrayList<>();
  private boolean buttonsDirty = false;
  protected int userDefinedButtonsWidth = -1;
  protected int calculatedButtonsWidth = 10;
  protected int defaultButtonsWidth = -1;

  public Optional<AnchoredListWidget<?>> currentConfigList = Optional.empty();

  private int selectedIndex = -1;

  private List<Button> navigationButtonWidgets = new ArrayList<>();

  public boolean autoScrollToTop = true;

  protected ConfigScreenBase(TextComponent text) {
    super(text);
  }

  public int getScrollY() {
    return currentConfigList.map(x -> x.getContainer().getScrollY()).orElse(0);
  }
  public void setScrollY(int scrollY) {
    currentConfigList.ifPresent(x -> x.getContainer().setScrollY(scrollY));
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    renderPre(mouseX, mouseY, partialTicks);
    boolean overList = currentConfigList.map(x -> x.isMouseOver(mouseX, mouseY)).orElse(false);
    for(int k = 0; k < this.buttons.size(); ++k) { // super.render
      this.buttons.get(k).render(overList ? -1 : mouseX, overList ? -1 : mouseY, partialTicks);
    }

    this.drawString(this.font, this.title.getFormattedText(), 20, 10, COLOR_WHITE);

    currentConfigList.ifPresent(x -> x.render(mouseX, mouseY, partialTicks));

  }

  public void renderPre(int mouseX, int mouseY, float partialTicks) { // for override
  }

  @Override
  public List<? extends IGuiEventListener> children() {
    List<IGuiEventListener> newList = new ArrayList<>();
    currentConfigList.ifPresent(x -> newList.add(x));
    newList.addAll(children);
    return newList;
  }

  @Override
  protected void init() {
    if (buttonsDirty) {
      buttonsDirty = false;
      calculatedButtonsWidth = 0;
      navigationButtons.forEach(x -> {
        int w = this.font.getStringWidth(x.getLeft());
        calculatedButtonsWidth = Math.max(w, calculatedButtonsWidth);
      });
      calculatedButtonsWidth = calculatedButtonsWidth == 0 ? 10 : calculatedButtonsWidth + 20;
    }
    navigationButtonWidgets.clear();
    int x = 10;
    int y = 30;
    int h = 22;
    int k = 0;
    int w = getButtonsWidth();
    for (Pair<String, Runnable> e : navigationButtons) {
      final int ind = k++;
      Button b = new Button(x, y + h * ind, w, 20, e.getLeft(), m -> setSelectedIndex(ind));
      b.active = selectedIndex != ind;
      this.addButton(b);
      navigationButtonWidgets.add(b);
    }

    resizeListWidget();
  }

  private void resizeListWidget() {
    currentConfigList.ifPresent(e -> {
      int x = 10;
      int w = getButtonsWidth();
      int y = 30;
      int lx = x + w + 5;
      int ly = y;
      int lw = this.width - lx - 10;
      int lh = this.height - ly - 10;
      e.setBounds(lx, ly, lw, lh);
    });
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  public int getButtonsWidth() {
    if (userDefinedButtonsWidth > 0) return userDefinedButtonsWidth;
    if (defaultButtonsWidth > 0) return defaultButtonsWidth;
    return calculatedButtonsWidth;
  }

  public void addNavigationButton(String buttonText, Runnable action) {
    navigationButtons.add(Pair.of(buttonText, action));
    buttonsDirty = true;
  }

  public void addNavigationButton(String buttonText, Supplier<? extends AnchoredListWidget<?>> anchoredListWidget) {
    addNavigationButton(buttonText, () -> {
      this.currentConfigList = Optional.ofNullable(anchoredListWidget.get());
      resizeListWidget();
    });
  }

  public void addNavigationButton(String buttonText) {
    addNavigationButton(buttonText, () -> {
      this.currentConfigList = Optional.empty();
    });
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }
  public void setSelectedIndex(int index) {
    if (autoScrollToTop) {
      setScrollY(0);
    }
    if (index < 0 || index >= navigationButtons.size()) {
      index = -1;
    }
    if (selectedIndex != index) {
      selectedIndex = index;
      updateButtonActives();
      if (index != -1) {
        navigationButtons.get(index).getRight().run();
      }
    }
  }

  private void updateButtonActives() {
    for (int i = 0; i < navigationButtonWidgets.size(); i++) {
      navigationButtonWidgets.get(i).active = selectedIndex != i;
    }
  }

  @Override
  public boolean mouseReleased(double d, double e, int i) {
    if (super.mouseReleased(d, e, i)) {
      return true;
    }
    return currentConfigList.map(x -> x.mouseReleased(d, e, i)).orElse(false);
  }

}