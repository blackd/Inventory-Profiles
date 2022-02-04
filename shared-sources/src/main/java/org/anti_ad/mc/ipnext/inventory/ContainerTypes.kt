package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.versionSpecificContainerTypes
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.ContainerType.*

val nonStorage = setOf(TEMP_SLOTS)

val playerOnly = setOf(PURE_BACKPACK,
                               PLAYER,
                               CRAFTING)

object ContainerTypes {

    private val innerMap = mutableMapOf<Class<*>, Set<ContainerType>>()
    private val outerMap = mutableMapOf<Class<*>, Set<ContainerType>>()

    @JvmStatic
    fun init() {
        register(*versionSpecificContainerTypes)
    }

    init {
        init()
    }

    fun reset() {
        outerMap.clear()
        innerMap.clear()
        init()
    }

    private val unknownContainerDefaultTypes : Set<ContainerType>
        get() {
            if (GuiSettings.TREAT_UNKNOWN_SCREENS_AS_CONTAINERS.booleanValue) {
                return setOf(SORTABLE_STORAGE, RECTANGULAR, WIDTH_9)
            }
            return nonStorage
        }

    fun deregister(containerClass: Class<*>) {
        outerMap.remove(containerClass)
    }

    fun register(containerClass: Class<*>,
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
            Log.trace("Class: ${container.javaClass.name} is in as top level")
            return container.javaClass
        }
        return innerMap.keys.firstOrNull {
            Log.trace("Checking inherited class: ${it.name}")
            it.isInstance(container)
        }
    }

    fun getTypes(container: Container): Set<ContainerType> {

        val v: Set<ContainerType>
        if (container.`(slots)`.isEmpty()) {
            v = nonStorage
        } else {
            Log.trace("container.slots.size: ${container.`(slots)`.size}")
            val z: Class<*>? = getRepresentingClass(container)
            val ignoredClass = if (HintsManagerNG.getHints(container.javaClass).ignore) container.javaClass else null
            val playerSideOnly = HintsManagerNG.isPlayerSideOnly(container.javaClass)
            Log.trace("Representing class: ${z?.name}")
            if (z == null) {
                if (ignoredClass == null) {
                    if (!playerSideOnly) {
                        v = unknownContainerDefaultTypes
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
                v =  outerMap[z] ?: innerMap[z] ?: unknownContainerDefaultTypes
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
    TRADER,

    RECTANGULAR,
    WIDTH_9,
    HEIGHT_3,

    CUSTOM
}
