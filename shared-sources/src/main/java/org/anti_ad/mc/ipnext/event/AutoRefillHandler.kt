package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Enchantments
import org.anti_ad.mc.common.vanilla.alias.Items
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.vanilla.alias.items.ArmorItem
import org.anti_ad.mc.common.vanilla.alias.items.AxeItem
import org.anti_ad.mc.common.vanilla.alias.items.HoeItem
import org.anti_ad.mc.common.vanilla.alias.items.PickaxeItem
import org.anti_ad.mc.common.vanilla.alias.items.ShovelItem
import org.anti_ad.mc.common.vanilla.alias.items.SwordItem
import org.anti_ad.mc.common.vanilla.alias.items.ToolItem
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.showSubTitle
import org.anti_ad.mc.common.vanilla.*
import org.anti_ad.mc.ipnext.config.AutoRefillSettings
import org.anti_ad.mc.ipnext.config.ThresholdUnit.ABSOLUTE
import org.anti_ad.mc.ipnext.config.ThresholdUnit.PERCENTAGE
import org.anti_ad.mc.ipnext.config.ToolReplaceVisualNotification
import org.anti_ad.mc.ipnext.ingame.`(equipmentSlot)`
import org.anti_ad.mc.ipnext.ingame.`(isPressed)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(keyDrop)`
import org.anti_ad.mc.ipnext.ingame.`(options)`
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
import org.anti_ad.mc.ipnext.item.customOrTranslatedName
import org.anti_ad.mc.ipnext.item.durability
import org.anti_ad.mc.ipnext.item.enchantments
import org.anti_ad.mc.ipnext.item.hasPotionEffects
import org.anti_ad.mc.ipnext.item.isBucket
import org.anti_ad.mc.ipnext.item.isDamageable
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isStew
import org.anti_ad.mc.ipnext.item.maxDamage
import org.anti_ad.mc.ipnext.item.rule.file.RuleFileRegister
import org.anti_ad.mc.ipnext.item.rule.natives.compareByMatch
import org.anti_ad.mc.ipnext.item.rule.parameter.Match

object AutoRefillHandler {

    private inline val pressingDropKey: Boolean
        get() = Vanilla.mc().`(options)`.`(keyDrop)`.`(isPressed)`

    var screenOpening = false

    fun onTickInGame() {
        if (Vanilla.screen() != null || (AutoRefillSettings.DISABLE_FOR_DROP_ITEM.booleanValue && pressingDropKey)) {
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
        ) + if (!AutoRefillSettings.REFILL_ARMOR.booleanValue) listOf() else
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

        private var lastNotifyDurability: Int = -1;
        private var lastNotifyBreakDurability: Int = -1;

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
                if (tickCount >= AutoRefillSettings.AUTO_REFILL_WAIT_TICK.integerValue) {
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
                this.currentItem == another.lastTickItem && this.lastTickItem == another.currentItem
            }
        }

        private fun unhandled() {
            storedItem = currentItem
            storedSlotId = currentSlotId
            tickCount = 0
            lastNotifyDurability = storedItem.itemType.durability
            lastNotifyBreakDurability = storedItem.itemType.durability
        }

        private fun handle() {
            // find same type with stored item in backpack
            GeneralInventoryActions.cleanCursor()
            val itemType = checkingItem.itemType
            val foundSlotId = findCorrespondingSlot(checkingItem)
            if (foundSlotId != null) {

                if (itemType.isDamageable) notifySuccessfulChange(itemType, foundSlotId)

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

            } else if (itemType.isDamageable) {
                notifyFailToChange()
            }
        }

        var checkingItem = storedItem // use to select
        private fun shouldHandleItem(): Boolean {
            checkingItem = storedItem


            if (storedItem.isEmpty()) return false // nothing become anything
            if (currentItem.isEmpty()) {
                return !(AutoRefillSettings.DISABLE_FOR_LOYALTY_ITEMS.value && storedItem.itemType.enchantments[Enchantments.LOYALTY] != null)
            }
            val itemType = currentItem.itemType
            if (itemType.isDamageable) {
                if (AutoRefillSettings.REFILL_BEFORE_TOOL_BREAK.booleanValue) {
                    if (!(AutoRefillSettings.ALLOW_BREAK_FOR_NON_ENCHANTED.value
                                && itemType.enchantments.isEmpty()
                                && itemType.maxDamage < AutoRefillSettings.TOOL_MAX_DURABILITY_THRESHOLD.value)) {
                        val threshold = getThreshold(itemType)

                        notifyDurabilityChange(itemType, itemType.durability, threshold)
                        if (itemType.durability <= threshold) return true.also { checkingItem = currentItem }
                    }
                }
            }

            if (storedItem.itemType.isBucket) return false
            // todo potion -> bottle, soup -> bowl etc
            if (storedItem.itemType.item == Items.POTION && currentItem.itemType.item == Items.GLASS_BOTTLE) return true
            if (storedItem.itemType.isStew && currentItem.itemType.item == Items.BOWL) return true
            // todo any else?

            return false
        }
        private fun notifySuccessfulChange(itemType: ItemType,
                                           foundSlotId: Int) {
            if (AutoRefillSettings.VISUAL_REPLACE_SUCCESS_NOTIFICATION.value) {
                val replacingWith = Vanilla.playerContainer().`(slots)`[foundSlotId].`(itemStack)`.itemType

                val message: (Boolean) -> String = {
                    val newl = if (it) {
                        """{"text": "\n"},"""
                    } else {
                        """{"text": " - ", "color": "#FFFFFF"},"""
                    }
                    """[
                           {"translate" : "inventoryprofiles.config.notification.tool_replace_ping.ipn", "color" : "#3584E4" },
                           $newl
                           {"text" : "\"${itemType.customOrTranslatedName}\" ", "color": "#FF8484"},
                           {"translate" : "inventoryprofiles.config.notification.tool_replace_success", "color": "#FFFFFF"},
                           {"text" : " \"${replacingWith.customOrTranslatedName}\"", "color": "#8484FF"}
                    ]"""
                }
                when (AutoRefillSettings.TYPE_VISUAL_REPLACE_SUCCESS_NOTIFICATION.value) {
                    ToolReplaceVisualNotification.SUBTITLE -> {
                        showSubTitle(TextSerializer.fromJson(message(false)));
                    }
                    ToolReplaceVisualNotification.HOTBAR   -> Vanilla.inGameHud().setOverlayMessage(TextSerializer.fromJson(message(false)),
                                                                                                    false)
                    ToolReplaceVisualNotification.CHAT     -> VanillaUtil.chat(TextSerializer.fromJson(message(true))!!)
                }
            }
            if (AutoRefillSettings.AUDIO_REPLACE_SUCCESS_NOTIFICATION.value) {
                Sounds.REFILL_STEP_NOTIFY.play(.2f)
                Sounds.REFILL_STEP_NOTIFY.play(1.5f, 5)
            }
        }

        private fun notifyFailToChange() {
            if (!currentItem.isEmpty() && (AutoRefillSettings.VISUAL_REPLACE_FAILED_NOTIFICATION.value || AutoRefillSettings.AUDIO_REPLACE_FAILED_NOTIFICATION.value)) {
                val itemType = currentItem.itemType
                val threshold = getThreshold(itemType)
                val durability = itemType.durability
                if (durability <= threshold) {
                    if (durability != lastNotifyBreakDurability) {
                        if (AutoRefillSettings.VISUAL_REPLACE_FAILED_NOTIFICATION.value) {
                            val message: (Boolean) -> String = {
                                val newl = if (it) {
                                    """{"text": "\n"},"""
                                } else {
                                    """{"text": " - ", "color": "#FFFFFF"},"""
                                }
                                """[
                                   {"translate" : "inventoryprofiles.config.notification.tool_replace_ping.ipn", "color" : "#3584E4" },
                                   {"translate": "inventoryprofiles.config.notification.tool_replace_ping.warning", "color" : "#FF8484"},
                                   $newl
                                   {"translate": "inventoryprofiles.config.notification.tool_replace_failed.replacing", "color" : "#E5A50A", "with": ["${itemType.customOrTranslatedName}"]}
                                   ]"""
                            }
                            when (AutoRefillSettings.TYPE_VISUAL_REPLACE_FAILED_NOTIFICATION.value) {
                                ToolReplaceVisualNotification.SUBTITLE -> {
                                    showSubTitle(TextSerializer.fromJson(message(false)))
                                }
                                ToolReplaceVisualNotification.HOTBAR   -> Vanilla.inGameHud().setOverlayMessage(TextSerializer.fromJson(message(false)),
                                                                                                                false)
                                ToolReplaceVisualNotification.CHAT     -> VanillaUtil.chat(TextSerializer.fromJson(message(true))!!)
                            }

                        }
                        if (AutoRefillSettings.AUDIO_REPLACE_FAILED_NOTIFICATION.value) {
                            Sounds.REFILL_STEP_NOTIFY.play(1.2f)
                            Sounds.REFILL_STEP_NOTIFY.play(1.5f, 5)
                        }
                    }
                }
            }
        }

        private fun notifyDurabilityChange(itemType: ItemType,
                                           durability: Int,
                                           threshold: Int) {
            if ((AutoRefillSettings.VISUAL_DURABILITY_NOTIFICATION.value
                        || AutoRefillSettings.AUDIO_DURABILITY_NOTIFICATION.value)
                && isItNotifyStep(durability, threshold)) {


                if (AutoRefillSettings.VISUAL_DURABILITY_NOTIFICATION.value) {
                    val message: (Boolean) -> String =  {
                        //                        {"translate": "inventoryprofiles.config.notification.tool_replace_ping.warning", "color" : "#FF8484"},
                        val newl = if (it) {
                            """{"text": "\n"},"""
                        } else {
                            """{"text": " - ", "color": "#FFFFFF"},"""
                        }
                        """[
                        {"text" : ""},
                        {"translate" : "inventoryprofiles.config.notification.tool_replace_ping.ipn", "color" : "#3584E4" },
                        $newl
                        {"translate": "inventoryprofiles.config.notification.tool_replace_ping.durability", "color" : "#E5A50A", "with": ["${itemType.customOrTranslatedName}","$durability"]},
                        {"translate": "inventoryprofiles.config.notification.tool_replace_ping.replacing", "color" : "#FF4545", "with": ["$threshold"]}
                        ]"""
                    }
                    when (AutoRefillSettings.TYPE_VISUAL_DURABILITY_NOTIFICATION.value) {
                        ToolReplaceVisualNotification.SUBTITLE -> {
                            showSubTitle(TextSerializer.fromJson(message(false)))
                        }
                        ToolReplaceVisualNotification.HOTBAR -> Vanilla.inGameHud().setOverlayMessage(TextSerializer.fromJson(message(false)), false)
                        ToolReplaceVisualNotification.CHAT -> VanillaUtil.chat(TextSerializer.fromJson(message(true))!!)
                    }

                }
                if (AutoRefillSettings.AUDIO_DURABILITY_NOTIFICATION.value) {
                    Sounds.REFILL_STEP_NOTIFY.play()
                }
            }
        }

        private fun isItNotifyStep(durability: Int,
                                   threshold: Int): Boolean {
            if (storedItem.itemType != lastTickItem.itemType) {
                lastNotifyDurability = -1
            }
            if (lastNotifyDurability != durability && durability != threshold) {
                val num = AutoRefillSettings.NUMBER_OF_NOTIFICATIONS.value
                val step = AutoRefillSettings.NOTIFICATION_STEP.value
                for (i in threshold..threshold + num * step step step) {
                    //            Log.trace("Checking if ")
                    if (durability == i) {
                        lastNotifyDurability = i;
                        return true
                    }
                }
            }
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
                    with(AreaTypes) {
                        playerStorage - lockedSlots
                    }.getItemArea(playerContainer,
                                  slots).slotIndices.map {
                        IndexedValue(it - 9,
                                     slots[it].`(itemStack)`)
                    }
                }.asSequence()
                var index = -1
                val itemType = checkingItem.itemType
                if (itemType.isDamageable) {
                    val threshold = if (AutoRefillSettings.REFILL_BEFORE_TOOL_BREAK.booleanValue) {
                        getThreshold(itemType)
                    } else {
                        -1
                    }
                    filtered = filtered.filter { it.value.itemType.run { isDamageable && durability > threshold } }
                    when (itemType.item) {
                        is ArmorItem -> {
                            filtered = filtered.filter {
                                val otherType = it.value.itemType
                                otherType.item is ArmorItem
                                        && otherType.item.`(equipmentSlot)` == itemType.item.`(equipmentSlot)`
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
                return when (AutoRefillSettings.THRESHOLD_UNIT.value) {
                    ABSOLUTE -> AutoRefillSettings.TOOL_DAMAGE_THRESHOLD.integerValue
                    PERCENTAGE -> AutoRefillSettings.TOOL_DAMAGE_THRESHOLD.integerValue * itemType.maxDamage / 100
                }.coerceAtLeast(0)
            }
        }
    }

}