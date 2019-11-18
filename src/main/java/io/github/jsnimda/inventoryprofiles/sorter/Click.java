package io.github.jsnimda.inventoryprofiles.sorter;

import net.minecraft.container.SlotActionType;

public class Click {

  public int slotId;
  public int button;
  public SlotActionType actionType;

  public Click(int slotId, int button, SlotActionType actionType) {
    this.slotId = slotId;
    this.button = button;
    this.actionType = actionType;
  }
  public Click(int slotId, int button) {
    this.slotId = slotId;
    this.button = button;
    this.actionType = SlotActionType.PICKUP;
  }

  public static Click leftClick(int slotId) {
    return new Click(slotId, 0, SlotActionType.PICKUP);
  }
  public static Click rightClick(int slotId) {
    return new Click(slotId, 1, SlotActionType.PICKUP);
  }
  public static Click shiftClick(int slotId) {
    return new Click(slotId, 0, SlotActionType.QUICK_MOVE);
  }
  public static Click dropOne(int slotId) {
    return new Click(slotId, 0, SlotActionType.THROW);
  }
  public static Click dropAll(int slotId) {
    return new Click(slotId, 1, SlotActionType.THROW);
  }
  public static Click dropOneCursor() {
    return dropOne(-999);
  }
  public static Click dropAllCursor() {
    return dropAll(-999);
  }

}