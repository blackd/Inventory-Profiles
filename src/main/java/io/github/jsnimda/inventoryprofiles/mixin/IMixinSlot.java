package io.github.jsnimda.inventoryprofiles.mixin;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * IMixinSlot
 */
@Mixin(Slot.class)
public interface IMixinSlot {

  @Accessor("invSlot")
  int getInvSlot();

}
