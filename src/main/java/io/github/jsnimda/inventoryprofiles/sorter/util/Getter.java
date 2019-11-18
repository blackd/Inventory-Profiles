package io.github.jsnimda.inventoryprofiles.sorter.util;

import net.minecraft.inventory.container.Slot;

/**
 * Getter
 * <p>
 * Provide a interface to access vanilla object properties. Via the unified
 * methods avoiding fabric/forge naming confusion.
 */
public class Getter {

  public static int slotId(Slot s) {
    return s.slotNumber;
  }

  public static int invSlot(Slot s) {
    return s.getSlotIndex();
  }

}