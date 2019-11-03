package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;

/**
 * Manipulator
 */
public class Manipulator {

  public final VirtualSlots slots;
  public Manipulator(VirtualSlots slots) {
    this.slots = slots;
  }

  // public List<Click> calcClicks() {
    
  // }

  // public Selection selectAll() {

  // }
  // public Selection select(List<Integer> slotIndexs) {

  // }

  // public class Selection {
  //   public Manipulator apply(Rule rule) {
  //     return Manipulator.this;
  //   }
  // }

  public enum OverflowAction {
    SHRINK,
    DROP,
    FIT_OR_SHRINK,
    FIT_OR_DROP,
  }
  public enum SlotNoMatchAction {
    LEAVE,
    EMPTY,

  }

}