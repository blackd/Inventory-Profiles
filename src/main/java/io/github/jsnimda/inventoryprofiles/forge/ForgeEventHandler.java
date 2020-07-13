package io.github.jsnimda.inventoryprofiles.forge;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.vanilla.Vanilla;
import io.github.jsnimda.common.vanilla.VanillaUtil;
import io.github.jsnimda.inventoryprofiles.config.Tweaks;
import io.github.jsnimda.inventoryprofiles.event.ClientEventHandler;
import io.github.jsnimda.inventoryprofiles.gui.inject.ContainerScreenHandler;
import io.github.jsnimda.inventoryprofiles.gui.inject.InjectWidget;
import io.github.jsnimda.inventoryprofiles.inventory.GeneralInventoryActions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ForgeEventHandler
 */
public class ForgeEventHandler {

  @SubscribeEvent
  public void clientClick(ClientTickEvent e) {
    if (e.phase == Phase.START) {
      ClientEventHandler.INSTANCE.onTickPre();
      onTickPre();
    } else { // e.phase == Phase.END
      onTickPost();
    }
  }

  @SubscribeEvent
  public void joinWorld(WorldEvent.Load event) {
    if (VanillaUtil.INSTANCE.isOnClientThread()) {
      ClientEventHandler.INSTANCE.onJoinWorld();
    }
  }

  @SubscribeEvent
  public void onCrafted(PlayerEvent.ItemCraftedEvent event) {
    if (VanillaUtil.INSTANCE.isOnClientThread()) {
      ClientEventHandler.INSTANCE.onCrafted();
    }
  }

  // ============
  // screen render
  // ============

  @SubscribeEvent
  public void onInitGuiPost(InitGuiEvent.Post e) { // MixinAbstractContainerScreen.init
    if (e.getGui() instanceof ContainerScreen) {
      // on forge this is called twice on creative screen lol
      // fix:
      if (Vanilla.INSTANCE.screen() != e.getGui()) return;

      List<InjectWidget> list = ContainerScreenHandler.INSTANCE.getContainerInjector((ContainerScreen) e.getGui());
      for (InjectWidget iw : list) {
        e.addWidget(iw);
      }
    }
  }

  @SubscribeEvent
  public void preScreenRender(GuiScreenEvent.DrawScreenEvent.Pre event) {
    ClientEventHandler.INSTANCE.preScreenRender();
  }

  // fabric GameRenderer.render() = forge updateCameraAndRender()
  // forge line 554
  @SubscribeEvent
  public void postScreenRender(DrawScreenEvent.Post e) {
    ClientEventHandler.INSTANCE.postScreenRender();
  }

  public void onBackgroundRender(GuiContainerEvent.DrawBackground e) {}

  @SubscribeEvent
  public void onForegroundRender(GuiContainerEvent.DrawForeground e) {
  }

  public void onTickPost() {
    ClientEventHandler.INSTANCE.onTick();
  }

  @SubscribeEvent
  public void preRenderTooltip(RenderTooltipEvent.Pre event) {
    ClientEventHandler.INSTANCE.preRenderTooltip();
  }

  // ============
  // old event
  // ============

  @SubscribeEvent
  public void onGuiKeyPressedPre(GuiScreenEvent.KeyboardKeyPressedEvent.Pre e) { // Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM
    if (!VanillaUtil.INSTANCE.inGame()) return;
    InputMappings.Input mouseKey = InputMappings.getInputByCode(e.getKeyCode(), e.getScanCode());
    if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()
        && (e.getKeyCode() == 256 || Vanilla.INSTANCE.mc().gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))) {
      GeneralInventoryActions.INSTANCE.handleCloseContainer();
    }
  }

  PlayerController pc = null;

  //blockHitDelay
  Field blockHitDelayField = null; // field_78781_i

  //rightClickDelayTimer
  Field rightClickDelayTimerField = null; // field_71467_ac

  public void onTickPre() { // Tweaks.DISABLE_BLOCK_BREAKING_COOLDOWN, Tweaks.DISABLE_ITEM_USE_COOLDOWN
    if (!VanillaUtil.INSTANCE.inGame()) return;
    if (Tweaks.INSTANCE.getDISABLE_BLOCK_BREAKING_COOLDOWN().getBooleanValue()) {
      if (pc == null || pc != Vanilla.INSTANCE.interactionManager()) {
        pc = Vanilla.INSTANCE.interactionManager();
        blockHitDelayField = ObfuscationReflectionHelper.findField(PlayerController.class, "field_78781_i");
      }
      try {
        FieldUtils.writeField(blockHitDelayField, pc, 0, true);
      } catch (IllegalAccessException e2) {
        e2.printStackTrace();
      }
    }
    if (Tweaks.INSTANCE.getDISABLE_ITEM_USE_COOLDOWN().getBooleanValue()) {
      if (rightClickDelayTimerField == null) {
        rightClickDelayTimerField = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71467_ac");
      }
      try {
        FieldUtils.writeField(rightClickDelayTimerField, Vanilla.INSTANCE.mc(), 0, true);
      } catch (IllegalAccessException e2) {
        e2.printStackTrace();
      }
    }
  }


  /*
  todo tweaks:
   @Mixin(ClientPlayerInteractionManager.class)
// INSTANT_MINING_COOLDOWN
//  @Inject(at = @At(value = "INVOKE_ASSIGN", target="Lnet/minecraft/client/network/ClientPlayerInteractionManager;"
//      + "breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"),
//      method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
//  public void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
//    if (Tweaks.INSTANCE.getINSTANT_MINING_COOLDOWN().getBooleanValue()) {
//      this.field_3716 = 5;
//    }
//  }

// ============
// MixinGameRenderer
// ============

// DISABLE_SCREEN_SHAKING_ON_DAMAGE
//@Mixin(GameRenderer.class)
//public class MixinGameRenderer {
//  @Inject(at = @At("HEAD"), method = "bobViewWhenHurt(F)V", cancellable = true)
//  public void bobViewWhenHurt(float float_1, CallbackInfo info) {
//    if (Tweaks.INSTANCE.getDISABLE_SCREEN_SHAKING_ON_DAMAGE().getBooleanValue()) {
//      info.cancel();
//    }
//  }

// DISABLE_LAVA_FOG
//@Mixin(GlStateManager.class)
//public class MixinGlStateManager {
//
//  @ModifyVariable(method = "fogDensity(F)V", at = @At("HEAD"), argsOnly = true)
//  private static float fogDensity(float fogDensity) {
//    if (fogDensity == 2.0f && Tweaks.INSTANCE.getDISABLE_LAVA_FOG().getBooleanValue()) {
//      return 0.02f;
//    }
//    return fogDensity;
//  }
//
//}
   */

}