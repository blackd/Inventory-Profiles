package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.anti_ad.mc.common.Log;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot {
    @Inject(at = @At("TAIL"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
    public void onCrafted(ItemStack itemStack, CallbackInfo ci) {
        //ClientEventHandler.INSTANCE.onCrafted();
        //Log.INSTANCE.debug("MixinCraftingResultSlot::onCrafted");
    }
}
