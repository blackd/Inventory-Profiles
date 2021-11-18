package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface IMixinKeyBinding {

    @Accessor("keyCode")
    InputUtil.KeyCode getKeyCode();

    @Accessor("pressed")
    void setPressed(boolean pressed);

    @Accessor
    void setTimesPressed(int pressed);
}
