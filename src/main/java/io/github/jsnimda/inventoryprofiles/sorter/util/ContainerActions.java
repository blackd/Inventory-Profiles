package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerCategory;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerInfo;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
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
      leftClick(focuesdSlot.id);
    }
    for (CleanCursorCandidateSlot cccs : CleanCursorCandidateSlot.gets(putToContainer, putToHotbar)) {
      if (Current.cursorStack().isEmpty()) return;
      if (cccs.suit(cursorStack)) {
        leftClick(cccs.slot.id);
      }
    }
  }
  public static void quickMoveByPlayerStorageSlotsFirst(int fromSlotId, boolean putToHotbar) { // fixing for when player inventory is full
    Slot slot = Current.container().slotList.get(fromSlotId);
    if (slot.getStack().isEmpty()) return;
    boolean pickedUpFromSlot = false;
    for (CleanCursorCandidateSlot cccs : CleanCursorCandidateSlot.gets(false, putToHotbar)) {
      if (pickedUpFromSlot && Current.cursorStack().isEmpty()) return;
      if (cccs.suit(pickedUpFromSlot ? Current.cursorStack() : slot.getStack())) {
        if (!pickedUpFromSlot) {
          pickedUpFromSlot = true;
          leftClick(fromSlotId);
        }
        leftClick(cccs.slot.id);
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
      ContainerInfo info = ContainerInfo.of(Current.container());
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
      if (skipIfNoStack && !slot.hasStack()) return false;
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
    if (Current.container().getSlot(0).hasStack()) { // beacon item
      shiftClick(Current.container(), 0);
    }
  }

  public static void restockHotbar() {
    ContainerInfo info = ContainerInfo.of(Current.container());
    Stream.concat(
      Stream.of(info.playerMainhandSlot, info.playerOffhandSlot),
      info.playerHotbarSlots.stream().filter(x->x!=info.playerMainhandSlot)
    )
    .forEach(x->{
      if (x != null && x.hasStack() && ContainerUtils.getRemainingRoom(x, x.getStack()) > 0) {
        for (Slot s : info.playerStorageSlots) {
          if (s.hasStack() && ContainerUtils.getRemainingRoom(x, s.getStack()) > 0) {
            leftClick(s.id);
            leftClick(x.id);
          }
          if (ContainerUtils.getRemainingRoom(x, x.getStack()) <= 0) {
            if (!Current.cursorStack().isEmpty()) {
              leftClick(s.id);
            }
            break;
          }
        }
      }
    });
  }

  public static void evenlyDistributeCraftingSlots(boolean includeHotbar) {
    //TODO
  }

  public static void moveAllAlike(boolean includeHotbar) {
    moveAllAlike(AdvancedOptions.SORT_CURSOR_POINTING.getBooleanValue() && ContainerUtils.cursorPointingPlayerInventory(), includeHotbar);
  }
  public static void moveAllAlike(boolean moveToPlayerInventory, boolean includeHotbar) {
    ContainerInfo info = ContainerInfo.of(Current.container());
    if (info.category == ContainerCategory.CRAFTABLE_3x3
        || info.category == ContainerCategory.PLAYER_SURVIVAL) {
      evenlyDistributeCraftingSlots(includeHotbar);
      return;
    }
    if (info.storageSlots.isEmpty()) return;
    List<VirtualItemStack> types;
    List<Slot> checkSlots = new ArrayList<>();
    if (!moveToPlayerInventory) { // player to chest
      types = VirtualSorter.collapse(VirtualSorterPort.getListOfVirtualItemStackFrom(info.storageSlots));
      checkSlots.addAll(info.playerStorageSlots);
      if (includeHotbar)
        checkSlots.addAll(info.playerHotbarSlots);
    } else { // chest to player
      checkSlots.addAll(info.storageSlots);
      List<Slot> typeSlots = new ArrayList<>();
      typeSlots.addAll(info.playerStorageSlots);
      if (includeHotbar)
        typeSlots.addAll(info.playerHotbarSlots);
      types = VirtualSorter.collapse(VirtualSorterPort.getListOfVirtualItemStackFrom(typeSlots));
      if (!Current.cursorStack().isEmpty()) {
        cleanCursor(); // as moving to player inventory depends on clicks
                           // for playerStorageSlots first purpose
      }
    }
    for (Slot s : checkSlots) {
      if (s.hasStack()) {
        for (VirtualItemStack t : types) {
          if (VirtualSorterPort.getVirtualItemTypeFrom(s.getStack()).sameAs(t.itemtype)) {
            if (!moveToPlayerInventory) {
              shiftClick(Current.container(), s.id);
            } else {
              quickMoveByPlayerStorageSlotsFirst(s.id, includeHotbar);
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
    if (container instanceof CreativeInventoryScreen.CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Current.playerContainer().onSlotClick(slotId, 0, SlotActionType.QUICK_MOVE, Current.player());
      Current.playerContainer().sendContentUpdates();
      return;
    }
    Current.interactionManager().method_2906(container.syncId, slotId,
        0, SlotActionType.QUICK_MOVE, Current.player());
  }
  public static void click(Container container, int slotId, int button) {
    if (container instanceof CreativeInventoryScreen.CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Current.playerContainer().onSlotClick(slotId, button, SlotActionType.PICKUP, Current.player());
      Current.playerContainer().sendContentUpdates();
      return;
    }
    Current.interactionManager().method_2906(container.syncId, slotId,
        button, SlotActionType.PICKUP, Current.player());
  }

}