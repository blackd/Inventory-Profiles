package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.BeaconContainer
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.PostAction
import io.github.jsnimda.inventoryprofiles.event.TellPlayer
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.inventory.AdvancedContainer.Companion.cleanCursor
import io.github.jsnimda.inventoryprofiles.inventory.VanillaContainerType.CREATIVE
import io.github.jsnimda.inventoryprofiles.inventory.action.moveAllTo
import io.github.jsnimda.inventoryprofiles.inventory.action.moveMatchTo
import io.github.jsnimda.inventoryprofiles.inventory.action.restockFrom
import io.github.jsnimda.inventoryprofiles.inventory.action.sort
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.toNamespacedString

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

  // MOVE_ALL_AT_CURSOR off = to container, on -> (pointing container -> to player) else to container
  // use MOVE_ALL_AT_CURSOR instead of SORT_AT_CURSOR
  fun doMoveMatch() {
    val types = ContainerTypes.getTypes(Vanilla.container())
    if (types.contains(CREATIVE)) return // no do creative menu
    val forceToPlayer = ModSettings.MOVE_ALL_AT_CURSOR.booleanValue &&
        vFocusedSlot()?.let { it.`(inventory)` !is PlayerInventory } ?: false // hover slot exist and not player
    if (forceToPlayer) {
      doMoveMatch(true) // container to player // non player and player by PlayerInventory
    } else {
      doMoveMatch(false) // player to container
    }
  }

  fun doMoveMatch(toPlayer: Boolean) { // true container to player or false player to container
    val types = ContainerTypes.getTypes(Vanilla.container())
    if (types.contains(CREATIVE)) return // no do creative menu
    val includeHotbar = VanillaUtil.altDown()
    val moveAll = VanillaUtil.shiftDown()
    AdvancedContainer.arrange { tracker ->
      val player =
        getItemArea(if (includeHotbar) AreaTypes.playerStorageAndHotbarAndOffhand else AreaTypes.playerStorage)
      val container = getItemArea(AreaTypes.nonPlayer)
      val source = tracker.subTracker(if (toPlayer) container else player)
      val destination = tracker.subTracker(if (toPlayer) player else container) // source -> destination
      if (moveAll) {
        source.moveAllTo(destination)
      } else {
        source.moveMatchTo(destination)
      }
    }
  }

  fun dumpItemNbt() {
    val stack = vFocusedSlot()?.`(itemStack)` ?: vCursorStack()
    TellPlayer.chat(stack.itemType.toNamespacedString())
  }

  fun continuousCrafting() {
    // todo
  }

  fun cleanCursor() {
    if (vCursorStack().isEmpty()) return
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

  private fun forcePlayerSide(): Boolean = // default container side
    ModSettings.SORT_AT_CURSOR.booleanValue && vFocusedSlot()?.inventory is PlayerInventory

  fun doSort(sortingRule: Rule, postAction: PostAction) = tryCatch {
    innerDoSort(sortingRule, postAction)
  }

  fun innerDoSort(sortingRule: Rule, postAction: PostAction) {
    AdvancedContainer.arrange { tracker ->
      val forcePlayerSide = forcePlayerSide()
      val target: ItemArea
      if (forcePlayerSide || getItemArea(AreaTypes.itemStorage).isEmpty()) {
        target = getItemArea(AreaTypes.playerStorage)
        if (ModSettings.RESTOCK_HOTBAR.booleanValue) {
          // priority: mainhand -> offhand -> hotbar 1-9
          tracker.subTracker(AreaTypes.playerHandsAndHotbar).restockFrom(tracker.subTracker(target))
        }
      } else {
        target = getItemArea(AreaTypes.itemStorage)
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


