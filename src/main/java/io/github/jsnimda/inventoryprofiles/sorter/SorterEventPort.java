package io.github.jsnimda.inventoryprofiles.sorter;

import io.github.jsnimda.inventoryprofiles.config.Configs.Hotkeys;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort.GroupingType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.gui.screen.Screen;

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
    ContainerActions.moveAllAlike(Screen.hasShiftDown());
  }

  public static boolean handleKey(int lastKey, int lastAction){
    if (!Current.inGame()) return false;
    try {
      if (Hotkeys.SORT_INVENTORY.isActivated()) {
        doSortAction();
        return true;
      } else if (Hotkeys.SORT_INVENTORY_IN_COLUMNS.isActivated()) {
        doSortActionByGroupColumns();
        return true;
      } else if (Hotkeys.SORT_INVENTORY_IN_ROWS.isActivated()) { 
        doSortActionByGroupRows();
        return true;
      } else if (Hotkeys.MOVE_ALL_ITEMS.isActivated()) {
        doMoveAll();
        return true;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    
    return false;
  }

}