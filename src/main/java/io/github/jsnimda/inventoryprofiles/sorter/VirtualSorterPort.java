package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.Click;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.GroupingShapeProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerCategory;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerInfo;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

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
    ContainerInfo info = ContainerInfo.of(Current.container());
    if (groupingType == GroupingType.PRESERVED) {
      doSort(info, sortingProvider, GroupingShapeProviders.PRESERVED);
    } else if (groupingType == GroupingType.COLUMNS) {
      doSort(info, sortingProvider, GroupingShapeProviders.columnsProvider(info.sortableWidth));
    } else if (groupingType == GroupingType.ROWS) {
      doSort(info, sortingProvider, GroupingShapeProviders.rowsProvider(info.sortableWidth));
    }
  }
  public static void doSort(ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    doSort(ContainerInfo.of(Current.container()), sortingProvider, groupingProvider);
  }
  public static void doSort(ContainerInfo info, ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    if (Current.screen() != null && !(Current.screen() instanceof AbstractContainerScreen)) return;
    //if (info.category == ContainerCategory.PLAYER_CREATIVE) return;

    ContainerActions.cleanCursor();
    boolean sortPlayer = info.category == ContainerCategory.PLAYER_SURVIVAL || info.sortableSlots.isEmpty() ||
      (AdvancedOptions.SORT_CURSOR_POINTING.getBooleanValue() && ContainerUtils.cursorPointingPlayerInventory());
    if (info.category == ContainerCategory.PLAYER_CREATIVE) sortPlayer = true;
    List<Slot> slots;
    if (sortPlayer)
      slots = info.playerStorageSlots;
    else
      slots = info.sortableSlots;
    List<Integer> slotIds = slots.stream().map(x -> x.id).collect(Collectors.toList());
    doSort(info.container, slots, slotIds, sortingProvider, groupingProvider);
  }

  public static void doSort(Container container, List<Slot> slots, List<Integer> slotIds,
      ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    boolean targetsFirst = AdvancedOptions.SORT_CLICK_TARGETS_FIRST.getBooleanValue();
    List<Click> clicks = VirtualSorter.doSort(getListOfVirtualItemStackFrom(slots), sortingProvider, groupingProvider, targetsFirst);
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
          Click c = clicks.get(i);
          ContainerActions.click(container, slotIds.get(c.index), c.button);
          lclick += c.button == 0 ? 1 : 0;
          rclick += c.button == 1 ? 1 : 0;
          i++;
        }
      }, 0, interval);
    } else {
      int lclick = 0;
      int rclick = 0;
      for (Click c : clicks) {
        ContainerActions.click(container, slotIds.get(c.index), c.button);
        lclick += c.button == 0 ? 1 : 0;
        rclick += c.button == 1 ? 1 : 0;
      }
      logClicks(clicks.size(), lclick, rclick);
    }

  }
  private static void logClicks(int total, int lclick, int rclick) {
    if (AdvancedOptions.DEBUG_LOGS.getBooleanValue()) {
      System.out.println(String.format("[inventoryfiles] Click count total %d. %d left. %d right.", total, lclick, rclick));
    }
  }

  public static List<VirtualItemStack> getListOfVirtualItemStackFrom(List<Slot> slots) {
    List<VirtualItemStack> res = new ArrayList<>();
    for (Slot s : slots) {
      if (s.hasStack()) {
        res.add(getVirtualItemStackFrom(s.getStack()));
      } else {
        res.add(null);
      }
    }
    return res;
  }
  public static VirtualItemStack getVirtualItemStackFrom(ItemStack item) {
    return new VirtualItemStack(getVirtualItemTypeFrom(item), item.getCount());
  }
  public static VirtualItemType getVirtualItemTypeFrom(ItemStack itemStack) {
    return new VirtualItemType(itemStack.getItem(), itemStack.getTag());
  }

}