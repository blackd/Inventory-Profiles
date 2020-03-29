package io.github.jsnimda.common.util

fun <T : Enum<T>> T.next(): T {
  val values = declaringClass.enumConstants
  return values[(ordinal + 1) % values.size]
}

fun <T : Enum<T>> T.previous(): T {
  val values = declaringClass.enumConstants
  return values[(ordinal + values.size - 1) % values.size]
}

fun <T> List<Comparable<T>>.compareTo(other: List<T>): Int {
  val it1 = this.iterator()
  val it2 = other.iterator()
  while (true) {
    if (!it1.hasNext() || !it2.hasNext()) {
      if (it1.hasNext()) return 1 // list 2 shorter than list 1
      if (it2.hasNext()) return -1 // list 2 longer than list 1
      return 0
    }
    it1.next().compareTo(it2.next()).let { result ->
      if (result != 0) return result
    }
  }
}

fun <T> List<Comparator<T>>.compare(a: T, b: T): Int {
  forEach { it.compare(a, b).let { result -> if (result != 0) return result } }
  return 0
}

// element is removed if action do not do non-local returns.
// at the end the list becomes empty
inline fun <T> MutableList<T>.consume(action: (T) -> Unit) = this.apply {
  val it = this.iterator()
  while (it.hasNext()) {
    action(it.next())
    it.remove()
  }
}

inline fun <T> MutableList<T>.consumeReversed(action: (T) -> Unit) =
  this.asReversed().consume(action)
