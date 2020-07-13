package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.options.ConfigEnum
import io.github.jsnimda.common.config.options.ConfigString
import io.github.jsnimda.common.util.containsAny
import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.BeaconContainer
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.inventoryprofiles.client.TellPlayer
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.PostAction
import io.github.jsnimda.inventoryprofiles.config.SortingMethodIndividual
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.inventory.ContainerType.*
import io.github.jsnimda.inventoryprofiles.inventory.action.*
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.toNamespacedString

object GeneralInventoryActions {

  fun doSort() {
    with(GuiSettings) { doSort(REGULAR_SORT_ORDER, REGULAR_CUSTOM_RULE, REGULAR_POST_ACTION) }
  }

  fun doSortInColumns() {
    with(GuiSettings) { doSort(IN_COLUMNS_SORT_ORDER, IN_COLUMNS_CUSTOM_RULE, IN_COLUMNS_POST_ACTION) }
  }

  fun doSortInRows() {
    with(GuiSettings) { doSort(IN_ROWS_SORT_ORDER, IN_ROWS_CUSTOM_RULE, IN_ROWS_POST_ACTION) }
  }

  fun doSort(
    sortOrder: ConfigEnum<SortingMethodIndividual>,
    customRule: ConfigString,
    postAction: ConfigEnum<PostAction>
  ) {
    val screen = Vanilla.screen()
    if (screen != null && screen !is ContainerScreen<*>) return
    TellPlayer.listenLog(Log.LogLevel.WARN) {
      InnerActions.doSort(sortOrder.value.rule(customRule.value), postAction.value)
    }
  }

  // MOVE_ALL_AT_CURSOR off = to container, on -> (pointing container -> to player) else to container
  // use MOVE_ALL_AT_CURSOR instead of SORT_AT_CURSOR
  fun doMoveMatch() {
    val types = ContainerTypes.getTypes(Vanilla.container())
    if (types.contains(CREATIVE)) return // no do creative menu
    if (!types.containsAny(setOf(SORTABLE_STORAGE, NO_SORTING_STORAGE, CRAFTING))) return
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
    if (types.contains(CRAFTING)) {
      doMoveMatchCrafting()
      return
    }
    val includeHotbar = // xor
      ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing() != ModSettings.ALWAYS_INCLUDE_HOTBAR.booleanValue
    val moveAll = // xor
      ModSettings.MOVE_ALL_MODIFIER.isPressing() != ModSettings.ALWAYS_MOVE_ALL.booleanValue
    AdvancedContainer.tracker {
      with(AreaTypes) {
        val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) -
            lockedSlots
        val container = itemStorage
        val source = (if (toPlayer) container else player).get().asSubTracker
        val destination = (if (toPlayer) player else container).get().asSubTracker // source -> destination
        if (moveAll) {
          source.moveAllTo(destination)
        } else {
          source.moveMatchTo(destination)
        }
      }
    }
  }

  fun doMoveMatchCrafting() {
    val includeHotbar = VanillaUtil.altDown()
    AdvancedContainer.tracker {
      with(AreaTypes) {
        val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) -
            lockedSlots
        val target = craftingIngredient
        val source = player.get().asSubTracker
        val destination = target.get().asSubTracker // source -> destination
        source.moveMatchCraftingTo(destination)
      }
    }
  }

  fun dumpItemNbt() {
    val stack = vFocusedSlot()?.`(itemStack)` ?: vCursorStack()
    TellPlayer.chat(stack.itemType.toNamespacedString())
  }

  fun cleanCursor() {
    if (vCursorStack().isEmpty()) return
    AdvancedContainer(instant = true) {
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
    AdvancedContainer.tracker {
      with(AreaTypes) {
        val forcePlayerSide = forcePlayerSide()
        val target: ItemArea
        if (forcePlayerSide || itemStorage.get().isEmpty()) {
          target = (playerStorage - lockedSlots).get()
          if (ModSettings.RESTOCK_HOTBAR.booleanValue) {
            // priority: mainhand -> offhand -> hotbar 1-9
            (playerHands + playerHotbar).get().asSubTracker.restockFrom(target.asSubTracker)
          }
        } else {
          target = itemStorage.get()
        }
        target.asSubTracker.sort(sortingRule, postAction, target.isRectangular, target.width, target.height)
      }
    }
  }


  fun cleanTempSlotsForClosing() {
    // in vanilla, seems only beacon will drop the item, handle beacon only
    //   - clicking cancel button in beacon will bypass
    //     ClientPlayerEntity.closeContainer (by GuiCloseC2SPacket instead)
    if (Vanilla.container() !is BeaconContainer) return
    if (!Vanilla.container().`(slots)`[0].`(itemStack)`.isEmpty()) { // beacon item
      ContainerClicker.shiftClick(0)
    }
  }

}


