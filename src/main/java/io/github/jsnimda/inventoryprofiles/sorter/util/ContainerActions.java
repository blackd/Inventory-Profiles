package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
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
    MinecraftClient mc = MinecraftClient.getInstance();
    ClientPlayerEntity player = mc.player;
    // creative menu is not handled
    if (player.container == null || player.container instanceof CreativeInventoryScreen.CreativeContainer) return;
    ItemStack cursorStack = player.inventory.getCursorStack();
    if (!cursorStack.isEmpty()) {
      ContainerActions.cleanCursor(cursorStack, player.container);
    }
  }
  public static void cleanCursor(ItemStack cursorStack, Container container) {
    int count = cursorStack.getCount();
    if (count <= 0) return;
    List<Integer> clickList = new ArrayList<>();
    cleanCursor_fillClickList(cursorStack, container, clickList);
    for(int slotId : clickList) {
      leftClick(container, slotId);
    }
  }
  private static void cleanCursor_fillClickList(ItemStack cursorStack, Container container, List<Integer> clickList) {
    // refer: PlayerInventory.offerOrDrop, getOccupiedSlotWithRoomForStack
    // vanilla getOccupiedSlotWithRoomForStack logic:
    //    find alike: mainhand, offhand, hotbar, storage
    //      -> empty (in order of invSlot)
    ContainerInfo info = ContainerUtils.getContainerInfo(container);
    // my logic
    // hovering slot -> if not:
    // find alike: mainhand, offhand, hotbar, storage
    //  -> empty: storage, hotbar, offhand
    //  -> if container is storage -> container alike -> container empty
    int count = cursorStack.getCount();
    Slot hoveringSlot = ContainerUtils.getSlotUnderMouse();
    if (hoveringSlot != null) {
      if (!hoveringSlot.hasStack()) {
        clickList.add(hoveringSlot.id);
        count -= cursorStack.getMaxCount();
      }
      if (count <= 0) return;
      count -= cleanCursor_attempt(hoveringSlot, clickList, cursorStack);
      if (count <= 0) return;
    }
    for (Slot s : cleanCursor_alikeOrder(info)) {
      count -= cleanCursor_attempt(s, clickList, cursorStack);
      if (count <= 0) return;
    }
    for (Slot s : cleanCursor_emptyOrder(info)) {
      if (!s.hasStack()) {
        clickList.add(s.id);
        count -= cursorStack.getMaxCount();
      }
      if (count <= 0) return;
    }
    if (info.isStorage) {
      for (Slot s : info.nonPlayerSlots) {
        count -= cleanCursor_attempt(s, clickList, cursorStack);
        if (count <= 0) return;
      }
      for (Slot s : info.nonPlayerSlots) {
        if (!s.hasStack()) {
          clickList.add(s.id);
          count -= cursorStack.getMaxCount();
        }
        if (count <= 0) return;
      }
    }
  }
  private static int cleanCursor_attempt(Slot s, List<Integer> clickList, ItemStack cursorStack){
    int room = ContainerUtils.getRoomForStackIfSlotOccupied(s.getStack(), cursorStack);
    if (room > 0) {
      clickList.add(s.id);
    }
    return room;
  }
  private static List<Slot> cleanCursor_alikeOrder(ContainerInfo info) {
    List<Slot> ss = new ArrayList<>();
    if (info.playerMainhandSlot != null) ss.add(info.playerMainhandSlot);
    if (info.playerOffhandSlot != null) ss.add(info.playerOffhandSlot);
    info.playerHotbarSlots.forEach(x -> {
      if (x != info.playerMainhandSlot) ss.add(x);
    });
    ss.addAll(info.playerStorageSlots);
    return ss;
  }
  private static List<Slot> cleanCursor_emptyOrder(ContainerInfo info) {
    List<Slot> ss = new ArrayList<>();
    ss.addAll(info.playerStorageSlots);
    ss.addAll(info.playerHotbarSlots);
    if (info.playerOffhandSlot != null) ss.add(info.playerOffhandSlot);
    return ss;
  }

  public static void cleanTempSlots(Container container) {
    // in vanilla, seems only beacon will drops the item, handle beacon only
    //   - clicking cancel button in beacon will still bypass this (by GuiCloseC2SPacket)
    if (!(container instanceof BeaconContainer)) return;
    if (container.getSlot(0).hasStack()) {
      shiftClick(container, 0);
    }
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
      return; // creative menu dont use method_2906
    }
    MinecraftClient mc = MinecraftClient.getInstance();
    mc.interactionManager.method_2906(container.syncId, slotId,
        0, SlotActionType.QUICK_MOVE, mc.player);
  }
  public static void click(Container container, int slotId, int button) {
    if (container instanceof CreativeInventoryScreen.CreativeContainer) {
      return; // creative menu dont use method_2906
    }
    MinecraftClient mc = MinecraftClient.getInstance();
    mc.interactionManager.method_2906(container.syncId, slotId,
        button, SlotActionType.PICKUP, mc.player);
  }
  
  public static void moveAllAlike() {
    
  }


}