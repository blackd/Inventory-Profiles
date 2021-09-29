package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Items
import org.anti_ad.mc.common.vanilla.alias.items.ArmorItem
import org.anti_ad.mc.common.vanilla.alias.items.AxeItem
import org.anti_ad.mc.common.vanilla.alias.items.HoeItem
import org.anti_ad.mc.common.vanilla.alias.items.PickaxeItem
import org.anti_ad.mc.common.vanilla.alias.items.ShovelItem
import org.anti_ad.mc.common.vanilla.alias.items.SwordItem
import org.anti_ad.mc.common.vanilla.alias.items.ToolItem
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.ThresholdUnit.ABSOLUTE
import org.anti_ad.mc.ipnext.config.ThresholdUnit.PERCENTAGE
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.ingame.vMainhandIndex
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.comparablePotionEffects
import org.anti_ad.mc.ipnext.item.durability
import org.anti_ad.mc.ipnext.item.hasPotionEffects
import org.anti_ad.mc.ipnext.item.isBucket
import org.anti_ad.mc.ipnext.item.isDamageable
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isStew
import org.anti_ad.mc.ipnext.item.maxDamage
import org.anti_ad.mc.ipnext.item.rule.file.RuleFileRegister
import org.anti_ad.mc.ipnext.item.rule.native.compareByMatch
import org.anti_ad.mc.ipnext.item.rule.parameter.Match

object AutoRefillHandler {
    fun pressingDropKey(): Boolean {
        return Vanilla.mc().options.keyDrop.isPressed
    }

    var screenOpening = false

    fun onTickInGame() {
        if (Vanilla.screen() != null || (ModSettings.DISABLE_FOR_DROP_ITEM.booleanValue && pressingDropKey())) {
            screenOpening = true
        } else if (VanillaUtil.inGame()) { //  Vanilla.screen() == null
            if (screenOpening) {
                screenOpening = false
                init() // close screen -> init
            }
            handleAutoRefill()
        }
    }

    fun onJoinWorld() {
        init()
    }

    fun init() {
        monitors.clear()
        val list = listOf(ItemSlotMonitor { 36 + vMainhandIndex() }, // main hand inv 0-8
                          ItemSlotMonitor(45) // offhand inv 40
        ) + if (!ModSettings.REFILL_ARMOR.booleanValue) listOf() else
            listOf(ItemSlotMonitor(5), // head inv 39
                   ItemSlotMonitor(6), // chest inv 38
                   ItemSlotMonitor(7), // legs inv 37
                   ItemSlotMonitor(8), // feet inv 36
            )
        list[0].anothers += list[1]
        list[0].anothers += list.drop(2) // + armor to main hand
        list[1].anothers += list[0]
        list[1].anothers += list.drop(2) // + armor to off hand
        monitors.addAll(list)
    }

    val monitors = mutableListOf<ItemSlotMonitor>()

    // fixed ~.~ [later fun change reminder: see if auto refill fail if item ran out then instantly pick up some items]
    fun handleAutoRefill() {
        tryCatch { // just in case (index out of range etc)
            monitors.forEach { it.updateCurrent() }
            monitors.forEach { it.checkShouldHandle() }
            monitors.forEach { it.checkHandle() }
        }
    }

    class ItemSlotMonitor(val slotId: () -> Int) {
        constructor(slotId: Int) : this({ slotId })

        val anothers = mutableListOf<ItemSlotMonitor>() // item may swap with another slot

        var storedItem = ItemStack.EMPTY
        var storedSlotId = -1
        var tickCount = 0

        var lastTickItem = ItemStack.EMPTY
        var currentItem = ItemStack.EMPTY
        var currentSlotId = -1

        fun updateCurrent() {
            lastTickItem = currentItem
            currentSlotId = slotId()
            currentItem = Vanilla.playerContainer().`(slots)`[currentSlotId].`(itemStack)`
        }

        var shouldHandle = false

        fun checkShouldHandle() {
            shouldHandle = currentSlotId == storedSlotId && !isSwapped() && shouldHandleItem()
        }

        fun checkHandle() {
            if (shouldHandle) {
                if (tickCount >= ModSettings.AUTO_REFILL_WAIT_TICK.integerValue) {
                    // do handle
                    handle()
                    updateCurrent()
                    unhandled() // update storedItem
                    LockedSlotKeeper.init()
                } else {
                    // wait and return
                    tickCount++
                    return
                }
            } else {
                unhandled()
            }
        }


        // ============
        // inner
        // ============
        private fun isSwapped(): Boolean { // check this current == other lastTick and other current == this lastTick
            if (currentItem == lastTickItem) return false
            return anothers.any { another ->
                this.currentItem == another.lastTickItem
                        && this.lastTickItem == another.currentItem
            }
        }

        private fun unhandled() {
            storedItem = currentItem
            storedSlotId = currentSlotId
            tickCount = 0
        }

        private fun handle() {
            // find same type with stored item in backpack
            GeneralInventoryActions.cleanCursor()
            val foundSlotId = findCorrespondingSlot(checkingItem)
            foundSlotId ?: return
            if ((storedSlotId - 36) in 0..8) { // use swap
                //handles hotbar
                ContainerClicker.swap(foundSlotId,
                                      storedSlotId - 36)
            } else {
                //handles offhand and armor slots
                ContainerClicker.leftClick(foundSlotId)
                ContainerClicker.leftClick(storedSlotId)
                if (!vCursorStack().isEmpty()) {
                    ContainerClicker.leftClick(foundSlotId) // put back
                }
            }
        }

        var checkingItem = storedItem // use to select
        private fun shouldHandleItem(): Boolean {
            checkingItem = storedItem
            if (storedItem.isEmpty()) return false // nothing become anything
            if (currentItem.isEmpty()) return true // something become nothing
            val itemType = currentItem.itemType
            if (itemType.isDamageable) {
                if (ModSettings.REFILL_BEFORE_TOOL_BREAK.booleanValue) {
                    val threshold = getThreshold(itemType)
                    if (itemType.durability <= threshold) return true.also { checkingItem = currentItem }
                }
            }
            if (storedItem.itemType.isBucket) return false
            // todo potion -> bottle, soup -> bowl etc
            if (storedItem.itemType.item == Items.POTION
                && currentItem.itemType.item == Items.GLASS_BOTTLE
            ) return true
            if (storedItem.itemType.isStew
                && currentItem.itemType.item == Items.BOWL
            ) return true
            // todo any else?

            return false
        }

        companion object {
            private fun findCorrespondingSlot(checkingItem: ItemStack): Int? { // for stored item
                // ============
                // vanillamapping code depends on mappings
                // ============
                // found slot id 9..35 (same inv)
//        val items = Vanilla.playerContainer().`(slots)`.slice(9..35).map { it.`(itemStack)` }
                var filtered = Vanilla.playerContainer().let { playerContainer ->
                    val slots = playerContainer.`(slots)`
                    with(AreaTypes) { playerStorage - lockedSlots }.getItemArea(playerContainer,
                                                                                slots)
                        .slotIndices.map {
                            IndexedValue(it - 9,
                                         slots[it].`(itemStack)`)
                        }
                }.asSequence()
                var index = -1
                val itemType = checkingItem.itemType
                if (itemType.isDamageable) {
                    val threshold =
                        if (ModSettings.REFILL_BEFORE_TOOL_BREAK.booleanValue) getThreshold(itemType) else -1
                    filtered = filtered.filter { it.value.itemType.run { isDamageable && durability > threshold } }
                    when (itemType.item) {
                        is ArmorItem -> {
                            filtered = filtered.filter {
                                val otherType = it.value.itemType
                                otherType.item is ArmorItem
                                        && otherType.item.slotType == itemType.item.slotType
                            }
                        }
                        is SwordItem -> {
                            filtered = filtered.filter { it.value.itemType.item is SwordItem }
                        }
                        is ShovelItem -> {
                            filtered = filtered.filter { it.value.itemType.item is ShovelItem }
                        }
                        is PickaxeItem -> {
                            filtered = filtered.filter { it.value.itemType.item is PickaxeItem }
                        }
                        is AxeItem -> {
                            filtered = filtered.filter { it.value.itemType.item is AxeItem }
                        }
                        is HoeItem -> {
                            filtered = filtered.filter { it.value.itemType.item is HoeItem }
                        }
                        is ToolItem -> {
                            filtered = filtered.filter { it.value.itemType.item is ToolItem }
                        }
                        else -> {
                            filtered = filtered.filter { it.value.itemType.item == itemType.item }
                        }
                    }
                    // find best tool match criteria
                } else if (checkingItem.itemType.hasPotionEffects) {
                    // find best potion match
                    val effectStr = checkingItem.itemType.comparablePotionEffects.map { it.effect }
                    filtered = filtered.filter {
                        it.value.itemType.comparablePotionEffects.map { it.effect }.containsAll(effectStr)
                    }
                } else {
                    // find item
                    filtered = filtered.filter { it.value.itemType.item == checkingItem.itemType.item }
                }
                filtered = filtered.sortedWith(Comparator<IndexedValue<ItemStack>> { a, b ->
                    val aType = a.value.itemType
                    val bType = b.value.itemType
                    compareByMatch(
                        aType,
                        bType,
                        { it.item == itemType.item },
                        Match.FIRST
                    ) // type match sort
                }.thenComparator { a, b ->
                    val aType = a.value.itemType
                    val bType = b.value.itemType
                    bType.maxDamage - aType.maxDamage // material sort
                }.thenComparator { a, b ->
                    val aType = a.value.itemType
                    val bType = b.value.itemType
                    RuleFileRegister.getCustomRuleOrEmpty("auto_refill_best").compare(aType,
                                                                                      bType)
                }.thenComparator { a, b ->
                    b.value.count - a.value.count
                })
                index = filtered.firstOrNull()?.index ?: -1 // test // todo better coding
                return index.takeIf { it >= 0 }?.plus(9)
            }

            private fun getThreshold(itemType: ItemType): Int {
                if (!itemType.isDamageable) return 0
                return when (ModSettings.THRESHOLD_UNIT.value) {
                    ABSOLUTE -> ModSettings.TOOL_DAMAGE_THRESHOLD.integerValue
                    PERCENTAGE -> ModSettings.TOOL_DAMAGE_THRESHOLD.integerValue * itemType.maxDamage / 100
                }.coerceAtLeast(0)
            }
        }
    }

}