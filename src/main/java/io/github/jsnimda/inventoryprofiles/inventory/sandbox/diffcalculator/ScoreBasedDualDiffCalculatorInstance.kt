package io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator

import io.github.jsnimda.common.Log
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemStat
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.stat
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator.SingleType.Button.LEFT
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator.SingleType.Button.RIGHT
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.toList
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.maxCount
import io.github.jsnimda.inventoryprofiles.util.MutableBucket

class ScoreBasedDualDiffCalculatorInstance(sandbox: ContainerSandbox, goalTracker: ItemTracker) :
  SimpleDiffCalculatorInstance(sandbox, goalTracker) {
  init {
    untilEqualsTypeOnly = true // until equal types or now empty
  }

  override fun run() {
    super.run()
    val clicks = sandbox.clickNode.toList()
    val lclick = clicks.count { it.button == 0 }
    val rclick = clicks.count { it.button == 1 }
    Log.debug("Stage A click count total ${sandbox.clickCount}. $lclick left. $rclick right.")
    runFinal()
  }

  /*
    rank 2 -> 1 -> 0
                   0  n == g
      (exactly 1)  1  n + 1 == g || n / 2 == g  [i.e. g * 2 == n || g * 2 + 1 == n] || g == 0
      (at least 1) 2  n < g
      (exactly 2)  3  n / 2 + 1 == g || (g == 1 && n > g) || n / 2 / 2 == g [i.e. n / 4 == g]
      (at least 2) 4  n > g

    |          .------------.
    | (4) -> (2) -> (1) -> (0)
    |  `---- (3) ---'

    drop rank priority (unused)
      2 -> 0  \
      2 -> 1   min -> exact
      4 -> 3  /
      1 -> 0  - exact -> exact
      3 -> 1  /
      4 -> 2  - min -> min
      2 -> 2
      4 -> 4

    score (unused)
      rank 2 prefer closer to goal
      rank 4 prefer closer to goal (?)
   */

  val statGoal: ItemStat = goalTracker.slots.stat()

  fun runFinal() { // stage b
    // all equals type, but cursor may not be empty
    if (!cursorNow.isEmpty()) doItemType(cursorNow.itemType)
    statGoal.itemTypes.forEach { doItemType(it) }
  }

  fun doItemType(itemType: ItemType) {
    val entry = statGoal.itemGroups.getValue(itemType)
    val indices = entry.slotIndices.filter { !CompareSlotDsl(it).equals }
    if (indices.isEmpty()) return
    val maxCount = itemType.maxCount
    val start = SingleType.Node().apply {
      c = cursorNow.count
      for (index in indices) {
        identities.add(CompareSlotDsl(index).toSlot())
      }
    }
    val clicks = SingleType.solve(start, maxCount)
    for (click in clicks) {
      val index = indices.firstOrNull { CompareSlotDsl(it).toSlot() == click.slot }
        ?: error("target slot ${click.slot} not found")
      CompareSlotDsl(index).run {
        when (click.button) {
          LEFT -> leftClick()
          RIGHT -> rightClick()
        }
      }
    }
  }

  fun CompareSlotDsl.toSlot(): SingleType.Slot {
    return SingleType.Slot(n, g)
  }
}

private const val MAX_LOOP = 100_000

object SingleType : DiffCalculatorUtil {
  val rankAfterAllowed = listOf(
    listOf(),         // rank 0
    listOf(0),        // rank 1
    listOf(0, 1, 2),  // rank 2
    listOf(1),        // rank 3
    listOf(2, 3, 4),  // rank 4
  )

  val nodeComparator = compareBy<Node> { it.fScore }.thenBy { it.upperTotal }

  fun solve(start: Node, maxCount: Int): List<Click> { // ref: A* algorithm
    val closedSet = mutableSetOf<Node>()
//    val openSet = mutableMapOf(start to start)
    val openSet = sortedMapOf(start to start)
    var minUpper = start.upperTotal
    var minLowerOfMinUpper = start.lowerTotal
    var loopCounter = 0
    while (openSet.isNotEmpty()) {
      if (++loopCounter > MAX_LOOP)
        error("Too many loop. $loopCounter > $MAX_LOOP")
      val x = openSet.firstKey() ?: break
      if (x.isGoal) {
        Log.trace("loopCounter $loopCounter")
        return constructClickPath(x)
      }
      if (x.lowerBound >= minUpper && minUpper > minLowerOfMinUpper)
        error("emmm (x.lowerBound >= minUpper && minUpper > minLowerOfMinUpper)")
      openSet.remove(x)
      closedSet.add(x)
      for (y in x.neighbor(maxCount)) {
        if (y in closedSet)
          continue
        if (y !in openSet || y.gScore < openSet.getValue(y).gScore) {
          openSet[y] = y
          if (y.upperTotal < minUpper) {
            minUpper = y.upperTotal
            minLowerOfMinUpper = y.lowerTotal
          } else if (y.upperTotal == minUpper && y.lowerBound < minLowerOfMinUpper) {
            minLowerOfMinUpper = y.lowerBound
          }
        }
      }
      // remove all lower bound >= min upperbound
//      var removedCount = 0
//      openSet.keys.removeAll {
//        (it.lowerBound >= minUpper && minUpper > minLowerOfMinUpper)
//          .ifTrue { closedSet.add(it); removedCount++ }
//      }
//      if (removedCount != 0)
//        Log.trace("removed $removedCount at $loopCounter")
    }
    error("solve failure")
  }

  fun constructClickPath(node: Node): List<Click> {
    return node.clickNode.toList()
  }

  enum class Button {
    LEFT, RIGHT
  }

  data class Slot(val n: Int, val g: Int) {
    val isGoal
      get() = n == g
    val rank
      get() = calcRank(n, g)

    fun click(c: Int, button: Button, maxCount: Int): Pair<Slot, Int> { // slot after, c after
      return when (c) {
        0 -> when (button) { // empty cur
          LEFT -> copy(n = 0) to n
          RIGHT -> copy(n = n / 2) to n - n / 2
        }
        else -> {
          val nAfter = when (button) { // has cur
            LEFT -> (n + c).coerceAtMost(maxCount)
            RIGHT -> n + 1
          }
          copy(n = nAfter) to c - (nAfter - n)
        }
      }
    }
  }

  class Node(val identities: MutableBucket<Slot> = MutableBucket()) : Comparable<Node> {
    var c = 0 // cursor

    val isGoal
      get() = c == 0 && identities.elementSet.all { it.isGoal }

    val gScore: Int
      get() = clickCount
    val hScore: Int
      get() = lowerBound
    val fScore: Int
      get() = gScore + hScore

    val lowerBound by lazy(LazyThreadSafetyMode.NONE) {
      identities.entrySet.sumBy { (slot, count) -> clickCountLowerBound(slot.n, slot.g) * count }
    }
    val upperBound by lazy(LazyThreadSafetyMode.NONE) {
      identities.entrySet.sumBy { (slot, count) -> clickCountUpperBound(slot.n, slot.g) * count }
    }

    fun neighbor(maxCount: Int): List<Node> {
      val result = mutableListOf<Node>()
      for (slot in identities.elementSet) {
        if (slot.isGoal) continue
        if (c == 0 && slot.n == 0) continue
        if (c != 0 && slot.n == maxCount) continue
        // try left
        copyByAddClick(slot, LEFT, maxCount)?.let { result.add(it) }
        // try right
        if (c != 1 && !(c == 0 && slot.n == 1)) // ban right if c == 1 or no cur and n == 1
          copyByAddClick(slot, RIGHT, maxCount)?.let { result.add(it) }
      }
      return result
    }

    val lowerTotal
      get() = clickCount + lowerBound
    val upperTotal
      get() = clickCount + upperBound

    val clickCount
      get() = clickNode?.clickIndex?.plus(1) ?: 0
    var clickNode: Click? = null
      private set

    private fun addClick(slot: Slot, button: Button) {
      Click(clickCount, slot, button, clickNode).also { clickNode = it }
    }

    // ============
    // copy
    // ============
    fun copy() = Node(identities.copyAsMutable()).also {
      it.c = this.c
      it.clickNode = this.clickNode
    }

    fun copyByAddClick(slot: Slot, button: Button, maxCount: Int): Node? {
      val rank = slot.rank
      val (slotAfter, cAfter) = slot.click(c, button, maxCount)
//      if (slotAfter.rank !in rankAfterAllowed[rank]) return null
      return copy().apply {
        addClick(slot, button)
        c = cAfter
        identities.remove(slot)
        identities.add(slotAfter)
      }
    }

    override fun compareTo(other: Node): Int {
      return nodeComparator.compare(this, other)
    }

    // ============
    // equals
    // ============

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Node) return false

      if (c != other.c) return false
      if (identities != other.identities) return false

      return true
    }

    val hashCode by lazy(LazyThreadSafetyMode.NONE, ::genHashCode)
    override fun hashCode(): Int {
      return hashCode
    }

    private fun genHashCode(): Int {
      var result = c
      result = 31 * result + identities.hashCode()
      return result
    }
  }

  // ref: SandboxClick
  data class Click(
    val clickIndex: Int,
    val slot: Slot,
    val button: Button,
    val previousClick: Click? = null
  )

  fun Click?.toList(): List<Click> {
    val list = mutableListOf<Click>()
    var click: Click? = this
    while (click != null) {
      list.add(click)
      click = click.previousClick
    }
    list.reverse()
    return list
  }
}
