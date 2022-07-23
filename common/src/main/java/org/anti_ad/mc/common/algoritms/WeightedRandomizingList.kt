/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.common.algoritms

import kotlin.random.Random



class WeightedRandomizingList<D: Comparable<D>>(private val noiseSource: Random) {

    inner class Node<D> constructor(override val data: D,
                                    aWeight: Int,
                                    var pos: Int): WeightedNode<D> {


        override var weight: Int = aWeight
            set(value) {
                field = value
                if (index > -1) {
                    adjust(index)
                }
            }

        var index: Int = -1

        val end
            get() = pos + weight


        init {
            weight = aWeight
        }

    }

    private val nodeList: MutableList<Node<D>> = mutableListOf()

    private var sortedIsDirty = true
    private val sortedByValue: MutableList<Node<D>> = mutableListOf()

    private var iSize: Int = 0

    private var iWeight: Int = 0

    val size: Int
        get() {
            return iSize
        }

    val length: Int
        get() {
            return nodeList.lastOrNull()?.end ?: 0
        }


    fun add(data: D, weight: Int): WeightedNode<D> {
        val pos = if (nodeList.size == 0) 0 else nodeList.last().end
        val newNode = Node(data, weight, pos)
        nodeList.add(newNode)
        newNode.index = nodeList.size - 1
        iWeight = newNode.end
        sortedByValue.add(newNode)
        sortedIsDirty = true
        return newNode
    }

    private fun adjust(from: Int) {
        var prev = nodeList[from].end
        for (i in from + 1 until nodeList.size) {
            nodeList[i].let { moving ->
                moving.pos = prev
                prev = moving.end
            }
        }
    }

    fun remove(data: D) {
        if (sortedIsDirty) {
            sortedByValue.sortBy {
                it.data
            }
            sortedIsDirty = false
        }
        sortedByValue.binarySearch {
            it.data.compareTo(data)
        }.let {
            if (it >= 0) {
                val node = sortedByValue.removeAt(it)
                var prev = node.pos
                for (i in node.index + 1 until nodeList.size) {
                    nodeList[i].let { moving ->
                        moving.pos = prev
                        prev = moving.end
                    }
                }
                nodeList.removeAt(node.index)
            }
        }

    }

    fun getWeightedRandomElement(pos: Int = noiseSource.nextInt(length)): D? {
        return if (pos >= 0 && pos < nodeList.last().end) {
            val index = nodeList.binarySearch {
                return@binarySearch if (pos >= it.pos && pos < it.end ) {
                    0
                } else if (it.end > pos) {
                    1
                } else {
                    -1
                }
            }
            nodeList[index].data
        } else {
            null
        }

    }

}


fun main() {
    val maxItems = 10

    val r = Random(100)
    val l = WeightedRandomizingList<Int>(r)
    val listPrc = listOf(5, 10, 5, 30, 10, 5, 5, 10, 5, 15)
    val nodes = mutableListOf<WeightedNode<Int>>()
    for (i in 0 until maxItems) {
        nodes.add(l.add(i, r.nextInt(45001)))
    }


    val maxIterations = 1000000
    val maxIterationsD = 1000000.0
    val timesList = mutableListOf<Int>()

    for (i in 0 until maxItems) {
        timesList.add(0)
    }
    for (i in 0 until maxIterations) {
        val chosen : Int = l.getWeightedRandomElement(r.nextInt(l.length))!!
        timesList[chosen] = timesList[chosen] + 1
    }

    var sum = 0.0
    timesList.forEachIndexed {  i, value ->
        sum += value/maxIterationsD
        println("$i: %.2f%%".format(value/maxIterationsD*100))
    }
    println("sum: $sum")


    nodes[5].weight += 45000

    timesList.clear()
    for (i in 0 until maxItems) {
        timesList.add(0)
    }
    for (i in 0 until maxIterations) {
        val chosen : Int = l.getWeightedRandomElement(r.nextInt(l.length))!!
        timesList[chosen] = timesList[chosen] + 1
    }

    sum = 0.0
    timesList.forEachIndexed {  i, value ->
        sum += value/maxIterationsD
        println("$i: %.2f%%".format(value/maxIterationsD*100))
    }
    println("sum: $sum")
}
