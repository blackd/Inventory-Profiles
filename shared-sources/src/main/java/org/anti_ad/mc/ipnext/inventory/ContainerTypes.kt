package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.integration.HintsManager
import org.anti_ad.mc.common.vanilla.alias.AbstractFurnaceContainer
import org.anti_ad.mc.common.vanilla.alias.AnvilContainer
import org.anti_ad.mc.common.vanilla.alias.BeaconContainer
import org.anti_ad.mc.common.vanilla.alias.BrewingStandContainer
import org.anti_ad.mc.common.vanilla.alias.CartographyTableContainer
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.CraftingTableContainer
import org.anti_ad.mc.common.vanilla.alias.CreativeContainer
import org.anti_ad.mc.common.vanilla.alias.EnchantingTableContainer
import org.anti_ad.mc.common.vanilla.alias.Generic3x3Container
import org.anti_ad.mc.common.vanilla.alias.GenericContainer
import org.anti_ad.mc.common.vanilla.alias.GrindstoneContainer
import org.anti_ad.mc.common.vanilla.alias.HopperContainer
import org.anti_ad.mc.common.vanilla.alias.HorseContainer
import org.anti_ad.mc.common.vanilla.alias.LecternContainer
import org.anti_ad.mc.common.vanilla.alias.LoomContainer
import org.anti_ad.mc.common.vanilla.alias.MerchantContainer
import org.anti_ad.mc.common.vanilla.alias.PlayerContainer
import org.anti_ad.mc.common.vanilla.alias.ShulkerBoxContainer
import org.anti_ad.mc.common.vanilla.alias.StonecutterContainer
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.ContainerType.*

private val nonStorage = setOf(TEMP_SLOTS)

private val playerOnly = setOf(PURE_BACKPACK,
                               PLAYER,
                               CRAFTING)

object ContainerTypes {

    private val innerMap = mutableMapOf<Class<*>, Set<ContainerType>>()
    private val outerMap = mutableMapOf<Class<*>, Set<ContainerType>>()

    init {
        register(
            PlayerContainer::class.java           /**/ to playerOnly,
            CreativeContainer::class.java         /**/ to setOf(PURE_BACKPACK,
                                                                CREATIVE),

            EnchantingTableContainer::class.java  /**/ to nonStorage,
            AnvilContainer::class.java            /**/ to nonStorage,
            BeaconContainer::class.java           /**/ to nonStorage,
            CartographyTableContainer::class.java /**/ to nonStorage,
            GrindstoneContainer::class.java       /**/ to nonStorage,
            LecternContainer::class.java          /**/ to nonStorage,
            LoomContainer::class.java             /**/ to nonStorage,
            StonecutterContainer::class.java      /**/ to nonStorage,

            MerchantContainer::class.java         /**/ to setOf(TRADER),
            CraftingTableContainer::class.java    /**/ to setOf(CRAFTING),

            HopperContainer::class.java           /**/ to setOf(NO_SORTING_STORAGE),
            BrewingStandContainer::class.java     /**/ to setOf(NO_SORTING_STORAGE),
            AbstractFurnaceContainer::class.java  /**/ to setOf(NO_SORTING_STORAGE),

            GenericContainer::class.java          /**/ to setOf(SORTABLE_STORAGE,
                                                                RECTANGULAR,
                                                                WIDTH_9),
            ShulkerBoxContainer::class.java       /**/ to setOf(SORTABLE_STORAGE,
                                                                RECTANGULAR,
                                                                WIDTH_9),
            HorseContainer::class.java            /**/ to setOf(SORTABLE_STORAGE,
                                                                RECTANGULAR,
                                                                HEIGHT_3,
                                                                HORSE_STORAGE),
            Generic3x3Container::class.java       /**/ to setOf(SORTABLE_STORAGE,
                                                                RECTANGULAR,
                                                                HEIGHT_3)

        )
    }

    private val unknownContainerDefaultTypes : Set<ContainerType>
        get() {
            if (GuiSettings.TREAT_UNKNOWN_SCREENS_AS_CONTAINERS.booleanValue) {
                return setOf(SORTABLE_STORAGE, RECTANGULAR, WIDTH_9)
            }
            return nonStorage
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
            val ignoredClass = HintsManager.getIgnoredClass(container.javaClass)
            val playerSideOnly = HintsManager.isForcePlayerSide(container.javaClass)
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
