package org.anti_ad.mc.common.extensions

// ============
// Enum Extensions
// ============

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

inline fun <T> T.runIf(condition: Boolean, block: T.() -> T): T = if (condition) run(block) else this
inline fun <T> T.letIf(condition: Boolean, block: (T) -> T): T = if (condition) let(block) else this
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T = if (condition) apply(block) else this
inline fun <T> T.alsoIf(condition: Boolean, block: (T) -> Unit): T = if (condition) also(block) else this

// [self ternary] a.run { if (cond) this else b }
// > cond [not depends] on a, b [depends] on a
//  *  <==> a.runIf(!cond) { b }
//     <==> a be { b } ifIt !cond
// > cond [depends] on a, b [not depends] on a
//     <==> a.takeIf { it.cond } ?: b
//  *  <==> a.ifIt { cond } ?: b
//     <==> a be b ifIt { !cond }                 // b evaluated
// > cond [not depends] on a, b [not depends] on a
//  *  <==> if (cond) a else b
//     <==> a be b ifIt !cond                     // b evaluated
// > cond [depends] on a, b [depends] on a
//     <==> a.run { if (cond) this else b }
//     <==> a.selfIf { cond orElse b }            // b evaluated
//     <==> a.selfIf { cond orElse { b } }
//     <==> a be { b } ifIt { !cond }
//     <==> a.runIf { !cond then b }              // b evaluated
//     <==> a.runIf { !cond then { b } }
//  *  <==> a.runIf({ !cond }) { b }

// like takeIf but receiver predicate
inline fun <T> T.ifIt(predicate: T.() -> Boolean): T? = if (this.predicate()) this else null
inline fun <T> T.unlessIt(predicate: T.() -> Boolean): T? = if (!this.predicate()) this else null

inline fun <T> T.runIf(condition: T.() -> Boolean, block: T.() -> T): T = if (condition()) run(block) else this
inline fun <T> T.letIf(condition: (T) -> Boolean, block: (T) -> T): T = if (condition(this)) let(block) else this
inline fun <T> T.applyIf(condition: T.() -> Boolean, block: T.() -> Unit): T = if (condition()) apply(block) else this
inline fun <T> T.alsoIf(condition: (T) -> Boolean, block: (T) -> Unit): T = if (condition(this)) also(block) else this

// [self ternary equals] a.run { if (this == c) this else b }
//                       a.run { if (this != c) this else b }
// > b [depends] on a
//     <==> a.run { if (this == c) this else b }
//     <==> a.let { if (it == c) it else it.b }
//  *  <==> a.runIf({ this == c }) { b }
//     <==> a.letIf({ it == c }) { it.b }
//     <==> a.runIf(c::equals) { b }
//     <==> a.runIf(c::notEquals) { b }
//     <==> a.runIfEquals(c) { b }
// > b [not depends] on a
//  *  <==> a.takeIf { it == c } ?: b
//     <==> a.ifIt { this == c } ?: b
//     <==> a.ifIt { equals(c) } ?: b
//     <==> a.ifIt(c::equals) ?: b
//     <==> a.ifIt(c::notEquals) ?: b
//     <==> a.ifItEquals(c) ?: b
//     <==> a.ifItNotEquals(c) ?: b

//fun Any.notEquals(other: Any?): Boolean = this != other

// ============
// Boolean Extensions
// ============

// boolean.also { if (it) block() } <==> boolean.ifTrue { block() }
inline fun Boolean.ifTrue(block: () -> Unit) = also { if (this) block() }
inline fun Boolean.ifFalse(block: () -> Unit) = also { if (!this) block() }

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
