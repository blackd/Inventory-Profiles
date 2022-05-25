/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("NOTHING_TO_INLINE",
               "FunctionName",
               "SpellCheckingInspection")

package org.anti_ad.mc.common.extensions

infix fun Int.mod(other: Int) = Math.floorMod(this,
                                              other)

infix fun Long.mod(other: Long) = Math.floorMod(this,
                                                other)

fun Int.divCeil(other: Int) = (this + other - 1) / other
fun Long.divCeil(other: Long) = (this + other - 1) / other

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
