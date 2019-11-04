package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.Click;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlots;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlots.ItemTypeInfo;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.GroupingShapeProviders;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * ContainerActions
 */
public class ContainerActions {
  
  public static void cleanCursor() {
    cleanCursor(true, true);
  }
  public static void cleanCursor(boolean putToContainer, boolean putToHotbar) {
    if (Current.cursorStack().isEmpty()) return;
    /**
     * refer: PlayerInventory.offerOrDrop, getOccupiedSlotWithRoomForStack
     * vanilla getOccupiedSlotWithRoomForStack logic:
     *    find alike: mainhand, offhand, hotbar, storage
     *      -> empty (in order of invSlot)
     * my logic
     * hovering slot -> if not:
     * find alike: mainhand, offhand, hotbar, storage
     *  -> empty: storage, hotbar, offhand
     *  -> if container is storage -> container alike -> container empty
     */
    Slot focuesdSlot = Current.focusedSlot();
    ItemStack cursorStack = Current.cursorStack();
    if (ContainerUtils.getRemainingRoom(focuesdSlot, cursorStack) > 0) {
      leftClick(focuesdSlot.slotNumber);
    }
    for (CleanCursorCandidateSlot cccs : CleanCursorCandidateSlot.gets(putToContainer, putToHotbar)) {
      if (Current.cursorStack().isEmpty()) return;
      if (cccs.suit(cursorStack)) {
        leftClick(cccs.slot.slotNumber);
      }
    }
  }
  public static void quickMoveByPlayerStorageSlotsFirst(int fromSlotId, boolean putToHotbar) { // fixing for when player inventory is full
    Slot slot = Current.container().inventorySlots.get(fromSlotId);
    if (slot.getStack().isEmpty()) return;
    boolean pickedUpFromSlot = false;
    for (CleanCursorCandidateSlot cccs : CleanCursorCandidateSlot.gets(false, putToHotbar)) {
      if (pickedUpFromSlot && Current.cursorStack().isEmpty()) return;
      if (cccs.suit(pickedUpFromSlot ? Current.cursorStack() : slot.getStack())) {
        if (!pickedUpFromSlot) {
          pickedUpFromSlot = true;
          leftClick(fromSlotId);
        }
        leftClick(cccs.slot.slotNumber);
      }
    }
    if (!Current.cursorStack().isEmpty()) {
      leftClick(fromSlotId); // put it back
    }
  }
  private static class CleanCursorCandidateSlot {
    public Slot slot;
    public boolean skipIfNoStack;
    
    public static List<CleanCursorCandidateSlot> gets(boolean putToContainer, boolean putToHotbar) {
      List<CleanCursorCandidateSlot> list = new ArrayList<>();
      ContainerInfo info = CurrentState.containerInfo();
      /**
       * player alike
       */
      if (putToHotbar)
        list.add(alike(info.playerMainhandSlot));
      if (info.playerOffhandSlot != null)
        list.add(alike(info.playerOffhandSlot));
      if (putToHotbar)
        info.playerHotbarSlots.stream().filter(x->x!=info.playerMainhandSlot)
          .forEach(x->list.add(alike(x)));
      info.playerStorageSlots.forEach(x->list.add(alike(x)));
      /**
       * player empty
       */
      info.playerStorageSlots.forEach(x->list.add(empty(x)));
      if (putToHotbar)
        info.playerHotbarSlots.forEach(x->list.add(empty(x)));
      if (info.playerOffhandSlot != null)
        list.add(empty(info.playerOffhandSlot));
      /**
       * player armor, if able to
       */
      info.playerArmorSlots.forEach(x->list.add(empty(x)));
      /**
       * container
       */
      if (putToContainer) {
        info.storageSlots.forEach(x->list.add(alike(x)));
        info.storageSlots.forEach(x->list.add(empty(x)));
      }
      return list;
    }
    public boolean suit(ItemStack forItem) {
      if (skipIfNoStack && !slot.getHasStack()) return false;
      return ContainerUtils.getRemainingRoom(slot, forItem) > 0;
    }

    public CleanCursorCandidateSlot(Slot slot, boolean skipIfNoStack) {
      this.slot = slot;
      this.skipIfNoStack = skipIfNoStack;
    }
    public static CleanCursorCandidateSlot alike(Slot slot) {
      return new CleanCursorCandidateSlot(slot, true);
    }
    public static CleanCursorCandidateSlot empty(Slot slot) {
      return new CleanCursorCandidateSlot(slot, false);
    }
  }

  public static void cleanTempSlotsForClosing() {
    // in vanilla, seems only beacon will drop the item, handle beacon only
    //   - clicking cancel button in beacon will bypass
    //     ClientPlayerEntity.closeContainer (by GuiCloseC2SPacket instead)
    if (ContainerCategory.of(Current.container()).isStorage()) {
      return;
    }
    if (!(Current.container() instanceof BeaconContainer)) return;
    if (Current.container().getSlot(0).getHasStack()) { // beacon item
      shiftClick(Current.container(), 0);
    }
  }

  public static void restock(List<Slot> target, List<Slot> from) {
    target
    .forEach(x->{
      if (x != null && x.getHasStack() && ContainerUtils.getRemainingRoom(x, x.getStack()) > 0) {
        for (Slot s : from) {
          if (s.getHasStack() && ContainerUtils.getRemainingRoom(x, s.getStack()) > 0) {
            leftClick(s.slotNumber);
            leftClick(x.slotNumber);
          }
          if (ContainerUtils.getRemainingRoom(x, x.getStack()) <= 0) {
            if (!Current.cursorStack().isEmpty()) {
              leftClick(s.slotNumber);
            }
            break;
          }
        }
      }
    });
  }

  public static void restockHotbar() {
    ContainerInfo info = CurrentState.containerInfo();
    restock(Stream.concat(
      Stream.of(info.playerMainhandSlot, info.playerOffhandSlot),
      info.playerHotbarSlots.stream().filter(x->x!=info.playerMainhandSlot)
    ).collect(Collectors.toList()), info.playerStorageSlots);
  }

  public static void evenlyDistributeCraftingSlots(boolean includeHotbar) {
    ContainerInfo info = CurrentState.containerInfo();
    List<Slot> fromSlots = new ArrayList<>();
    fromSlots.addAll(info.playerStorageSlots);
    if (includeHotbar)
      fromSlots.addAll(info.playerHotbarSlots);
    restock(info.craftingSlots, fromSlots);
    VirtualSlots vs = new VirtualSlots(Converter.toVirtualItemStackList(info.craftingSlots));
    List<VirtualItemStack> a = vs.uniquified;
    Map<VirtualItemType, ItemTypeInfo> infos = vs.getInfos();
    Map<VirtualItemType, Queue<Integer>> bMap = infos.entrySet().stream().collect(Collectors.toMap(
      x->x.getKey(), 
      x->new LinkedList<>(GroupingShapeProviders.columns_widths(x.getValue().totalCount, x.getValue().fromIndex.size()))
    ));
    try {
      List<VirtualItemStack> b = a.stream().map(x -> x == null ? null : x.copy(bMap.get(x.itemtype).remove())).collect(Collectors.toList());
      List<Click> clicks = VirtualSorter.diff(a, b);
      VirtualSorterPort.doClicks(info.container, clicks, info.craftingSlots.stream().map(
        x->x.slotNumber
      ).collect(Collectors.toList()));
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static void moveAllAlike(boolean includeHotbar) {
    moveAllAlike(AdvancedOptions.SORT_CURSOR_POINTING.getBooleanValue() && ContainerUtils.cursorPointingPlayerInventory(), includeHotbar);
  }
  public static void moveAllAlike(boolean moveToPlayerInventory, boolean includeHotbar) {
    cleanCursor();
    ContainerInfo info = CurrentState.containerInfo();
    if (info.category == ContainerCategory.CRAFTABLE_3x3
        || info.category == ContainerCategory.PLAYER_SURVIVAL) {
      evenlyDistributeCraftingSlots(includeHotbar);
      return;
    }
    if (info.storageSlots.isEmpty()) return;
    List<VirtualItemType> types;
    List<Slot> checkSlots = new ArrayList<>();
    if (!moveToPlayerInventory) { // player to chest
      types = new VirtualSlots(Converter.toVirtualItemStackList(info.storageSlots)).getItemTypes();
      checkSlots.addAll(info.playerStorageSlots);
      if (includeHotbar)
        checkSlots.addAll(info.playerHotbarSlots);
    } else { // chest to player
      checkSlots.addAll(info.storageSlots);
      List<Slot> typeSlots = new ArrayList<>();
      typeSlots.addAll(info.playerStorageSlots);
      if (includeHotbar)
        typeSlots.addAll(info.playerHotbarSlots);
      types = new VirtualSlots(Converter.toVirtualItemStackList(typeSlots)).getItemTypes();
      if (!Current.cursorStack().isEmpty()) {
        cleanCursor(); // as moving to player inventory depends on clicks
                           // for playerStorageSlots first purpose
      }
    }
    for (Slot s : checkSlots) {
      if (s.getHasStack()) {
        for (VirtualItemType t : types) {
          if (Converter.toVirtualItemType(s.getStack()).equals(t)) {
            if (!moveToPlayerInventory) {
              shiftClick(Current.container(), s.slotNumber);
            } else {
              quickMoveByPlayerStorageSlotsFirst(s.slotNumber, includeHotbar);
            }
            break;
          }
        }
      }
    }
  }

  public static void leftClick(int slotId) {
    leftClick(Current.container(), slotId);
  }
  public static void rightClick(int slotId) {
    rightClick(Current.container(), slotId);
  }
  public static void leftClick(Container container, int slotId) {
    click(container, slotId, 0);
  }
  public static void rightClick(Container container, int slotId) {
    click(container, slotId, 1);
  }
  // public static void middleClick(Container container, int slotId) {
  //   click(container, slotId, 2);
  // }
  public static void shiftClick(Container container, int slotId) {
    if (container instanceof CreativeScreen.CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Current.playerContainer().slotClick(slotId, 0, ClickType.QUICK_MOVE, Current.player());
      Current.playerContainer().detectAndSendChanges();
      return;
    }
    Current.interactionManager().windowClick(container.windowId, slotId,
        0, ClickType.QUICK_MOVE, Current.player());
  }
  public static void click(Container container, int slotId, int button) {
    if (container instanceof CreativeScreen.CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Current.playerContainer().slotClick(slotId, button, ClickType.PICKUP, Current.player());
      Current.playerContainer().detectAndSendChanges();
      return;
    }
    Current.interactionManager().windowClick(container.windowId, slotId,
        button, ClickType.PICKUP, Current.player());
  }

}