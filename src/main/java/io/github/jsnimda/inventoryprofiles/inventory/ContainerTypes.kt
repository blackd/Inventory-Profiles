package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.vanilla.*
import io.github.jsnimda.inventoryprofiles.inventory.VanillaContainerType.*

private val nonStorage = setOf(TEMP_SLOTS)
object ContainerTypes {
  private val innerMap = mutableMapOf<Class<*>, Set<ContainerType>>()

  init {
    register(
      PlayerContainer::class.java           to setOf(PLAYER_INVENTORY, PLAYER, CRAFTING),
      CreativeContainer::class.java         to setOf(PLAYER_INVENTORY, CREATIVE),

      EnchantingTableContainer::class.java  to nonStorage,
      AnvilContainer::class.java            to nonStorage,
      BeaconContainer::class.java           to nonStorage,
      CartographyTableContainer::class.java to nonStorage,
      GrindstoneContainer::class.java       to nonStorage,
      LecternContainer::class.java          to nonStorage,
      LoomContainer::class.java             to nonStorage,
      StonecutterContainer::class.java      to nonStorage,

      MerchantContainer::class.java         to setOf(TRADER),
      CraftingTableContainer::class.java    to setOf(CRAFTING),

      HopperContainer::class.java           to setOf(NO_SORTING_STORAGE),
      BrewingStandContainer::class.java     to setOf(NO_SORTING_STORAGE),
      AbstractFurnaceContainer::class.java  to setOf(NO_SORTING_STORAGE),

      GenericContainer::class.java          to setOf(ANY_ITEM_STORAGE, RECTANGULAR, WIDTH_9),
      ShulkerBoxContainer::class.java       to setOf(ANY_ITEM_STORAGE, RECTANGULAR, WIDTH_9),
      HorseContainer::class.java            to setOf(ANY_ITEM_STORAGE, RECTANGULAR, HEIGHT_3, HORSE_STORAGE),
      Generic3x3Container::class.java       to setOf(ANY_ITEM_STORAGE, RECTANGULAR, HEIGHT_3)

    )
  }

  private val unknownContainerDefaultTypes = setOf(ANY_ITEM_STORAGE, RECTANGULAR, WIDTH_9)

  fun register(containerClass: Class<*>, types: Set<ContainerType>) {
    innerMap[containerClass] = innerMap.getOrDefault(containerClass, setOf()) + types
  }

  fun register(vararg entries: Pair<Class<*>, Set<ContainerType>>) {
    entries.forEach { register(it.first, it.second) }
  }

  fun Set<ContainerType>.match(with: Set<ContainerType> = setOf(), without: Set<ContainerType> = setOf()) =
    this.containsAll(with) && without.all { it !in this }

  private fun getRepresentingClass(container: Container): Class<*>? {
    if (innerMap.containsKey(container.javaClass)) return container.javaClass
    return innerMap.keys.firstOrNull { it.isInstance(container) }
  }

  fun getTypes(container: Container) =
    innerMap.getOrDefault(getRepresentingClass(container), unknownContainerDefaultTypes)

  fun match(container: Container, vararg with: ContainerType) = match(container, with.toSet())
  fun match(container: Container, with: Set<ContainerType> = setOf(), without: Set<ContainerType> = setOf()) =
    getTypes(container).match(with, without)
}

interface ContainerType

enum class VanillaContainerType : ContainerType {
  PLAYER,
  CREATIVE,

  PLAYER_INVENTORY,

  TEMP_SLOTS,
  NO_SORTING_STORAGE, // sorting should be disabled on this container

  ANY_ITEM_STORAGE, // slots that purpose is storing any item (e.g. crafting table / furnace is not the case)
  HORSE_STORAGE, // first two slot is not item storage
  CRAFTING,
  TRADER,

  RECTANGULAR,
  WIDTH_9,
  HEIGHT_3,

}

fun ContainerTypes.isItemStorage(container: Container) = match(container, ANY_ITEM_STORAGE)
fun ContainerTypes.isHorseStorage(container: Container) = match(container, HORSE_STORAGE)