package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.alias.BeaconContainer
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.PostAction
import io.github.jsnimda.inventoryprofiles.inventory.AdvancedContainer.Companion.cleanCursor
import io.github.jsnimda.inventoryprofiles.inventory.action.restockFrom
import io.github.jsnimda.inventoryprofiles.inventory.action.sort
import io.github.jsnimda.inventoryprofiles.item.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.util.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.util.`(slots)`
import io.github.jsnimda.inventoryprofiles.util.focusedSlot

object GeneralInventoryActions {

  fun doSort() {
    InnerActions.doSort(GuiSettings.REGULAR_SORT_ORDER.value.rule, GuiSettings.REGULAR_POST_ACTION.value)
  }

  fun doSortInColumns() {
    InnerActions.doSort(GuiSettings.IN_COLUMNS_SORT_ORDER.value.rule, GuiSettings.IN_COLUMNS_POST_ACTION.value)
  }

  fun doSortInRows() {
    InnerActions.doSort(GuiSettings.IN_ROWS_SORT_ORDER.value.rule, GuiSettings.IN_ROWS_POST_ACTION.value)
  }

  fun doMoveMatch() {
    //todo
//    ContainerActions.moveAllAlike(VanillaState.shiftDown())
  }

  fun doMoveMatch(chestSide: Boolean) {
    //todo
//    ContainerActions.moveAllAlike(VanillaState.shiftDown())
  }

  fun continuousCrafting() {
    // todo
  }

  fun cleanCursor() {
    if (VanillaInGame.cursorStack().`(itemStack)`.isEmpty()) return
    AdvancedContainer.arrange(true) { ->
      cleanCursor()
    }
  }

  fun handleCloseContainer() {
    cleanCursor()
    InnerActions.cleanTempSlotsForClosing()
  }

}

private object InnerActions {

  fun doSort(sortingRule: Rule, postAction: PostAction) = tryCatch {
    innerDoSort(sortingRule, postAction)
  }

  fun innerDoSort(sortingRule: Rule, postAction: PostAction) {
    AdvancedContainer.arrange { tracker ->
      val forcePlayerSide = ModSettings.SORT_AT_CURSOR.booleanValue &&
          VanillaInGame.focusedSlot()?.inventory is PlayerInventory
      val target: Zone
      if (forcePlayerSide || getZone(ZoneTypes.itemStorage).isEmpty()) {
        target = getZone(ZoneTypes.playerStorage)
        if (ModSettings.RESTOCK_HOTBAR.booleanValue) {
          // priority: mainhand -> offhand -> hotbar 1-9
          tracker.subTracker(ZoneTypes.playerHandsAndHotbar).restockFrom(tracker.subTracker(target))
        }
      } else {
        target = getZone(ZoneTypes.itemStorage)
      }
      tracker.subTracker(target).sort(sortingRule, postAction, target.isRectangular, target.width, target.height)
    }
  }


  fun cleanTempSlotsForClosing() {
    // in vanilla, seems only beacon will drop the item, handle beacon only
    //   - clicking cancel button in beacon will bypass
    //     ClientPlayerEntity.closeContainer (by GuiCloseC2SPacket instead)

    // in vanilla, seems only beacon will drop the item, handle beacon only
    //   - clicking cancel button in beacon will bypass
    //     ClientPlayerEntity.closeContainer (by GuiCloseC2SPacket instead)
    if (Vanilla.container() !is BeaconContainer) return
    if (!Vanilla.container().`(slots)`[0].`(itemStack)`.isEmpty()) { // beacon item
      ContainerClicker.shiftClick(0)
    }
  }

}


