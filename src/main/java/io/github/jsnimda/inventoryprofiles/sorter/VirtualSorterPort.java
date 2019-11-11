package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.Log;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.GroupingShapeProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerInfo;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils;
import io.github.jsnimda.inventoryprofiles.sorter.util.Converter;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import io.github.jsnimda.inventoryprofiles.sorter.util.CurrentState;
import io.github.jsnimda.inventoryprofiles.sorter.util.Get;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;

/**
 * VirtualSorterPort
 */
public class VirtualSorterPort {

  public enum GroupingType {
    PRESERVED,
    COLUMNS,
    ROWS
  }
  public static void doSort(ISortingMethodProvider sortingProvider, GroupingType groupingType) {
    ContainerInfo info = CurrentState.containerInfo();
    boolean sortPlayer = info.sortableSlots.isEmpty() ||
      (AdvancedOptions.SORT_CURSOR_POINTING.getBooleanValue() && ContainerUtils.cursorPointingPlayerInventory());
    doSort(sortPlayer, sortingProvider, groupingType);
  }
  public static void doSort(boolean sortPlayer, ISortingMethodProvider sortingProvider, GroupingType groupingType) {
    ContainerInfo info = CurrentState.containerInfo();
    if (groupingType == GroupingType.PRESERVED) {
      doSort(sortPlayer, sortingProvider, GroupingShapeProviders.RANDOM);
    } else if (groupingType == GroupingType.COLUMNS) {
      doSort(sortPlayer, sortingProvider, GroupingShapeProviders.columnsProvider(info.sortableWidth));
    } else if (groupingType == GroupingType.ROWS) {
      doSort(sortPlayer, sortingProvider, GroupingShapeProviders.rowsProvider(info.sortableWidth));
    }
  }
  public static void doSort(boolean sortPlayer, ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    if (Current.screen() != null && !(Current.screen() instanceof AbstractContainerScreen)) return;

    ContainerInfo info = CurrentState.containerInfo();
    ContainerActions.cleanCursor();
    List<Slot> slots;
    if (sortPlayer) {
      if (AdvancedOptions.SORT_RESTOCK_HOTBAR.getBooleanValue())
        ContainerActions.restockHotbar();
      slots = info.playerStorageSlots;
    }
    else
      slots = info.sortableSlots;
    List<Integer> slotIds = slots.stream().map(x -> Get.slotId(x)).collect(Collectors.toList());
    doSort(info.container, slots, slotIds, sortingProvider, groupingProvider);
  }

  public static void doSort(Container container, List<Slot> slots, List<Integer> slotIds,
      ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    // boolean targetsFirst = AdvancedOptions.SORT_CLICK_TARGETS_FIRST.getBooleanValue();
    // List<OldClick> clicks = VirtualSorter.doSort(Converter.toVirtualItemStackList(slots), sortingProvider, groupingProvider, targetsFirst);
    List<Click> clicks = VirtualSorter.doSort(Converter.toVirtualItemStackList(slots), sortingProvider, groupingProvider);
    clicks.forEach(x->x.slotId = slotIds.get(x.slotId));
    //doClicks(container, clicks, slotIds);
    int interval = AdvancedOptions.ADD_INTERVAL_BETWEEN_CLICKS.getBooleanValue() ? AdvancedOptions.INTERVAL_BETWEEN_CLICKS_MS.getIntegerValue() : 0;
    ContainerActions.genericClicks(container, clicks, interval);
  }

  public static void doClicks(Container container, List<OldClick> clicks, List<Integer> slotIds) {
    if (AdvancedOptions.ADD_INTERVAL_BETWEEN_CLICKS.getBooleanValue()) {
      Timer timer = new Timer();
      int interval = Math.max(1, AdvancedOptions.INTERVAL_BETWEEN_CLICKS_MS.getIntegerValue());
      timer.scheduleAtFixedRate(new TimerTask(){
        int i = 0;
        int lclick = 0;
        int rclick = 0;
        @Override
        public void run() {
          if (i >= clicks.size()) {
            logClicks(clicks.size(), lclick, rclick);
            timer.cancel();
            return;
          }
          OldClick c = clicks.get(i);
          ContainerActions.click(container, slotIds.get(c.index), c.button);
          lclick += c.button == 0 ? 1 : 0;
          rclick += c.button == 1 ? 1 : 0;
          i++;
        }
      }, 0, interval);
    } else {
      int lclick = 0;
      int rclick = 0;
      for (OldClick c : clicks) {
        ContainerActions.click(container, slotIds.get(c.index), c.button);
        lclick += c.button == 0 ? 1 : 0;
        rclick += c.button == 1 ? 1 : 0;
      }
      logClicks(clicks.size(), lclick, rclick);
    }
  }

  private static void logClicks(int total, int lclick, int rclick) {
    if (AdvancedOptions.DEBUG_LOGS.getBooleanValue()) {
      Log.info(String.format("[inventoryfiles] Click count total %d. %d left. %d right.", total, lclick, rclick));
    }
  }

}