package io.github.jsnimda.common.util

fun <T : Enum<T>> T.previous(amount: Int = 1) = next(-amount)
fun <T : Enum<T>> T.next(amount: Int = 1): T {
  val values = declaringClass.enumConstants
  return values[(ordinal + amount) mod values.size]
}

// ============
// Class Extensions
// ============

//val <T : Any> Class<T>.primitiveType
//  get() = this.kotlin.javaPrimitiveType
//val <T : Any> Class<T>.objectType
//  get() = this.kotlin.javaObjectType

val Class<*>.usefulName // as simpleName sometimes return "" which is useless
  get() = name.substringAfterLast('.')

// ============
// Scope Functions with if else
// ============
// (let, run, with, apply, also)

// hints:
// [null ternary] a.let { if (it == null) b else c } <==> a?.let { c } ?: b
// [self ternary] a.run { if (cond) this else b } <==> a.selfIf { cond orElse b }

inline fun <T : Any> T.selfIf(block: T.() -> T?): T = block() ?: this
infix fun <T : Any> Boolean.orElse(elseValue: T): T? = orElse { elseValue }
inline infix fun <T : Any> Boolean.orElse(elseValue: () -> T): T? =
  if (this) null else elseValue()

fun <T> T.selfIfEquals(value: T, elseValue: T) = selfIfEquals(value) { elseValue }
inline fun <T> T.selfIfEquals(value: T, elseValue: T.() -> T): T =
  if (this == value) this else elseValue()

fun <T> T.selfIfNotEquals(value: T, elseValue: T) = selfIfNotEquals(value) { elseValue }
inline fun <T> T.selfIfNotEquals(value: T, elseValue: T.() -> T): T =
  if (this != value) this else elseValue()

// ============
// Boolean Extensions
// ============

// boolean.also { if (it) block() } <==> boolean.ifTrue { block() }
inline fun Boolean.ifTrue(block: () -> Unit) = also { if (this) block() }
inline fun Boolean.ifFalse(block: () -> Unit) = also { if (!this) block() }

// ============
// Collections
// ============

fun <T> Iterable<T>.containsAny(collection: Iterable<T>): Boolean =
  collection.any { this.contains(it) }

fun <T> List<T>.indexed(): List<IndexedValue<T>> =
  mapIndexed { index, value -> IndexedValue(index, value) }

//fun <T : Any> List<T?>.indexedUnlessNull(): List<IndexedValue<T>?> =
//  mapIndexed { index, value -> value?.let { IndexedValue(index, value) } }
//
//fun <T : Any> List<T?>.indexedNotNull(): List<IndexedValue<T>> =
//  mapIndexedNotNull { index, value -> value?.let { IndexedValue(index, value) } }

// ============
// Bulk Comparisons
// ============

class AsComparable<T>(val value: T, val comparator: Comparator<T>) : Comparable<AsComparable<T>> {
  override fun compareTo(other: AsComparable<T>): Int = comparator.compare(value, other.value)
}

fun <T> T.asComparable(comparator: Comparator<T>) = AsComparable(this, comparator)

fun <T : Comparable<T>> List<T>.asComparable() = asComparable(Comparator(::compareList))
fun <T : Comparable<T>> List<T>.compareTo(other: List<T>): Int = compareList(this, other)
private fun <T : Comparable<T>> compareList(a: List<T>, b: List<T>): Int {
  for (i in 0 until minOf(a.size, b.size)) {
    a[i].compareTo(b[i]).let { if (it != 0) return it }
  } // list 2 shorter than list 1 => 1 else longer => -1
  return a.size - b.size
}

fun <T> List<Comparator<T>>.asComparator() = Comparator<T>(::compare)
fun <T> List<Comparator<T>>.compare(a: T, b: T): Int {
  forEach { it.compare(a, b).let { result -> if (result != 0) return result } }
  return 0
}

// ============
// deplete
// ============

// like removeAll but allowing local returns to stop the while loop
//inline fun <T> MutableList<T>.deplete(action: (T) -> Boolean) = this.apply {
//  val it = this.iterator()
//  while (it.hasNext()) {
//    if (action(it.next())) {
//      it.remove()
//    }
//  }
//}

//inline fun <T> MutableList<T>.depleteReversed(action: (T) -> Boolean) =
//  this.asReversed().deplete(action)
