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

package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.alias.inventory.CraftingInventory
import org.anti_ad.mc.alias.screen.Container
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.ipnext.container.versionSpecificContainerTypes
import org.anti_ad.mc.ipnext.ingame.`(inventory)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.ContainerType.*

val nonStorage = setOf(TEMP_SLOTS)

val playerOnly = setOf(PURE_BACKPACK,
                       PLAYER,
                       CRAFTING)
val simple = setOf(PURE_BACKPACK)

object ContainerTypes {

    fun addContainersSource(source: () -> Array<out Pair<Class<*> ,Set<ContainerType>>>) {
        sources.add(source)
    }

    private val sources = mutableListOf<() -> Array<out Pair<Class<*> ,Set<ContainerType>>>>()

    private val innerMap = mutableMapOf<Class<*>, Set<ContainerType>>()
    private val outerMap = mutableMapOf<Class<*>, Set<ContainerType>>()

    @JvmStatic
    fun init() {
        sources.forEach {
            val s = it()
            register(*s)
        }
        //register(*versionSpecificContainerTypes)
    }

    init {
        addContainersSource {
            versionSpecificContainerTypes
        }
        init()
    }

    fun reset() {
        outerMap.clear()
        innerMap.clear()
        init()
    }

    private val <T: Container> T.unknownContainerDefaultTypes : Set<ContainerType>
        get() {

/*
            val res = mutableSetOf(SORTABLE_STORAGE, RECTANGULAR, WIDTH_9)
            this.`(slots)`.find {
                it.`(inventory)`.javaClass === CraftingInventory::class.java
            }?.let {
                res.add(CRAFTING)
            }
*/
            return setOf(SORTABLE_STORAGE, RECTANGULAR, WIDTH_9)
        }

    fun deregister(containerClass: Class<*>) {
        outerMap.remove(containerClass)
    }

    private fun register(containerClass: Class<*>,
                         types: Set<ContainerType>,
                         external: Boolean = false) {
        if (external) {
            outerMap[containerClass] = outerMap.getOrDefault(containerClass,
                                                             setOf()) + types
        } else {
            innerMap[containerClass] = innerMap.getOrDefault(containerClass,
                                                             setOf()) + types
        }
    }

    private fun register(vararg entries: Pair<Class<*>, Set<ContainerType>>) {
        entries.forEach {
            register(it.first,
                     it.second)
        }
    }

    fun Set<ContainerType>.match(with: Set<ContainerType> = setOf(),
                                 without: Set<ContainerType> = setOf()) =
        this.containsAll(with) && without.all { it !in this }

    private fun getRepresentingClass(container: Container): Class<*>? {
        if (outerMap.containsKey(container.javaClass)) {
            return container.javaClass
        }
        outerMap.keys.forEach {
            if (it.isInstance(container)) {
                return it
            }
        }
        if (innerMap.containsKey(container.javaClass)) {
            //Log.trace("Class: ${container.javaClass.name} is in as top level")
            return container.javaClass
        }
        return innerMap.keys.firstOrNull {
            //Log.trace("Checking inherited class: ${it.name}")
            it.isInstance(container)
        }
    }

    fun getTypes(container: Container): Set<ContainerType> {

        val v: Set<ContainerType>
        if (container.`(slots)`.isEmpty()) {
            v = nonStorage
        } else {
            //Log.trace("container.slots.size: ${container.`(slots)`.size}")
            val z: Class<*>? = getRepresentingClass(container)
            val ignoredClass = if (HintsManagerNG.getHints(container.javaClass).ignore) container.javaClass else null
            val playerSideOnly = HintsManagerNG.isPlayerSideOnly(container.javaClass)
            //Log.trace("Representing class: ${z?.name}")
            if (z == null) {
                if (ignoredClass == null) {
                    if (!playerSideOnly) {
                        v = container.unknownContainerDefaultTypes
                        register(container.javaClass, v, true)
                    } else {
                        v = playerOnly
                        register(container.javaClass, v, true)
                    }
                } else {
                    v = nonStorage
                    register(ignoredClass, v, true)
                }
            } else if (ignoredClass != null && z != ignoredClass) {
                v = nonStorage
                register(ignoredClass, v, true)
            } else {
                v =  outerMap[z] ?: innerMap[z] ?: container.unknownContainerDefaultTypes
            }
        }
        return v
    }



//  fun match(container: Container, vararg with: ContainerType) = match(container, with.toSet())
//  fun match(container: Container, with: Set<ContainerType> = setOf(), without: Set<ContainerType> = setOf()) =
//    getTypes(container).match(with, without)
}

enum class ContainerType {
    PLAYER,
    CREATIVE,

    PURE_BACKPACK, // which press 'e' to open

    TEMP_SLOTS,
    NO_SORTING_STORAGE, // sorting should be disabled on this container

    SORTABLE_STORAGE, // slots that purpose is storing any item (e.g. crafting table / furnace is not the case)
    HORSE_STORAGE, // first two slot is not item storage
    CRAFTING,
    STONECUTTER,
    TRADER,
    ANVIL,

    RECTANGULAR,
    WIDTH_9,
    HEIGHT_3,

    CUSTOM
}
