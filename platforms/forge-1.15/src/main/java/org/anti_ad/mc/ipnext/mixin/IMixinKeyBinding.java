package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface IMixinKeyBinding {

    @Accessor("keyCode")
    InputMappings.Input getKeyCode();

    @Accessor("pressed")
    void setPressed(boolean pressed);

    @Accessor("pressTime")
    int getTimesPressed();

    @Accessor("pressTime")
    void setTimesPressed(int pressed);
}
