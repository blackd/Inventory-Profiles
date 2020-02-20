package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.config.Configs.ModSettings;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.GroupingShapeProviders;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerInfo;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils;
import io.github.jsnimda.inventoryprofiles.sorter.util.Converter;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import io.github.jsnimda.inventoryprofiles.sorter.util.CurrentState;
import io.github.jsnimda.inventoryprofiles.sorter.util.Getter;
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
      (ModSettings.SORT_AT_CURSOR.getBooleanValue() && ContainerUtils.cursorPointingPlayerInventory());
    doSort(sortPlayer, sortingProvider, groupingType);
  }
  public static void doSort(boolean sortPlayer, ISortingMethodProvider sortingProvider, GroupingType groupingType) {
    ContainerInfo info = CurrentState.containerInfo();
    if (groupingType == GroupingType.PRESERVED) {
      doSort(sortPlayer, SortingMethodProviders.SHUFFLE, GroupingShapeProviders.RANDOM);
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
      if (ModSettings.RESTOCK_HOTBAR.getBooleanValue())
        ContainerActions.restockHotbar();
      slots = info.playerStorageSlots;
    }
    else
      slots = info.sortableSlots;
    List<Integer> slotIds = slots.stream().map(x -> Getter.slotId(x)).collect(Collectors.toList());
    doSort(info.container, slots, slotIds, sortingProvider, groupingProvider);
  }

  public static void doSort(Container container, List<Slot> slots, List<Integer> slotIds,
      ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    // boolean targetsFirst = AdvancedOptions.SORT_CLICK_TARGETS_FIRST.getBooleanValue();
    // List<OldClick> clicks = VirtualSorter.doSort(Converter.toVirtualItemStackList(slots), sortingProvider, groupingProvider, targetsFirst);
    List<Click> clicks = VirtualSorter.doSort(Converter.toVirtualItemStackList(slots), sortingProvider, groupingProvider);
    clicks.forEach(x->x.slotId = slotIds.get(x.slotId));
    //doClicks(container, clicks, slotIds);
    int interval = ModSettings.ADD_INTERVAL_BETWEEN_CLICKS.getBooleanValue() ? ModSettings.INTERVAL_BETWEEN_CLICKS_MS.getIntegerValue() : 0;
    ContainerActions.genericClicks(container, clicks, interval);
  }


}