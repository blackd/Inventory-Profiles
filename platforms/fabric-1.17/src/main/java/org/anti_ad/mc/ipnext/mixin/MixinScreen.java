package org.anti_ad.mc.ipnext.mixin;


import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    private final List<Element> children = new ArrayList<>();
    ;
    @Shadow
    private final List<Selectable> selectables = new ArrayList<>();
    ;

    @Inject(at = @At("RETURN"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
    public void init(MinecraftClient minecraftClient, int i, int j, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        ScreenEventHandler.INSTANCE.onScreenInit(self, x -> {
            addSelectableChild(x);
            return Unit.INSTANCE;
        });
    }

    public <T extends Element & Selectable> T addSelectableChild(T child) {
        this.children.add(child);
        this.selectables.add((Selectable) child);
        return child;
    }
}
