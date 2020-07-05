package io.github.jsnimda.inventoryprofiles.forge;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.jsnimda.common.gui.Tooltips;
import io.github.jsnimda.common.vanilla.Vanilla;
import io.github.jsnimda.common.vanilla.VanillaUtil;
import io.github.jsnimda.inventoryprofiles.config.Tweaks;
import io.github.jsnimda.inventoryprofiles.gui.inject.ContainerScreenHandler;
import io.github.jsnimda.inventoryprofiles.inventory.GeneralInventoryActions;
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
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ForgeEventHandler
 */
public class ForgeEventHandler {

  @SubscribeEvent
  public void onDrawForeground(GuiContainerEvent.DrawForeground e) {
  }

  @SubscribeEvent
  public void onDrawScreenPost(DrawScreenEvent.Post e) { // MixinAbstractContainerScreen.render
//    if (!ToolTips.current.isEmpty()) {
//      GlStateManager.pushMatrix();
//      ToolTips.renderAll();
//      GlStateManager.disableLighting();
//      GlStateManager.popMatrix();
//    }
    if (!Tooltips.INSTANCE.getTooltips().isEmpty()) {
      GlStateManager.pushMatrix();
      Tooltips.INSTANCE.renderAll();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
    }
  }

  @SubscribeEvent
  public void onInitGuiPost(InitGuiEvent.Post e) { // MixinAbstractContainerScreen.init
    if (e.getGui() instanceof ContainerScreen) {
      e.addWidget(ContainerScreenHandler.INSTANCE.getContainerInjector((ContainerScreen) (Object) this));
    }
  }

  /*
  todo tweaks:
   @Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

  @Shadow
  private int field_3716;

  @Inject(at = @At("HEAD"), method = "method_2902(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
  public void method_2902(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
    if (this.field_3716 > 0 && Tweaks.INSTANCE.getDISABLE_BLOCK_BREAKING_COOLDOWN().getBooleanValue()) {
      this.field_3716 = 0;
    }
  }

  @Inject(at = @At(value = "INVOKE_ASSIGN", target="Lnet/minecraft/client/network/ClientPlayerInteractionManager;"
      + "breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"),
      method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
  public void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
    if (Tweaks.INSTANCE.getINSTANT_MINING_COOLDOWN().getBooleanValue()) {
      this.field_3716 = 5;
    }
  }

}


// ============
// MixinGameRenderer
// ============

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
  @Inject(at = @At("HEAD"), method = "bobViewWhenHurt(F)V", cancellable = true)
  public void bobViewWhenHurt(float float_1, CallbackInfo info) {
    if (Tweaks.INSTANCE.getDISABLE_SCREEN_SHAKING_ON_DAMAGE().getBooleanValue()) {
      info.cancel();
    }
  }

  @Inject(at = @At("RETURN"), method = "render")
  public void render(float f, long l, boolean bl, CallbackInfo ci) {
    GameEventHandler.INSTANCE.postScreenRender();
  }
}

@Mixin(GlStateManager.class)
public class MixinGlStateManager {

  @ModifyVariable(method = "fogDensity(F)V", at = @At("HEAD"), argsOnly = true)
  private static float fogDensity(float fogDensity) {
    if (fogDensity == 2.0f && Tweaks.INSTANCE.getDISABLE_LAVA_FOG().getBooleanValue()) {
      return 0.02f;
    }
    return fogDensity;
  }

}

@Mixin(GuiCloseC2SPacket.class)
public class MixinGuiCloseC2SPacket {

  @Inject(method = "<init>(I)V", at = @At("RETURN"))
  private void onConstructed(CallbackInfo ci) {
    if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()) {
      GeneralInventoryActions.INSTANCE.handleCloseContainer();
    }
  }

}
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

  @Shadow
  private int itemUseCooldown;

  @Inject(at = @At("HEAD"), method = "tick()V")
  public void tick(CallbackInfo info) {
    if (this.itemUseCooldown > 0 && Tweaks.INSTANCE.getDISABLE_ITEM_USE_COOLDOWN().getBooleanValue()) {
      this.itemUseCooldown = 0;
    }
  }

  @Inject(at = @At("RETURN"), method = "tick()V")
  public void tick2(CallbackInfo info) {
    GameEventHandler.INSTANCE.onTick();
  }

  @Inject(at = @At("RETURN"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
  public void joinWorld(ClientWorld clientWorld, CallbackInfo info) {
    GameEventHandler.INSTANCE.onJoinWorld();
  }
}

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot {
  @Inject(at = @At("HEAD"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
  public void onCrafted(ItemStack itemStack, CallbackInfo ci) {
    if (VanillaUtil.INSTANCE.isOnClientThread()) {
      GameEventHandler.INSTANCE.onCrafted();
    }
  }
}

@Mixin(Screen.class)
public class MixinScreen {
  @Inject(at= @At("HEAD"), method = "renderTooltip(Ljava/util/List;II)V")
  public void renderTooltip(List<String> list, int i, int j, CallbackInfo ci) {
    GameEventHandler.INSTANCE.preRenderTooltip();
  }

  @Inject(at= @At("HEAD"), method = "render")
  public void render(int i, int j, float f, CallbackInfo ci) {
    GameEventHandler.INSTANCE.preScreenRender();
  }
}

   */
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

  @SubscribeEvent
  public void onTick(ClientTickEvent e) { // Tweaks.DISABLE_BLOCK_BREAKING_COOLDOWN, Tweaks.DISABLE_ITEM_USE_COOLDOWN
    if (!VanillaUtil.INSTANCE.inGame()) return;
    if (e.phase != Phase.START) return;
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
}