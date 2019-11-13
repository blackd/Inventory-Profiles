package io.github.jsnimda.inventoryprofiles.sorter.util;

import io.github.jsnimda.inventoryprofiles.mixin.IMixinSlot;
import net.minecraft.container.Slot;

/**
 * Getter
 * <p>
 * Provide a interface to access vanilla object properties.
 * Via the unified methods avoiding fabric/forge naming confusion.
 */
public class Getter {

  public static int slotId(Slot s) {
    return s.id;
  }

  public static int invSlot(Slot s) {
    return ((IMixinSlot)s).getInvSlot();
  }

}