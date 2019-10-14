package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.GuiUtils;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.GroupingShapeProviders;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;

/**
 * SorterEventPort
 *  - Main entry to the core sorting code
 */
public class SorterEventPort {

  public static void handleCloseContainer(){
    MinecraftClient mc = MinecraftClient.getInstance();
    ClientPlayerEntity player = mc.player;
    ContainerActions.cleanCursor();
    if (ContainerUtils.isContainerStorage(player.container)) {
      return;
    }
    ContainerActions.cleanTempSlots(player.container);
  }

  private static boolean isPointingSelfInventory() {
    Slot s = ContainerUtils.getSlotUnderMouse();
    return AdvancedOptions.SORT_CURSOR_POINTING.getBooleanValue() && s != null && s.inventory instanceof PlayerInventory;
  }

  public static void callDoSort() {
    callDoSort(SortingMethodProviders.DEFAULT, GroupingShapeProviders.PRESERVED);
  }
  public static void callDoSort(ISortingMethodProvider sordingProvider, IGroupingShapeProvider groupingProvider) {
    Container container = MinecraftClient.getInstance().player.container;
    ContainerInfo info = ContainerUtils.getContainerInfo(container);
    ContainerActions.cleanCursor();
    boolean sortSelf = isPointingSelfInventory();
    List<Slot> target = (!sortSelf && info.isStorage && info.nonPlayerSlots.size() >= 9) ?
      info.nonPlayerSlots : info.playerStorageSlots;
    List<Integer> targetMapp = target.stream().map(x -> x.id).collect(Collectors.toList());
    VirtualSorterPort.doSort(container, target, targetMapp, sordingProvider, groupingProvider);
  }
  public static void doSortAction() {
    callDoSort();
  }
  public static void doSortActionByGroupColumns() {
    callDoSort(SortingMethodProviders.DEFAULT, GroupingShapeProviders.COLUMNS);
  }
  public static void doSortActionByGroupRows() {
    callDoSort(SortingMethodProviders.DEFAULT, GroupingShapeProviders.ROWS);
  }
  public static void doSwitchProfile() {

  }
  public static void doMoveAll() {
    Container container = MinecraftClient.getInstance().player.container;
    ContainerInfo info = ContainerUtils.getContainerInfo(container);
    if (info.nonPlayerSlots.size() - info.craftingResultSlots.size() > 0) {
      ContainerActions.moveAllAlike();
    }
  }

  private static final ImmutableList<IKeybind> SHOULD_HANDLE_KEY_LIST = ImmutableList.of(
    Generic.SORT_INVENTORY.getKeybind(),
    Generic.SORT_INVENTORY_BY_GROUP_COLUMNS.getKeybind(),
    Generic.SORT_INVENTORY_BY_GROUP_ROWS.getKeybind(),
    Generic.SWITCH_PROFILE.getKeybind(),
    Generic.MOVE_ALL_CONTAINER_EXISTING_ITEMS.getKeybind()
  );
  public static boolean shouldHandle(IKeybind key){
    return SHOULD_HANDLE_KEY_LIST.contains(key);
  }
  public static boolean handleKey(KeyAction action, IKeybind key){
    MinecraftClient mc = MinecraftClient.getInstance();
    ClientPlayerEntity player = mc.player;
    if (player.container == null) return false;
    if (key == Generic.SORT_INVENTORY.getKeybind()) {
      doSortAction();
      return true;
    } else if (key == Generic.SORT_INVENTORY_BY_GROUP_COLUMNS.getKeybind()) {
      doSortActionByGroupColumns();
      return true;
    } else if (key == Generic.SORT_INVENTORY_BY_GROUP_ROWS.getKeybind()) { 
      doSortActionByGroupRows();
      return true;
    } else if (key == Generic.SWITCH_PROFILE.getKeybind()) {
      doSwitchProfile();
      return true;
    } else if (key == Generic.MOVE_ALL_CONTAINER_EXISTING_ITEMS.getKeybind()) {
      doMoveAll();
      return true;
    }
    
    return false;
  }

}