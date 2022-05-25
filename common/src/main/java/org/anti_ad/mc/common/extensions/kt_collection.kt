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

package org.anti_ad.mc.common.extensions

// ============
// list
// ============

inline fun <T, R, V> Iterable<T>.zipIndexed(other: Iterable<R>,
                                            transform: (index: Int, a: T, b: R) -> V): List<V> {
    return this.zip(other).mapIndexed { index, (a, b) ->
        transform(index,
                  a,
                  b)
    }
}

fun <T> List<T>.last(index: Int): T {
    return this[size - index]
}

fun <T> List<T>.lastOrNull(index: Int): T? {
    return this.getOrNull(size - index)
}

fun <T> Iterable<T>.containsAny(collection: Iterable<T>): Boolean =
    collection.any { this.contains(it) }

fun <T> List<T>.indexed(): List<IndexedValue<T>> =
//  mapIndexed { index, value -> IndexedValue(index, value) }
    withIndex().toList()

//fun <T : Any> List<T?>.indexedUnlessNull(): List<IndexedValue<T>?> =
//  mapIndexed { index, value -> value?.let { IndexedValue(index, value) } }
//
//fun <T : Any> List<T?>.indexedNotNull(): List<IndexedValue<T>> =
//  mapIndexedNotNull { index, value -> value?.let { IndexedValue(index, value) } }

// ============
// slice
// ============

fun <T> List<T>.slice(beginIndex: Int = 0,
                      endIndex: Int = size): List<T> {
    return sliceMirror(beginIndex,
                       endIndex).toList()
}

// allow negative index like javascript. this function won't throw IndexOutOfBoundsException
// return a sublist
fun <T> List<T>.sliceMirror(beginIndex: Int = 0,
                            endIndex: Int = size): List<T> {
    return sliceMirrorInternal(beginIndex,
                               endIndex,
                               ::subList,
                               ::listOf)
}

@JvmName("sliceMirrorT")
fun <T> MutableList<T>.sliceMirror(beginIndex: Int,
                                   endIndex: Int): MutableList<T> {
    return sliceMirrorInternal(beginIndex,
                               endIndex,
                               ::subList,
                               ::mutableListOf)
}

private inline fun <L : List<*>> L.sliceMirrorInternal(
    beginIndex: Int,
    endIndex: Int,
    subList: (Int, Int) -> L,
    emptyList: () -> L
): L {
    val a = if (beginIndex < 0) size + beginIndex else beginIndex
    val b = if (endIndex < 0) size + endIndex else endIndex
    if (a < 0 || b > size || a > b) return emptyList()
    return subList(a,
                   b)
}
