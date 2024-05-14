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

package org.anti_ad.mc.ipnext.util

import org.anti_ad.mc.ipnext.item.rule.CountSink
import org.anti_ad.mc.ipnext.item.rule.CountSource

interface Bucket<T: CountSink<T>>: CountSource<T> {
    val size: Int
    override fun count(element: T): Int
    fun contains(element: T): Boolean
    fun contains(element: T,
                 count: Int): Boolean

    fun containsAll(another: Bucket<T>): Boolean
    val elementSet: Set<T>
    val entrySet: Set<Map.Entry<T, Int>>
    val asMap: Map<T, Int>
    fun isEmpty(): Boolean
}

open class MutableBucket<T: CountSink<T>> protected constructor(innerMap: Map<T, Int>) : Bucket<T> { /* : MutableCollection<T> */
    constructor() : this(mapOf())

    protected open fun validateEmpty(element: T): Boolean { // determine whether the element can be added to this collection
        return false
    }

    private val innerMap = innerMap.toMutableMap()

    override val size: Int
        get() = innerMap.entries.sumOf { it.value }

    override fun count(element: T): Int {
        return innerMap.getOrDefault(element,
                                     0)
    }

    fun add(element: T) = add(element,
                              1)

    fun add(element: T,
            count: Int): Boolean {
        if (validateEmpty(element) || count <= 0) return false
        element.setCountSource(this)
        innerMap[element] = count(element) + count
        return true
    }

    override fun contains(element: T) = contains(element,
                                                 1)

    override fun contains(element: T,
                          count: Int): Boolean {
        return validateEmpty(element) || count(element) >= count
    }

    fun remove(element: T) = remove(element,
                                    1)

    fun remove(element: T,
               count: Int): Boolean {
        if (validateEmpty(element) || count <= 0) return false
        if (count(element) <= 0) return false
        innerMap[element] = count(element) - count
        if (count(element) <= 0) innerMap.remove(element)
        return true
    }

    fun addAll(another: Bucket<T>): Boolean {
        return another.entrySet.map {
            add(it.key,
                it.value)
        }.any { it }
    }

    override fun containsAll(another: Bucket<T>): Boolean {
        return another.entrySet.all {
            contains(it.key,
                     it.value)
        }
    }

    fun removeAll(another: Bucket<T>): Boolean {
        return another.entrySet.map {
            remove(it.key,
                   it.value)
        }.any { it }
    }

    override val elementSet: Set<T>
        get() = innerMap.keys
    override val entrySet: Set<Map.Entry<T, Int>>
        get() = innerMap.entries
    override val asMap: Map<T, Int>
        get() = innerMap

//  companion object {
//    fun <T> of(elements: Collection<T>): MutableBucket<T> {
//      return MutableBucket<T>().apply {
//        elements.forEach { add(it) }
//      }
//    }
//  }

    override fun isEmpty(): Boolean {
        return innerMap.isEmpty()
    }

    fun clear() {
        innerMap.clear()
    }

    open fun copyAsMutable(): MutableBucket<T> {
        return MutableBucket(innerMap)
    }

    // ============
    // equals
    // ============
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MutableBucket<*>) return false

        if (innerMap != other.innerMap) return false

        return true
    }

    final override fun hashCode(): Int {
        return innerMap.hashCode()
    }
}
