package io.github.jsnimda.inventoryprofiles.sorter;

import javax.annotation.Nullable;

import io.github.jsnimda.inventoryprofiles.sorter.util.Get;
import net.minecraft.container.Slot;

/**
 * VirtualSlot
 */
public class VirtualSlot {

  private final Slot slotConditionObject; //the actually ItemStack of this slot in real world is ingored
  @Nullable
  public VirtualItemStack slotItem;

  public int getSlotId() {
    return Get.slotId(slotConditionObject);
  }

  public int getInvSlot() {
    return Get.invSlot(slotConditionObject);
  }

  public boolean canInsert(VirtualItemStack item) { // aka isItemValid
    return slotConditionObject.canInsert(item.itemType.getDummyItemStack());
  }

  public boolean isEmpty() {
    return slotItem == null;
  }

  private VirtualSlot(Slot slotConditionObject) {
    this.slotConditionObject = slotConditionObject;
  }


}