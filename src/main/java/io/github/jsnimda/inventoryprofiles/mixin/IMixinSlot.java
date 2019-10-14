package io.github.jsnimda.inventoryprofiles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.container.Slot;

/**
 * IMixinSlot
 */
@Mixin(Slot.class)
public interface IMixinSlot {

  @Accessor("invSlot")
  int getInvSlot();

}
