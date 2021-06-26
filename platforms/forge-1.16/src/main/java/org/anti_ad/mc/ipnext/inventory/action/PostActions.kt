package org.anti_ad.mc.ipnext.inventory.action

import org.anti_ad.mc.common.extensions.unlessIt
import org.anti_ad.mc.ipnext.item.*

object PostActions {

    fun groupInRows(slots: List<ItemStack>,
                    width: Int,
                    height: Int): List<ItemStack> =
        group(slots,
              width,
              height,
              true)

    fun groupInColumns(slots: List<ItemStack>,
                       width: Int,
                       height: Int): List<ItemStack> =
        group(slots,
              width,
              height,
              false)

    private fun group(slots: List<ItemStack>,
                      width: Int,
                      height: Int,
                      inRows: Boolean = false): List<ItemStack> {
        // only works for sorted items (also works on others (?) )
        if (slots.isEmpty()) return slots
        if (width * height != slots.size) error("Area is not rectangular!")
        val groups = slots.group()
        val slotCountPairList = groups.mapValues { (_, value) -> value.size }.toList()
        val calcResult = if (inRows) { // transpose
            GroupInColumnsCalculator(slotCountPairList,
                                     height,
                                     width).calc()?.map { (itemType, slotIndices) ->
                itemType to slotIndices.asIndicesTranspose(height,
                                                           width)
            }
        } else {
            GroupInColumnsCalculator(slotCountPairList,
                                     width,
                                     height).calc()
        } ?: return slots

        val ans = MutableList(width * height) { MutableItemStack.empty() }
        calcResult.forEach { (itemType, slotIndices) ->
            val sortedIndices = slotIndices.sorted()
            groups.getValue(itemType).forEachIndexed { index, (newItemType, newCount) ->
                val slotIndex = sortedIndices[index]
                ans[slotIndex].itemType = newItemType
                ans[slotIndex].count = newCount
            }
        }
        return ans
    }

//  fun distribute(slots: List<ItemStack>) = slots.cop().apply {
//    group().forEach { (_, slots) ->
//      distributeMonontonic(slots.sumBy { it.count }, slots.size).forEachIndexed { index, count ->
//        slots[index].count = count
//      }
//    }
//  }

}

private fun List<ItemStack>.group(): Map<ItemType, List<ItemStack>> =
    mapNotNull { it.unlessIt { isEmpty() } }.groupBy { it.itemType }

private fun transposedIndex(width: Int,
                            height: Int,
                            index: Int) =
    (index % width) * height + (index / width)

private fun List<Int>.asIndicesTranspose(width: Int,
                                         height: Int) =
    map {
        transposedIndex(width,
                        height,
                        it)
    }


private class GroupInColumnsCalculator(
    val slotCountPairList: List<Pair<ItemType, Int>>,
    val width: Int,
    val height: Int
) {
    fun calc(): List<Pair<ItemType, List<Int>>>? { // itemType, slotIndices
        val minRows = slotCountPairList.size
        if (minRows == 0) return null

        val ccList = mutableListOf<ColumnsCandidate>()
        for (columnsCount in 1..width) {
            if (minRows > height * columnsCount) continue
            val cc = ColumnsCandidate(slotCountPairList,
                                      distribute(width,
                                                 columnsCount),
                                      height)
            if (cc.succeeded) {
                if (cc.brokenGroups == 0) return cc.apply()
                ccList.add(cc)
            }
        }
        return ccList.minByOrNull { it.brokenGroups }?.apply()
    }

    class ColumnsCandidate(
        val slotCountPairList: List<Pair<ItemType, Int>>,
        val columnWidths: List<Int>,
        val height: Int
    ) {
        val width = columnWidths.sum()
        var brokenGroups = 0
        val succeeded: Boolean
        val cells: List<Cell> =
            columnWidths.mapIndexed { columnIndex, width ->
                List(height) { rowIndex ->
                    Cell(width,
                         rowIndex,
                         columnIndex)
                }
            }
                .flatten()
        var cellIndex = 0
        var allowBroken = false

        val eachCellsList = mutableListOf<List<Cell>>()

        init {
            succeeded = slotCountPairList.indices.all { addCellsForIndex(it) }
        }

        fun addCellsForIndex(index: Int): Boolean {
            val result = findCellsForRoom(slotCountPairList[index].second)
            if (result.isEmpty()) return false
            result.forEach { it.occupied = true }
            eachCellsList.add(result)
            if (!result.connected()) brokenGroups++
            return true
        }

        fun findCellsForRoom(slotCount: Int): List<Cell> {
            if (!findEmptyCell()) return listOf()
            if (!allowBroken) {
                // first loop, try not to break groups apart
                val initColumnIndex = cells[cellIndex].columnIndex
                var totalRoom = 0
                var neededCells = 0
                for (i in cellIndex until cells.size) {
                    totalRoom += cells[i].room
                    neededCells++
                    if (totalRoom >= slotCount) { // enough, check if broken
                        if (neededCells > height || cells[i].columnIndex == initColumnIndex) { // no broken
                            return cells.subList(cellIndex,
                                                 cellIndex + neededCells)
                        } else { // start at new column
                            cellIndex = (initColumnIndex + 1) * height
                        }
                        break
                    }
                } // end for
            }
            // add anyway
            val result = mutableListOf<Cell>()
            var remaining = slotCount
            while (remaining > 0) {
                if (!findEmptyCell()) return listOf()
                val cell = cells[cellIndex]
                remaining -= cell.room
                result.add(cell)
                cell.occupied = true
            }
            return result
        }

        fun findEmptyCell(): Boolean {
            while (cellIndex < cells.size) {
                if (!cells[cellIndex].occupied) return true
                cellIndex++
            }
            if (allowBroken) return false
            allowBroken = true
            cellIndex = 0
            return findEmptyCell()
        }

        fun apply(): List<Pair<ItemType, List<Int>>> = // itemType, slotIndices
            slotCountPairList.mapIndexed { index, (itemType, _) ->
                itemType to eachCellsList[index].flatMap { it.slotIndices }
            }

        inner class Cell(val room: Int,
                         val rowIndex: Int,
                         val columnIndex: Int) {
            var occupied = false
            val index
                get() = columnIndex * height + rowIndex
            val slotX
                get() = columnWidths.take(columnIndex).sum()
            val slotY
                get() = rowIndex
            val slotIndex
                get() = slotY * width + slotX
            val slotIndices
                get() = slotIndex until slotIndex + room
        }

        fun List<Cell>.connected(): Boolean = this.map { it.rowIndex to it.columnIndex }.connected()
    }
}

private fun Collection<Pair<Int, Int>>.connected(): Boolean { // not disconnected
    if (isEmpty()) return true
    val active = this.toMutableSet()
    val queue = mutableSetOf(active.first())
    while (queue.isNotEmpty()) {
        val point = queue.first().also { queue.remove(it) }
        if (point in active) {
            active.remove(point)
            queue.add(point.copy(first = point.first - 1))
            queue.add(point.copy(first = point.first + 1))
            queue.add(point.copy(second = point.second - 1))
            queue.add(point.copy(second = point.second + 1))
        }
    }
    return active.isEmpty()
}
