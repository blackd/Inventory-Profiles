package io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator

interface DiffCalculatorUtil {

//  /*
//    compare   [....]
//                   [....]
//   */
//  fun <T : Comparable<T>> compareRange(a: ClosedRange<T>, b: ClosedRange<T>): Int? {
//    if (a.isEmpty() || b.isEmpty()) return null
//    if (a.endInclusive <= b.start) return -1
//    if (b.endInclusive <= a.start) return 1
//  }


  fun estimateClickCountValueSingleSlot(from: Int, to: Int): Int {
    if (from == to) return 0
    if (to - from == 1 || to * 2 == from || to * 2 + 1 == from) return 1
    return 2
  }

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

