package io.github.jsnimda.inventoryprofiles.sorter;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort.GroupingType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;

/**
 * SorterEventPort
 *  - Main entry to the core sorting code
 */
public class SorterEventPort {

  public static void handleCloseContainer(){
    ContainerActions.cleanCursor();
    ContainerActions.cleanTempSlotsForClosing();
  }

  public static void doSortAction() {
    VirtualSorterPort.doSort(SortingMethodProviders.current(), GroupingType.PRESERVED);
  }
  public static void doSortActionByGroupColumns() {
    VirtualSorterPort.doSort(SortingMethodProviders.current(), GroupingType.COLUMNS);
  }
  public static void doSortActionByGroupRows() {
    VirtualSorterPort.doSort(SortingMethodProviders.current(), GroupingType.ROWS);
  }
  public static void doSwitchProfile() {

  }
  public static void doMoveAll() {
    ContainerActions.moveAllAlike(GuiBase.isShiftDown());
  }

  public static boolean shouldHandle(IKeybind key){
    return ImmutableList.of(
      Generic.SORT_INVENTORY.getKeybind(),
      Generic.SORT_INVENTORY_BY_GROUP_COLUMNS.getKeybind(),
      Generic.SORT_INVENTORY_BY_GROUP_ROWS.getKeybind(),
      Generic.SWITCH_PROFILE.getKeybind(),
      Generic.MOVE_ALL_CONTAINER_EXISTING_ITEMS.getKeybind()
    ).contains(key);
  }
  public static boolean handleKey(KeyAction action, IKeybind key){
    if (!Current.inGame()) return false;
    try {
      if (key == Generic.SORT_INVENTORY.getKeybind()) {
        doSortAction();
        return true;
      } else if (key == Generic.SORT_INVENTORY_BY_GROUP_COLUMNS.getKeybind()) {
        doSortActionByGroupColumns();
        return true;
      } else if (key == Generic.SORT_INVENTORY_BY_GROUP_ROWS.getKeybind()) { 
        doSortActionByGroupRows();
        return true;
      } else if (key == Generic.SWITCH_PROFILE.getKeybind()) {
        doSwitchProfile();
        return true;
      } else if (key == Generic.MOVE_ALL_CONTAINER_EXISTING_ITEMS.getKeybind()) {
        doMoveAll();
        return true;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    
    return false;
  }

}