package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.jsnimda.inventoryprofiles.Log;
import io.github.jsnimda.inventoryprofiles.config.ModSettings;
import io.github.jsnimda.inventoryprofiles.sorter.BiVirtualSlots;
import io.github.jsnimda.inventoryprofiles.sorter.Click;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlot;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.DistributeSorter;
import net.minecraft.client.gui.screen.Screen;
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
      leftClick(Getter.slotId(focuesdSlot));
    }
    for (CleanCursorCandidateSlot cccs : CleanCursorCandidateSlot.gets(putToContainer, putToHotbar)) {
      if (Current.cursorStack().isEmpty()) return;
      if (cccs.suit(cursorStack)) {
        leftClick(Getter.slotId(cccs.slot));
      }
    }
  }
  public static void quickMoveByPlayerStorageSlotsFirst(int fromSlotId, boolean putToHotbar) { // fixing for when player inventory is full
    Slot slot = Current.container().slots.get(fromSlotId);
    if (slot.getStack().isEmpty()) return;
    boolean pickedUpFromSlot = false;
    for (CleanCursorCandidateSlot cccs : CleanCursorCandidateSlot.gets(false, putToHotbar)) {
      if (pickedUpFromSlot && Current.cursorStack().isEmpty()) return;
      if (cccs.suit(pickedUpFromSlot ? Current.cursorStack() : slot.getStack())) {
        if (!pickedUpFromSlot) {
          pickedUpFromSlot = true;
          leftClick(fromSlotId);
        }
        leftClick(Getter.slotId(cccs.slot));
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
    ContainerInfo info = CurrentState.containerInfo();
    List<Slot> source = info.playerStorageSlots;
    List<Slot> target = Stream.concat(
      Stream.of(info.playerMainhandSlot, info.playerOffhandSlot),
      info.playerHotbarSlots.stream().filter(x->x!=info.playerMainhandSlot)
    ).filter(x -> x != null).collect(Collectors.toList());
    List<VirtualSlot> aVS = Converter.toVirtualSlotList(source);
    List<VirtualSlot> bVS = Converter.toVirtualSlotList(target);
    List<VirtualSlot> from = Converter.concat(aVS, bVS, x->x.copy());
    new BiVirtualSlots(aVS, bVS).restock();
    List<VirtualSlot> to = Converter.concat(aVS, bVS, x->x.copy());
    genericClicks(info.container, ContainerUtils.calcDiff(from, to), 0);
  }

  public static void evenlyDistributeCraftingSlots(boolean includeHotbar) {
    ContainerInfo info = CurrentState.containerInfo();
    List<Slot> fromSlots = new ArrayList<>();
    fromSlots.addAll(info.playerStorageSlots);
    if (includeHotbar)
      fromSlots.addAll(info.playerHotbarSlots);
    List<VirtualSlot> aVS = Converter.toVirtualSlotList(fromSlots);
    List<VirtualSlot> bVS = Converter.toVirtualSlotList(info.craftingSlots);
    List<VirtualSlot> from = Converter.concat(aVS, bVS, x->x.copy());
    new BiVirtualSlots(aVS, bVS).restock();
    VirtualSlot.bulkAction(bVS, x -> new DistributeSorter().sort(x));
    List<VirtualSlot> to = Converter.concat(aVS, bVS, x->x.copy());
    genericClicks(info.container, ContainerUtils.calcDiff(from, to), 0);
  }

  public static void moveAllAlike(boolean includeHotbar) {
    moveAllAlike(ModSettings.INSTANCE.getSORT_AT_CURSOR().getBooleanValue() && ContainerUtils.cursorPointingPlayerInventory(), includeHotbar);
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
      types = new VirtualSlotsStats(Converter.toVirtualItemStackList(info.storageSlots)).getItemTypes();
      checkSlots.addAll(info.playerStorageSlots);
      if (includeHotbar)
        checkSlots.addAll(info.playerHotbarSlots);
    } else { // chest to player
      checkSlots.addAll(info.storageSlots);
      List<Slot> typeSlots = new ArrayList<>();
      typeSlots.addAll(info.playerStorageSlots);
      if (includeHotbar)
        typeSlots.addAll(info.playerHotbarSlots);
      types = new VirtualSlotsStats(Converter.toVirtualItemStackList(typeSlots)).getItemTypes();
      if (!Current.cursorStack().isEmpty()) {
        cleanCursor(); // as moving to player inventory depends on clicks
                           // for playerStorageSlots first purpose
      }
    }
    for (Slot s : checkSlots) {
      if (s.hasStack()) {
        for (VirtualItemType t : types) {
          if (Converter.toVirtualItemType(s.getStack()).equals(t)) {
            if (!moveToPlayerInventory) {
              shiftClick(Current.container(), Getter.slotId(s));
            } else {
              quickMoveByPlayerStorageSlotsFirst(Getter.slotId(s), includeHotbar);
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
    genericClick(container, slotId, 0, SlotActionType.QUICK_MOVE);
  }
  public static void click(Container container, int slotId, int button) {
    genericClick(container, slotId, button, SlotActionType.PICKUP);
  }

  public static void genericClick(Container container, int slotId, int button, SlotActionType actionType) {
    if (container instanceof CreativeInventoryScreen.CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Current.playerContainer().onSlotClick(slotId, button, actionType, Current.player());
      Current.playerContainer().sendContentUpdates();
      return;
    }
    Current.interactionManager().method_2906(container.syncId, slotId, button, actionType, Current.player());
  }

  public static void genericClick(Container container, Click click) {
    genericClick(container, click.slotId, click.button, click.actionType);
  }

  public static void genericClicks(Container container, List<Click> clicks, int interval) {
    int lclick = 0;
    int rclick = 0;
    for (Click c : clicks) {
      lclick += c.button == 0 ? 1 : 0;
      rclick += c.button == 1 ? 1 : 0;
    }
    logClicks(clicks.size(), lclick, rclick, interval);
    new Runnable(){
      Screen currentScreen = Current.screen();
      @Override
      public void run() {
        CodeUtils.timedTasks(clicks, (c, timer)->{
          if (timer != null) {
            if (container != Current.container()) {
              timer.cancel();
              Log.debugLogs("[inventoryprofiles] Click cancelled due to container changed");
              return;
            }
            // FIXME when gui close cursor stack will put back to container that will influence the sorting result
            if (ModSettings.INSTANCE.getSTOP_AT_SCREEN_CLOSE().getBooleanValue() && currentScreen != Current.screen()) {
              if (currentScreen == null) {
                currentScreen = Current.screen();
              } else {
                timer.cancel();
                Log.debugLogs("[inventoryprofiles] Click cancelled due to screen closed");
                return;
              }
            }
          }
          genericClick(container, c);
        }, interval, () -> {});
      }
    }.run();
  }

  private static void logClicks(int total, int lclick, int rclick, int interval) {
    Log.debugLogs(String.format("[inventoryprofiles] Click count total %d. %d left. %d right. Time = %ss",
      total, lclick, rclick, total * interval / (double)1000));
  }

}