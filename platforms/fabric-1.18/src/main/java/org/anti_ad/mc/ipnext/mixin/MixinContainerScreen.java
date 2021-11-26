package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
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
@Mixin(HandledScreen.class)
public abstract class MixinContainerScreen<T extends ScreenHandler> extends Screen {

    protected MixinContainerScreen(Text text) {
        super(text);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
            "drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V", shift = At.Shift.AFTER), method = "render")
    public void onBackgroundRender(MatrixStack matrixStack, int i, int j, float f, CallbackInfo ci) {
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
            "drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V", shift = At.Shift.AFTER), method = "render")
    public void onForegroundRender(MatrixStack matrixStack, int i, int j, float f, CallbackInfo ci) {
        ContainerScreenEventHandler.INSTANCE.onForegroundRender();
    }

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
                    "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"),
            method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
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
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
                    "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"),
            method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V")
    public void afterOnMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(null);
    }
}