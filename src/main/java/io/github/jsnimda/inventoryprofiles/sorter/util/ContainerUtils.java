package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

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
import net.minecraft.container.TradeOutputSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TraderInventory;

/**
 * ContainerUtils
 */
public class ContainerUtils {

  public static boolean cursorPointingPlayerInventory() {
    return Current.focusedSlot() != null && Current.focusedSlot().inventory instanceof PlayerInventory;
  }

  public static int getRemainingRoom(@Nullable Slot slot, @Nullable ItemStack forItem) {
    if (slot == null || forItem == null || forItem.isEmpty()) return 0;
    if (!slot.canInsert(forItem)) return 0;
    if (!slot.hasStack()) return slot.getMaxStackAmount(forItem);
    ItemStack slotItem = slot.getStack();
    if (!areItemsEqual(slotItem, forItem)) return 0;
    int maxAmount = Math.min(slot.getMaxStackAmount(slotItem), slotItem.getMaxCount());
    return Math.max(0, maxAmount - slotItem.getCount());
  }
  public static boolean areItemsEqual(ItemStack itemStack_1, ItemStack itemStack_2) {
    return itemStack_1.getItem() == itemStack_2.getItem() && ItemStack.areTagsEqual(itemStack_1, itemStack_2);
  }

  
  public enum ContainerCategory {
    TRADABLE,
    CRAFTABLE_3x3,
    SORTABLE_3x3,
    SORTABLE_9xN, // 9x3 or 9x6
    SORTABLE_Nx3, // 5x3 for donkey, [1-5]x3 for llama
    NON_SORTABLE_STORAGE,
    NON_STORAGE,
    PLAYER_SURVIVAL, // including adventure and spectator
    PLAYER_CREATIVE,
    UNKNOWN;

    public static ContainerCategory of(Container container) {
      if (container instanceof GenericContainer
          || container instanceof ShulkerBoxContainer
          ) {
        return ContainerCategory.SORTABLE_9xN;
      }
      if (container instanceof HorseContainer) {
        return ContainerCategory.SORTABLE_Nx3;
      }
      if (container instanceof Generic3x3Container) {
        return ContainerCategory.SORTABLE_3x3;
      }
      if (container instanceof HopperContainer
          || container instanceof BrewingStandContainer
          || container instanceof AbstractFurnaceContainer
          ) {
        return ContainerCategory.NON_SORTABLE_STORAGE;
      }
  
      if (container instanceof CreativeInventoryScreen.CreativeContainer) {
        return ContainerCategory.PLAYER_CREATIVE;
      }
      if (container instanceof PlayerContainer) {
        return ContainerCategory.PLAYER_SURVIVAL;
      }
      if (container instanceof CraftingTableContainer) {
        return ContainerCategory.CRAFTABLE_3x3;
      }
      if (container instanceof EnchantingTableContainer
          || container instanceof AnvilContainer
          || container instanceof BeaconContainer
          || container instanceof CartographyTableContainer
          || container instanceof GrindstoneContainer
          || container instanceof LecternContainer
          || container instanceof LoomContainer
          || container instanceof StonecutterContainer
          ) {
        return ContainerCategory.NON_STORAGE;
      }
      if (container instanceof MerchantContainer) {
        return ContainerCategory.TRADABLE;
      }
      // TODO handle mods
      System.out.println("[inventoryprofiles] unknown container (mod?)");
      System.out.println(" - container - " + container);
      return ContainerCategory.UNKNOWN;
    }
  
    public boolean isStorage() {
      return this == SORTABLE_3x3
          || this == SORTABLE_9xN
          || this == SORTABLE_Nx3
          || this == NON_SORTABLE_STORAGE
          || this == UNKNOWN
          ;
    }
  }

  public static class ContainerInfo {
    // slot.id is real
    // invSlot 
    //   head,chest,legs,feet 39 38 37 36
    //   offhand 40
    //   hotbar 0 - 8    left to right
    //   storage 9 - 35    left to right, top to bottom
    public final Container container;
    public final ContainerCategory category;
    public List<Slot> playerHotbarSlots = new ArrayList<>();    // typically 9 slots,  always exist
    public List<Slot> playerStorageSlots = new ArrayList<>();   // typically 27 slots, always exist
    public Slot playerHeadSlot = null;                          //                     only exist in PLAYER_SURVIVAL/PLAYER_CREATIVE
    public Slot playerChestSlot = null;                         //                     only exist in PLAYER_SURVIVAL/PLAYER_CREATIVE
    public Slot playerLegsSlot = null;                          //                     only exist in PLAYER_SURVIVAL/PLAYER_CREATIVE
    public Slot playerFeetSlot = null;                          //                     only exist in PLAYER_SURVIVAL/PLAYER_CREATIVE
    public Slot playerOffhandSlot = null;                       //                     only exist in PLAYER_SURVIVAL/PLAYER_CREATIVE
    public Slot playerMainhandSlot = null;      // selected slot in playerHotbarSlots, always exist
    public List<Slot> playerArmorSlots = new ArrayList<>(); // non null slots in [head, chest, legs, feet] order
    // ==========
    // notice: all slots below are not including player slots (even for sortableSlots and storageSlots)
    public List<Slot> nonPlayerSlots = new ArrayList<>();       // all the slots in the container excluding the player slots above
    public ListMultimap<Inventory, Slot> slotsMap = ArrayListMultimap.create();  // slots in container grouped by their inventory (if any)

    public List<Slot> craftingSlots = new ArrayList<>();        // slots in CraftingInventory, 2x2 or 3x3 in PLAYER_SURVIVAL/CRAFTABLE_3x3
    public List<Slot> craftingResultSlots = new ArrayList<>();  // slots in CraftingResultInventory , in PLAYER_SURVIVAL/CRAFTABLE_3x3
    public List<Slot> traderSlots = new ArrayList<>();          // slots in TraderInventory                                   , in TRADABLE
    public List<Slot> traderInputSlots = new ArrayList<>();     // slots in TraderInventory but not instanceof TradeOutputSlot, in TRADABLE
    public List<Slot> traderOutputSlots = new ArrayList<>();    // slots that instanceof TradeOutputSlot                      , in TRADABLE
    public List<Slot> sortableSlots = new ArrayList<>();        // slots that usually rectangular,            in SORTABLE_~
    public List<Slot> nonSortableStorageSlots = new ArrayList<>();  //                                        in NON_SORTABLE_STORAGE/SORTABLE_Nx3
    public List<Slot> storageSlots = new ArrayList<>();        // joint list of sortableSlots and nonSortableStorageSlots
    public List<Slot> nonStorageSlots = new ArrayList<>();     // not inlcuding crafting and trading slots,  in NON_STORAGE

    public int sortableWidth = 9; // = 3 in SORTABLE_3x3, = 1 to 5 in SORTABLE_Nx3, = 9 by default
    
    public static ContainerInfo of(Container container) {
      return new ContainerInfo(container);
    }

    private void translatePlayer() {
      translatePlayer(container);
    }
    private void translatePlayer(Container asContainer) {
      Slot[] armorSlots = new Slot[5];
      for (Slot s : asContainer.slotList) { // assumed in order of slot.id
        if (!(s.inventory instanceof PlayerInventory)) continue;
        int invSlot = ((IMixinSlot)s).getInvSlot();
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
      }

      playerHeadSlot  = armorSlots[3];
      playerChestSlot = armorSlots[2];
      playerLegsSlot  = armorSlots[1];
      playerFeetSlot  = armorSlots[0];
      playerOffhandSlot = armorSlots[4];
      Stream.of(playerHeadSlot, playerChestSlot, playerLegsSlot, playerFeetSlot)
        .filter(x->x!=null).forEach(x->playerArmorSlots.add(x));
    }
    private void translateNonPlayer() {
      for (Slot s : container.slotList) { // (need to test hidden slots?)
        if (s.inventory instanceof PlayerInventory) continue;
        nonPlayerSlots.add(s);
        slotsMap.put(s.inventory, s);
      }

      for (Slot s : container.slotList) {
        if (s.inventory instanceof PlayerInventory) continue;
        if (s instanceof TradeOutputSlot) {
          traderOutputSlots.add(s);
        }
        if (s.inventory instanceof CraftingInventory) {
          craftingSlots.add(s);
        } else if (s.inventory instanceof CraftingResultInventory) {
          craftingResultSlots.add(s);
        } else if (s.inventory instanceof TraderInventory) {
          traderSlots.add(s);
          if (!(s instanceof TradeOutputSlot)) {
            traderInputSlots.add(s);
          }
        } else if (!(s instanceof TradeOutputSlot)) {
          translateGenericSlots(s);
        }
      }
    }
    /**
     * fill sortableSlots, nonSortableStorageSlots, storageSlots, and nonStorageSlots
     * @param s
     */
    private void translateGenericSlots(Slot s) {
      if (category == ContainerCategory.NON_STORAGE) {
        nonStorageSlots.add(s);
      } else {
        storageSlots.add(s);
          if (category == ContainerCategory.NON_SORTABLE_STORAGE) {
          nonSortableStorageSlots.add(s);
        } else if (category == ContainerCategory.SORTABLE_Nx3) {
          if (s.id <= 1)
            nonSortableStorageSlots.add(s);
          else
            sortableSlots.add(s);
        } else if (category == ContainerCategory.SORTABLE_9xN
            || category == ContainerCategory.SORTABLE_3x3
            || category == ContainerCategory.UNKNOWN) {
          // we treat unknown container (mod?) as sortable no matter what
          sortableSlots.add(s);
        } else {
          System.out.println("[inventoryprofile] we shouldn't come here");
          System.out.println("[inventoryprofile] unknown slot: " + s + " in category " + category);
        }
      }
    }
    private void translateCreative() {
      translatePlayer(Current.playerContainer());
    }

    public ContainerInfo(Container container) {
      this.container = container;
      category = ContainerCategory.of(container);
      if (category != ContainerCategory.PLAYER_CREATIVE) {
        translatePlayer();
        translateNonPlayer();
      } else {
        translateCreative();
      }

      if (category == ContainerCategory.SORTABLE_3x3) {
        sortableWidth = 3;
      }
      
      if (category == ContainerCategory.SORTABLE_Nx3) {
        sortableWidth = sortableSlots.size() / 3;
      }
    }

  }



}