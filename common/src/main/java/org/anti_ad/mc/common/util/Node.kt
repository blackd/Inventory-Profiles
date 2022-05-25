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

package org.anti_ad.mc.common.util

import org.anti_ad.mc.common.Log

// two way associative node
// implement hierarchical structure
class Node<T>(val value: T) {
    private var mParent: Node<T>? = null
    var parent: Node<T>?
        get() = mParent
        set(value) {
            value?.add(this) ?: mParent?.remove(this)
        }

    private val mChildren = mutableSetOf<Node<T>>()
    val children: Set<Node<T>>
        get() = mChildren

    // deepContains: any ancestor (parent of parent) of child == this
    // ref: js Node.contains(), contains(self) returns true
    fun deepContains(child: Node<T>): Boolean {
        var c = child
        while (this != c) {
            c = c.mParent ?: return false
        }
        return true
    }

    fun add(child: Node<T>) {
        if (child.deepContains(this)) { // The new child element contains the parent.
            Log.error("The new child node contains the parent")
        } else { // continue even if child in mChildren (= move to the end)
            child.mParent?.remove(child)
            mChildren.add(child)
            child.mParent = this
        }
    }

    fun remove(child: Node<T>) {
        if (mChildren.remove(child)) {
            child.mParent = null
        }
    }

    fun dumpWidgetTree() {
        Log.trace {
            value.toString()
        }
        mChildren.forEach {
            Log.indent()
            it.dumpWidgetTree()
            Log.unindent()
        }
    }
}
