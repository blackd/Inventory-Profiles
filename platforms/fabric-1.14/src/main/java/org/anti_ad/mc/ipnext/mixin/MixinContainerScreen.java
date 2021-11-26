package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.text.Text;
import org.anti_ad.mc.common.Log;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(ContainerScreen.class)
public abstract class MixinContainerScreen<T extends Container> extends Screen {

    protected MixinContainerScreen(Text text) {
        super(text);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;" +
            "drawBackground(FII)V", shift = At.Shift.AFTER), method = "render")
    public void onBackgroundRender(int i, int j, float f, CallbackInfo ci) {
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;" +
            "drawForeground(II)V", shift = At.Shift.AFTER), method = "render")
    public void onForegroundRender(int i, int j, float f, CallbackInfo ci) {
        ContainerScreenEventHandler.INSTANCE.onForegroundRender();
    }

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;onMouseClick(Lnet/minecraft/container/Slot;IILnet/minecraft/container/SlotActionType;)V"),
            method = "onMouseClick(Lnet/minecraft/container/Slot;IILnet/minecraft/container/SlotActionType;)V",
            cancellable = true)
    public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        Log.INSTANCE.trace("onMouseClick for " + slotId);
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(slot);
        if (slot != null
                && LockedSlotsSettings.INSTANCE.getLOCK_SLOTS_DISABLE_USER_INTERACTION().getValue()
                && LockSlotsHandler.INSTANCE.isMappedSlotLocked(slot)) {
            Log.INSTANCE.trace("cancel for " + slotId);
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "TAIL",
            target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;onMouseClick(Lnet/minecraft/container/Slot;IILnet/minecraft/container/SlotActionType;)V"),
            method = "onMouseClick(Lnet/minecraft/container/Slot;IILnet/minecraft/container/SlotActionType;)V")
    public void afterOnMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(null);
    }

}