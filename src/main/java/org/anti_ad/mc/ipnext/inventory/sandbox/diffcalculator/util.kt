package org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator

interface DiffCalculatorUtil {
  companion object : DiffCalculatorUtil

  fun calcRank(n: Int, g: Int): Int {
    if (n == g)         /**/ return 0
    if (n + 1 == g)     /**/ return 10 // have cur +1
    if (g == 0)         /**/ return 11 // no cur left click
    if (n / 2 == g)     /**/ return 12 // no cur right click
    if (n < g)          /**/ return 20
    // n > g
    // for rank 31 32 no cur left pick then have cur drop is also possible
    //                also two clicks only, we can't eliminate this possibility
    if (g == 1)         /**/ return 30 // no cur left then +1
    if (n / 2 + 1 == g) /**/ return 31 // no cur right then +1
    if (n / 4 == g)     /**/ return 32 // no cur right then no cur right
    if (n > g)          /**/ return 40
    throw AssertionError("unreachable")
  }

  fun clickCountLowerBound(n: Int, g: Int): Int {
    return when(calcRank(n, g) / 10) {
      0 -> 0
      1 -> 1
      2 -> 1
      3 -> 2
      4 -> 2
      else -> throw AssertionError("unreachable")
    }
  }

  fun clickCountUpperBound(n: Int, g: Int): Int {
    return when(calcRank(n, g) / 10) {
      0 -> 0
      1 -> 1
      2 -> g - n
      3 -> 2
      4 -> clickCountUpperBoundNGreaterThanG(n, g)
      else -> throw AssertionError("unreachable")
    }
  }

  private fun clickCountUpperBoundNGreaterThanG(n: Int, g: Int): Int { // no need to tailrec, i think
    return 1 + minOf(clickCountUpperBound(0, g), clickCountUpperBound(n / 2, g))
  }

//  /*
//    compare   [....]
//                   [....]
//   */
//  fun <T : Comparable<T>> compareRange(a: ClosedRange<T>, b: ClosedRange<T>): Int? {
//    if (a.isEmpty() || b.isEmpty()) return null
//    if (a.endInclusive <= b.start) return -1
//    if (b.endInclusive <= a.start) return 1
//  }


//  fun estimateClickCountValueSingleSlot(from: Int, to: Int): Int {
//    if (from == to) return 0
//    if (to - from == 1 || to * 2 == from || to * 2 + 1 == from) return 1
//    return 2
//  }

  fun clickCountSingleSlotToLess(from: Int, to: Int): Int{
    if (from < to) error("from < to")
    if (from == to) return 0
    val r = from / 2
    return 1 + if (r <= to) { // can right, rightThenRight
      to - r
    } else { // r > to, can't right, = leftThenRight
      to
    }
  }

  fun canRight(from: Int, to: Int): Boolean {
    if (from < to) error("from < to")
    return from / 2 <= to
  }
}

class SimpleClickCount : Comparable<SimpleClickCount> {
  val size
    get() = clicks.size
  val clicks = mutableListOf<SimpleClickEntry>()
  override fun compareTo(other: SimpleClickCount): Int {
    return size.compareTo(other.size)
  }
}

sealed class SimpleClickEntry

