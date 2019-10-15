package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import io.github.jsnimda.inventoryprofiles.mixin.IMixinSlot;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.container.HopperContainer;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.LecternContainer;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * ContainerUtils
 */
public class ContainerUtils {



  //public static int getOccupiedSlotWithRoomForStack() {
  public static boolean areItemsEqual(ItemStack itemStack_1, ItemStack itemStack_2) {
    return itemStack_1.getItem() == itemStack_2.getItem() && ItemStack.areTagsEqual(itemStack_1, itemStack_2);
  }
  public static int getRoomForStackIfSlotOccupied(ItemStack slotItem, ItemStack foreignItem) {
    if (!areItemsEqual(slotItem, foreignItem)) {
      return 0;
    }
    // need to check getInvMaxStackAmount (?)
    return slotItem.getMaxCount() - slotItem.getCount();
  }
  public static int getRoomForStack(ItemStack slotItem, ItemStack foreignItem) {
    if (slotItem.isEmpty()) return foreignItem.getMaxCount();
    return getRoomForStackIfSlotOccupied(slotItem, foreignItem);
  }

  
  public static boolean isContainerStorage(Container container) {
    if (container instanceof GenericContainer
        || container instanceof ShulkerBoxContainer
        || container instanceof HorseContainer
        || container instanceof Generic3x3Container
        || container instanceof HopperContainer
        || container instanceof BrewingStandContainer
        || container instanceof AbstractFurnaceContainer
        ) {
      return true;
    }
    if (container instanceof CreativeInventoryScreen.CreativeContainer
        || container instanceof PlayerContainer
        || container instanceof CraftingTableContainer
        || container instanceof EnchantingTableContainer
        || container instanceof AnvilContainer
        || container instanceof MerchantContainer
        || container instanceof BeaconContainer
        || container instanceof CartographyTableContainer
        || container instanceof GrindstoneContainer
        || container instanceof LecternContainer
        || container instanceof LoomContainer
        || container instanceof StonecutterContainer
        ) {
      return false;
    }
    // TODO handle mods
    System.out.println("[inventoryprofiles] unknown container (mod?)");
    System.out.println(" - container - " + container);
    System.out.println(" - type - " + container.getType());
    return false;
  }
  /**
   * true if items in the slot wont be dropped after the gui closed
   */
  public static boolean isSlotStorage(Slot slot, Container container) {
    return (slot.inventory instanceof PlayerInventory) || isContainerStorage(container);
  }
  public static ContainerInfo getContainerInfo(Container container) {
    return new ContainerInfo(container);
  }
  public static class ContainerInfo {
    // slot.id is real
    // invSlot 
    //   head,chest,legs,feet 39 38 37 36
    //   offhand 40
    //   hotbar 0 - 8    left to right
    //   storage 9 - 35    left to right, top to bottom
    public final Container container;
    public final boolean isStorage;
    public List<Slot> playerHotbarSlots = new ArrayList<>();    // typically 9 slots
    public List<Slot> playerStorageSlots = new ArrayList<>();   // typically 27 slots
    public Slot playerHeadSlot = null;
    public Slot playerChestSlot = null;
    public Slot playerLegsSlot = null;
    public Slot playerFeetSlot = null;
    public Slot playerOffhandSlot = null;
    public Slot playerMainhandSlot = null; // repeated from playerHotbarSlots
    public List<Slot> nonPlayerSlots = new ArrayList<>();       // all other slots, = all of below lists
    public List<Slot> craftingSlots = new ArrayList<>();        // slots in CraftingInventory (if any)
    public List<Slot> craftingResultSlots = new ArrayList<>();  // slots in CraftingResultInventory (if any)
    public ListMultimap<Inventory, Slot> containerSlots = ArrayListMultimap.create();  // slots in container (if any)
    
    public ContainerInfo(Container container) {
      this.container = container;
      isStorage = isContainerStorage(container);
      Slot[] armorSlots = new Slot[5];
      List<Slot> slots = container.slotList;
      for (Slot s : slots) { // assumed in order of slot.id
        int invSlot = ((IMixinSlot)s).getInvSlot();
        if (s.inventory instanceof PlayerInventory) {
          if (invSlot >= 0 && invSlot < 9) {
            playerHotbarSlots.add(s);
            if (((PlayerInventory)s.inventory).selectedSlot == invSlot) {
              playerMainhandSlot = s;
            }
          } else if (invSlot <= 35) {
            playerStorageSlots.add(s);
          } else if (invSlot <= 40){
            armorSlots[invSlot-36] = s;
          }
        } else if (s.inventory instanceof CraftingInventory) {
          nonPlayerSlots.add(s);
          craftingSlots.add(s);
        } else if (s.inventory instanceof CraftingResultInventory) {
          nonPlayerSlots.add(s);
          craftingResultSlots.add(s);
        } else {
          nonPlayerSlots.add(s);
          containerSlots.put(s.inventory, s);
        }
      }
      playerHeadSlot  = armorSlots[3];
      playerChestSlot = armorSlots[2];
      playerLegsSlot  = armorSlots[1];
      playerFeetSlot  = armorSlots[0];
      playerOffhandSlot = armorSlots[4];
    }
  }

}