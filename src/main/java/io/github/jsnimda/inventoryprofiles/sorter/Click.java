package io.github.jsnimda.inventoryprofiles.sorter;

import net.minecraft.inventory.container.ClickType;

public class Click {

  public int slotId;
  public int button;
  public ClickType actionType;

  public Click(int slotId, int button, ClickType actionType) {
    this.slotId = slotId;
    this.button = button;
    this.actionType = actionType;
  }
  public Click(int slotId, int button) {
    this.slotId = slotId;
    this.button = button;
    this.actionType = ClickType.PICKUP;
  }

  public static Click leftClick(int slotId) {
    return new Click(slotId, 0, ClickType.PICKUP);
  }
  public static Click rightClick(int slotId) {
    return new Click(slotId, 1, ClickType.PICKUP);
  }
  public static Click shiftClick(int slotId) {
    return new Click(slotId, 0, ClickType.QUICK_MOVE);
  }
  public static Click dropOne(int slotId) {
    return new Click(slotId, 0, ClickType.THROW);
  }
  public static Click dropAll(int slotId) {
    return new Click(slotId, 1, ClickType.THROW);
  }
  public static Click dropOneCursor() {
    return dropOne(-999);
  }
  public static Click dropAllCursor() {
    return dropAll(-999);
  }

}