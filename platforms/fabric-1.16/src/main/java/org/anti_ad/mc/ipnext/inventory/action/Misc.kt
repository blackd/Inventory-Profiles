package org.anti_ad.mc.ipnext.inventory.action

import org.anti_ad.mc.ipnext.inventory.data.ItemStat
import org.anti_ad.mc.ipnext.inventory.data.stat
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.maxCount
import kotlin.random.Random
import kotlin.random.nextInt

// old sorters

// DISTRIBUTE_EVENLY, SHUFFLE, FILL_ONE

private val random = Random

object RandFixedSum {
    // n number all < max whose total = sum
    // equals to constraint all min = 0 max = max
    fun randfixedsum(n: Int,
                     max: Int,
                     sum: Int): List<Int> { // TODO use randfixedsum
        // https://www.mathworks.com/matlabcentral/fileexchange/9700-random-vectors-with-fixed-sum
        require(sum <= n * max) { "sum > n * max" }
        if (n <= 0) {
            return listOf()
        }
        if (n == 1) {
            return listOf(sum)
        }
        val a = n / 2
        val b = n - a
        val aMinSum = (sum - b * max).coerceAtLeast(0)
        val aMaxSum = sum.coerceAtMost(a * max)
        val aSum = random.nextInt(aMinSum..aMaxSum) // randInc(aMinSum, aMaxSum)
        val aRes = randfixedsum(a,
                                max,
                                aSum)
        val bRes = randfixedsum(b,
                                max,
                                sum - aSum)
        return aRes + bRes
    }

    class Constraint(val min: Int,
                     val max: Int)

    fun randfixedsum(nums: List<Constraint>,
                     sum: Int): List<Int> {
        require(nums.totalMin <= sum && nums.totalMax >= sum) { "impossible sum" }
        if (nums.size <= 0) {
            return listOf()
        }
        if (nums.size == 1) {
            return listOf(sum)
        }
        val a = nums.subList(0,
                             nums.size / 2)
        val b = nums.subList(a.size,
                             nums.size)
        val aMinSum = (sum - b.totalMax).coerceAtLeast(a.totalMin)
        val aMaxSum = (sum - b.totalMin).coerceAtMost(a.totalMax)
        val aSum = random.nextInt(aMinSum..aMaxSum)
        val aRes = randfixedsum(a,
                                aSum)
        val bRes = randfixedsum(b,
                                sum - aSum)
        return aRes + bRes
    }

    private val List<Constraint>.totalMin
        get() = sumOf {  it.min }
    private val List<Constraint>.totalMax
        get() = sumOf { it.max }
}

fun List<Pair<ItemType, List<Int>>>.flatten(resultSize: Int? = null): List<ItemStack> { // itemType, itemCounts
    val a: List<ItemStack> = this.flatMap { (itemType, counts) ->
        counts.map {
            ItemStack(itemType,
                      it)
        }
    }
    if (resultSize == null) return a
    return a + List(resultSize - a.size) { ItemStack.EMPTY }
}

fun List<ItemStack>.spreadSlot(emptySlot: Int = 0): List<ItemStack> { // random distribute slot
    val stat = this.stat()
    val resultSlotCount = (this.size - emptySlot).coerceIn(stat.totalMinSlotCount,
                                                           stat.totalMaxSlotCount)
    val groupEntries = stat.groupEntries
    val constraints = groupEntries.map {
        RandFixedSum.Constraint(it.minSlotCount,
                                it.itemCount)
    }
    val slotCounts: List<Int> = RandFixedSum.randfixedsum(constraints,
                                                          resultSlotCount)
    // ok now know slot count for each item type (itemTypes zip slotCounts)
    val itemCountsForEachItemType: List<List<Int>> =
        groupEntries.zip(slotCounts) { entry, slotCount ->
            distributeMonotonic(entry.itemCount,
                                slotCount)
        }
    return (stat.itemTypes zip itemCountsForEachItemType).flatten(this.size)
}

private inline fun List<ItemStack>.writeEachItemType(itemCounts: (ItemStat.GroupEntry) -> List<Int>): List<ItemStack> {
    val stat = this.stat()
    val result = MutableList(this.size) { ItemStack.EMPTY }
    for (entry in stat.groupEntries) {
        itemCounts(entry).zip(entry.slotIndices) { count, index -> // ok now write to result
            result[index] = ItemStack(entry.itemType,
                                      count)
        }
    }
    return result
}

fun List<ItemStack>.spreadItemCount(): List<ItemStack> { // random distribute count
    return writeEachItemType { entry ->
        val constraint = RandFixedSum.Constraint(1,
                                                 entry.itemType.maxCount)
        val constraints = List(entry.slotCount) { constraint }
        return@writeEachItemType RandFixedSum.randfixedsum(constraints,
                                                           entry.itemCount)
    }
}

fun List<ItemStack>.distribute(): List<ItemStack> {
    return writeEachItemType { entry ->
        distribute(entry.itemCount,
                   entry.slotCount)
    }
}

fun List<ItemStack>.distributeMonotonic(): List<ItemStack> {
    return writeEachItemType { entry ->
        distributeMonotonic(entry.itemCount,
                            entry.slotCount)
    }
}

fun List<ItemStack>.fillOne(): List<ItemStack> {
    return writeEachItemType { entry ->
        fillOne(entry.itemCount,
                entry.itemType.maxCount,
                entry.slotCount)
    }
}

// return list of size [listSize], with sum equal to [sum]
// (9, 6) -> [1, 2, 1, 2, 1, 2]
fun distribute(sum: Int,
               listSize: Int): List<Int> =
    (0..listSize).map { sum * it / listSize }.zipWithNext { a, b -> b - a }

// (9, 6) -> [2, 2, 2, 1, 1, 1]
fun distributeMonotonic(sum: Int,
                        listSize: Int): List<Int> =
    List(sum % listSize) { sum / listSize + 1 } + List(listSize - sum % listSize) { sum / listSize }

// (30, 6, 7) -> [6, 6, 6, 6, 4, 1, 1]
fun fillOne(sum: Int,
            max: Int,
            listSize: Int): List<Int> {
    if (listSize * max == sum) return List(listSize) { max }
    val a = List((sum - listSize) / (max - 1)) { max }
    val b = listOf((sum - listSize) % (max - 1) + 1)
    return a + b + List(listSize - a.size - b.size) { 1 }
}

fun pack(sum: Int,
         max: Int): List<Int> {
    val a = List(sum / max) { max }
    if (sum % max == 0) return a
    return a + listOf(sum % max)
}
