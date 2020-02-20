package io.github.jsnimda.inventoryprofiles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import io.github.jsnimda.inventoryprofiles.sorter.SorterEventPort;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;

/**
 * MixinGuiCloseC2SPacket
 */
@Mixin(GuiCloseC2SPacket.class)
public class MixinGuiCloseC2SPacket {

  @Inject(method = "<init>(I)V", at = @At("RETURN"))
  private void onConstructed(CallbackInfo ci) {
    if (Tweaks.PREVENT_CLOSE_GUI_DROP_ITEM.getBooleanValue()) {
      SorterEventPort.handleCloseContainer();
    }
  }

}