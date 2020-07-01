@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "SpellCheckingInspection")

package io.github.jsnimda.common.util

infix fun Int.mod(other: Int) = Math.floorMod(this, other)
infix fun Long.mod(other: Long) = Math.floorMod(this, other)

// as kotlin or/and is really misleading in bitwise flags operation
inline infix fun Int.`|`(other: Int) = this.or(other)
inline infix fun Int.lor(other: Int) = this.or(other)
inline infix fun Int.`&`(other: Int) = this.and(other)
inline infix fun Int.land(other: Int) = this.and(other)
inline fun Int.lnot() = this.inv()

inline infix fun Long.`|`(other: Long) = this.or(other)
inline infix fun Long.lor(other: Long) = this.or(other)
inline infix fun Long.`&`(other: Long) = this.and(other)
inline infix fun Long.land(other: Long) = this.and(other)
inline fun Long.lnot() = this.inv()

val Int.ordinalName: String // 1 -> 1st, 11 -> 11th etc
  get() {
    return this.toString() + when (this % 100) {
      11, 12, 13 -> "th"
      else -> when (this % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
      }
    }
  }
