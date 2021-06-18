package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface IMixinContainerScreen {

  @Accessor("focusedSlot")
  Slot getFocusedSlot();

  @Accessor("x")
  int getContainerX();

  @Accessor("y")
  int getContainerY();

  @Accessor("backgroundWidth")
  int getContainerWidth();

  @Accessor("backgroundHeight")
  int getContainerHeight();

}
