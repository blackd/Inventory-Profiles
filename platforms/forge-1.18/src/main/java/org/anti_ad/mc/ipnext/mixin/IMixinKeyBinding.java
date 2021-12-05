package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface IMixinKeyBinding {

    @Accessor("key")
    InputConstants.Key getKeyCode();

    @Accessor("isDown")
    void setPressed(boolean pressed);

    @Accessor("clickCount")
    int getTimesPressed();

    @Accessor("clickCount")
    void setTimesPressed(int pressed);
}
