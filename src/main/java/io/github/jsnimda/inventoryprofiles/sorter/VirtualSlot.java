package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.screen.slot.Slot;
import io.github.jsnimda.inventoryprofiles.sorter.util.Getter;

/**
 * VirtualSlot
 */
public class VirtualSlot {

  public final Slot slotConditionObject; //the actually ItemStack of this slot in real world is ingored
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

  public VirtualSlot(Slot slotConditionObject) {
    this.slotConditionObject = slotConditionObject;
    slotItem = VirtualItemStack.empty();
  }
  public VirtualSlot(Slot slotConditionObject, VirtualItemStack slotItem) {
    this.slotConditionObject = slotConditionObject;
    this.slotItem = slotItem;
  }
  
  public VirtualSlot copy() {
    return new VirtualSlot(slotConditionObject, slotItem.copy());
  }

  public static void bulkAction(List<VirtualSlot> slots, Function<List<VirtualItemStack>, List<VirtualItemStack>> action) {
    List<VirtualItemStack> res = action.apply(
      slots.stream().map(x -> x.slotItem).collect(Collectors.toList())
    );
    for (int i = 0; i < res.size(); i++) {
      slots.get(i).slotItem = res.get(i);
    }
  }

}