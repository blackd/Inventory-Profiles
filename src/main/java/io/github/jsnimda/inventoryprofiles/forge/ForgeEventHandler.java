package io.github.jsnimda.inventoryprofiles.forge;

import java.lang.reflect.Field;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import org.apache.commons.lang3.reflect.FieldUtils;

import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import io.github.jsnimda.inventoryprofiles.gui.ToolTips;
import io.github.jsnimda.inventoryprofiles.gui.inject.GuiSortingButtons;
import io.github.jsnimda.inventoryprofiles.sorter.SorterEventPort;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * ForgeEventHandler
 */
public class ForgeEventHandler {

  @SubscribeEvent
  public void onDrawForeground(GuiContainerEvent.DrawForeground e) {
  }

  @SubscribeEvent
  public void onDrawScreenPost(DrawScreenEvent.Post e) { // MixinAbstractContainerScreen.render
    if (!ToolTips.current.isEmpty()) {
      GlStateManager.pushMatrix();
      ToolTips.renderAll();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
    }
  }

  @SubscribeEvent
  public void onInitGuiPost(InitGuiEvent.Post e) { // MixinAbstractContainerScreen.init
    if (e.getGui() instanceof ContainerScreen) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) e.getGui();
      Container container = containerScreen.getContainer();
      int left = (containerScreen.width - containerScreen.getXSize()) / 2;
      int top = (containerScreen.height - containerScreen.getYSize()) / 2;
      int containerWidth = containerScreen.getXSize();
      int containerHeight = containerScreen.getYSize();
      List<Widget> buttons = GuiSortingButtons.gets(containerScreen, container, left, top, containerWidth,
          containerHeight);
      buttons.forEach(x -> e.addWidget(x));
    }
  }

  @SubscribeEvent
  public void onGuiKeyPressedPre(GuiScreenEvent.KeyboardKeyPressedEvent.Pre e) { // Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM
    if (!Current.inGame()) return;
    InputMappings.Input mouseKey = InputMappings.getInputByCode(e.getKeyCode(), e.getScanCode());
    if (Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM.getBooleanValue()
        && (e.getKeyCode() == 256 || Current.MC().gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))) {
      SorterEventPort.handleCloseContainer();
    }
  }

  PlayerController pc = null;

  //blockHitDelay
  Field blockHitDelayField = null; // field_78781_i

  //rightClickDelayTimer
  Field rightClickDelayTimerField = null; // field_71467_ac

  @SubscribeEvent
  public void onTick(ClientTickEvent e) { // Tweaks.DISABLE_BLOCK_BREAKING_COOLDOWN, Tweaks.DISABLE_ITEM_USE_COOLDOWN
    if (!Current.inGame()) return;
    if (e.phase != Phase.START) return;
    if (Tweaks.DISABLE_BLOCK_BREAKING_COOLDOWN.getBooleanValue()) {
      if (pc == null || pc != Current.interactionManager()) {
        pc = Current.interactionManager();
        blockHitDelayField = ObfuscationReflectionHelper.findField(PlayerController.class, "field_78781_i");
      }
      try {
        FieldUtils.writeField(blockHitDelayField, pc, 0, true);
      } catch (IllegalAccessException e2) {
        e2.printStackTrace();
      }
    }
    if (Tweaks.DISABLE_ITEM_USE_COOLDOWN.getBooleanValue()) {
      if (rightClickDelayTimerField == null) {
        rightClickDelayTimerField = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71467_ac");
      }
      try {
        FieldUtils.writeField(rightClickDelayTimerField, Current.MC(), 0, true);
      } catch (IllegalAccessException e2) {
        e2.printStackTrace();
      }
    }
  }
}