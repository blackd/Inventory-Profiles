package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.Click;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.GroupingShapeProviders;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * VirtualSorterPort
 */
public class VirtualSorterPort {

  public static void doSort(Container container, List<Slot> slots, List<Integer> slotIds) {
    doSort(container, slots, slotIds, SortingMethodProviders.ITEM_ID, GroupingShapeProviders.PRESERVED);
  }

  public static void doSort(Container container, List<Slot> slots, List<Integer> slotIds,
      ISortingMethodProvider sordingProvider, IGroupingShapeProvider groupingProvider) {
    boolean targetsFirst = AdvancedOptions.SORT_CLICK_TARGETS_FIRST.getBooleanValue();
    List<Click> clicks = VirtualSorter.doSort(convertSlotList(slots), sordingProvider, groupingProvider, targetsFirst);
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

  public static List<VirtualItemStack> convertSlotList(List<Slot> slots) {
    List<VirtualItemStack> res = new ArrayList<>();
    for (Slot s : slots) {
      if (s.hasStack()) {
        res.add(convertToVirtual(s.getStack()));
      } else {
        res.add(null);
      }
    }
    return res;
  }

  public static VirtualItemStack convertToVirtual(ItemStack item) {
    return new VirtualItemStack(getType(item), item.getCount());
  }

  public static VirtualItemType getType(ItemStack itemStack) {
    return new VirtualItemType(itemStack.getItem(), itemStack.getTag());
  }

  public static boolean isTypeOf(VirtualItemType type, ItemStack itemStack) {
    // refer: PlayerInventory.areItemsEqual
    return VirtualSorter.areItemTypesEqual(type.item, type.tag, itemStack.getItem(), itemStack.getTag());
  }
}