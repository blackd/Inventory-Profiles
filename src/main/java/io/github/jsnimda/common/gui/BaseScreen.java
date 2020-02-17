package io.github.jsnimda.common.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.controls.ControlsListWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BaseScreen extends Screen {

  public BaseScreen() {
    super(new LiteralText(""));
    // TODO Auto-generated constructor stub
  }

  int i = 0;
  private EntryListWidget keyBindingListWidget;
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);

    this.keyBindingListWidget.render(mouseX, mouseY, partialTicks);
    TextRenderer textRenderer = Current.MC().textRenderer;
    this.drawCenteredString(textRenderer, "w: " + width + " h: " + height, 50, 50, 0xffffffff);
    this.drawCenteredString(textRenderer, String.format("x: %s y: %s p: %s", mouseX, mouseY, partialTicks), 50, 100, 0x80ffffff);

    GlStateManager.pushMatrix();
    GlStateManager.translatef(0, 0, -400.0F);
    GlStateManager.enableDepthTest();
    GlStateManager.depthFunc(518);
    GlStateManager.disableAlphaTest();
    fill(50, 50, 70, 210, 0x70ffffff);
    GlStateManager.enableAlphaTest();
    GlStateManager.depthFunc(515);
    this.drawCenteredString(textRenderer, "i am the best of the world aaa", 50, 200, 0xffffffff);
    GlStateManager.disableDepthTest();
    GlStateManager.popMatrix();

    GlStateManager.clearDepth(1.0d);

    GlStateManager.pushMatrix();
    GlStateManager.translatef(0, 0, -400.0F);
    GlStateManager.enableDepthTest();
    GlStateManager.depthFunc(518);
    GlStateManager.depthFunc(515);

    this.drawCenteredString(textRenderer, "i am the best of the world aaa", 50, 180, 0xffffffff);
    
    GlStateManager.disableDepthTest();
    GlStateManager.popMatrix();
    this.drawString(textRenderer, "§nconfig§r > §l§nmod settings§r", 50, 210, 0xffffffff);
    this.drawString(textRenderer, "Inventory Profiles Config Menu", 50, 230, 0xffffffff);

  }

  @Override
  protected void init() {
    this.addButton(new AbstractButtonWidget(50, this.height - 50, 300, 30, "gui.narrate.editBox " + i++) {

    });
    this.keyBindingListWidget = new EntryListWidget(Current.MC(), 500, 200, 1, 200-1, 30) {
      {
        for (int i = 0; i < 100; i++){
          addEntry(new EntryListWidget.Entry() {
            @Override
            public void render(int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
              fill(x, y, x + rowWidth, y + rowHeight, isSelected ? 0x770000ff : 0x77ff0000);
            }
          });
        }
      }
    };
    this.children.add(this.keyBindingListWidget);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
  
}