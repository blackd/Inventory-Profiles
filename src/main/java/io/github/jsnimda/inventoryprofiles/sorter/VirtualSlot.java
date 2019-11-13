package io.github.jsnimda.inventoryprofiles.sorter;

import javax.annotation.Nullable;

import io.github.jsnimda.inventoryprofiles.sorter.util.Getter;
import net.minecraft.container.Slot;

/**
 * VirtualSlot
 */
public class VirtualSlot {

  private final Slot slotConditionObject; //the actually ItemStack of this slot in real world is ingored
  public VirtualItemStack slotItem;

  public int getSlotId() {
    return Getter.slotId(slotConditionObject);
  }

  public int getInvSlot() {
    return Getter.invSlot(slotConditionObject);
  }

  public boolean canInsert(VirtualItemStack item) { // aka isItemValid
    return slotConditionObject.canInsert(item.itemType.getDummyItemStack());
  }

  public boolean isEmpty() {
    return slotItem.isEmpty();
  }

  private VirtualSlot(Slot slotConditionObject) {
    this.slotConditionObject = slotConditionObject;
  }


}