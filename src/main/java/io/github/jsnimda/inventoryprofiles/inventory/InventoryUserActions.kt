package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort.GroupingType
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions

object InventoryUserActions {

  fun doSort() {
    VirtualSorterPort.doSort(SortingMethodProviders.current(), GroupingType.PRESERVED)
  }
  fun doSortInColumns() {
    VirtualSorterPort.doSort(SortingMethodProviders.current(), GroupingType.COLUMNS)
  }
  fun doSortInRows() {
    VirtualSorterPort.doSort(SortingMethodProviders.current(), GroupingType.ROWS)
  }
  fun doMoveAll() {
    ContainerActions.moveAllAlike(VanillaState.shiftDown())
  }

  fun handleCloseContainer() {
    ContainerActions.cleanCursor()
    ContainerActions.cleanTempSlotsForClosing()
  }

}