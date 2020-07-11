package io.github.jsnimda.inventoryprofiles.mixin;

import io.github.jsnimda.common.vanilla.VanillaUtil;
import io.github.jsnimda.inventoryprofiles.event.ClientEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot {
  @Inject(at = @At("HEAD"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
  public void onCrafted(ItemStack itemStack, CallbackInfo ci) {
    if (VanillaUtil.INSTANCE.isOnClientThread()) {
      ClientEventHandler.INSTANCE.onCrafted();
    }
  }
}
