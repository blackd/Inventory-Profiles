package io.github.jsnimda.inventoryprofiles.forge;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.inventoryprofiles.gui.ToolTips;
import io.github.jsnimda.inventoryprofiles.gui.inject.GuiSortingButtons;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * ForgeEventHandler
 */
public class ForgeEventHandler {

  @SubscribeEvent
  public void onDrawForeground(GuiContainerEvent.DrawForeground e) {
  }

  @SubscribeEvent
  public void onDrawScreenEventPost(DrawScreenEvent.Post e) { // MixinAbstractContainerScreen.render
    if (!ToolTips.current.isEmpty()) {
      GlStateManager.pushMatrix();
      ToolTips.renderAll();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
    }
  }

  @SubscribeEvent
  public void onInitGuiEventPost(InitGuiEvent.Post e) {
    if (e.getGui() instanceof ContainerScreen) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) e.getGui();
      Container container = containerScreen.getContainer();
      int left = (containerScreen.width - containerScreen.getXSize()) / 2;
      int top = (containerScreen.height - containerScreen.getYSize()) / 2;
      int containerWidth = containerScreen.getXSize();
      int containerHeight = containerScreen.getYSize();
      List<Widget> buttons = GuiSortingButtons.gets(containerScreen, container, left, top, containerWidth, containerHeight);
      buttons.forEach(x -> e.addWidget(x));
    }
  }
}