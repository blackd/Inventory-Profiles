package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.*

import org.anti_ad.mc.common.profiles.conifg.ProfileItemData;
import org.anti_ad.mc.common.profiles.conifg.ProfileData
import org.anti_ad.mc.common.profiles.conifg.ProfileEnchantmentData
import org.anti_ad.mc.common.profiles.conifg.ProfileSlot
import org.anti_ad.mc.common.profiles.conifg.ProfileSlotId
import org.anti_ad.mc.common.profiles.conifg.fromEnchantmentLevel
import org.anti_ad.mc.common.vanilla.alias.AbstractNbtList
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.ingame.`(asString)`
import org.anti_ad.mc.ipnext.parser.ProfilesLoader

object ProfileSwitchHandler: IInputHandler {

    private var doApplyProfile: Boolean = false

    private var allSlots: List<Int> = (9..45).toList() + (5..8).toList()

    fun onTickInGame() {
        if (VanillaUtil.inGame() && doApplyProfile) {
            doApplyProfile = false
            applyProfile()
        }
    }

    fun createProfileFromCurrentState(): ProfileData {
        val inventory = Vanilla.playerInventory()
        return ProfileData("Saved", ProfileSlotId.valueOf(inventory.`(selectedSlot)` + 36), mutableListOf<ProfileSlot>().apply {
            allSlots.forEach { slot->
                if (ProfileSlotId.valueOf(slot) != ProfileSlotId.NONE) {
                    val stack = Vanilla.playerContainer().`(slots)`[slot].`(itemStack)`
                    if (!stack.isEmpty()) {
                        add(ProfileSlot(ProfileSlotId.valueOf(slot), mutableListOf<ProfileItemData>().apply {
                            add(ProfileItemData(stack.itemType.itemId, mutableListOf<ProfileEnchantmentData>().apply {
                                stack.itemType.tag?.get("Enchantments")?.let { element ->
                                    if (element is AbstractNbtList<*>) {
                                        element.forEach {
                                            if (it is NbtCompound) {
                                                it["id"]?.`(asString)`?.let { enchId ->
                                                    it["lvl"]?.`(asString)`?.let { enchLevel ->
                                                        this.add(ProfileEnchantmentData(enchId, enchLevel.fromEnchantmentLevel()))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }))
                        }))
                    }
                }
            }
        })
    }

    private fun applyProfile() {
        var sourceSlots = allSlots
        val clicks = mutableListOf<Pair<Int, Int>>()
        monitors.forEach {
            if (it.findAndSwap(sourceSlots, clicks)) {
                sourceSlots = sourceSlots - it.slot
            }
        }
        if (targetProfile.active != ProfileSlotId.NONE) {
            val activate: Int = targetProfile.active.slotId;
            Vanilla.playerInventory().`(selectedSlot)` = activate - 36
        }
        /* TODO think how to make this work
        val interval: Int =
                if (ModSettings.ADD_INTERVAL_BETWEEN_CLICKS.booleanValue)
                    ModSettings.INTERVAL_BETWEEN_CLICKS_MS.integerValue
                else 0
        ContainerClicker.executeSwapClicks(clicks, interval)

         */
    }

    private var targetProfile: ProfileData = ProfileData("", ProfileSlotId.NONE, emptyList(), false)

    val monitors: MutableList<ProfileMonitor> = mutableListOf()

    fun init(newProfile: ProfileData) {
        if (targetProfile == newProfile) {
            targetProfile = newProfile
            doApplyProfile = targetProfile.valid

        } else {
            monitors.clear()
            targetProfile = newProfile
            if (targetProfile.valid) {
                targetProfile.slots.forEach {
                    if (it.items.isNotEmpty()) {
                        monitors.add(ProfileMonitor(it.id.slotId, it.items))
                    }
                }
                doApplyProfile = true
            }
        }
    }

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {

        if (!VanillaUtil.inGame()) return false

        if (Hotkeys.NEXT_PROFILE.isActivated()) {
            if (ProfilesLoader.profiles.isNotEmpty()) {
                init(ProfilesLoader.profiles[0])
            }
            return true
        }
        if (Hotkeys.SAVE_AS_PROFILE.isActivated()) {
            val p = createProfileFromCurrentState()
            ProfilesLoader.profiles.add(p)
            ProfilesLoader.save()
            Log.trace("\n$p")
            return true
        }
        return false
    }

    class ProfileMonitor(val slot: Int, private val targetValues: List<ProfileItemData>) {

        fun findAndSwap(sourceSlots: List<Int>,
                        clicks: MutableList<Pair<Int, Int>>): Boolean {
            val currentItem = Vanilla.playerContainer().`(slots)`[slot].`(itemStack)`
            Log.trace("found ${currentItem.itemType.itemId} in slot $slot")
            val swapWith: Int? = targetValues.findIn(sourceSlots, ::bestMatch)
            if (swapWith != null) {
                if (slot != swapWith) {
                    Log.trace("swapping $swapWith to $slot")
                    //clicks.add(Pair(slot, swapWith))
                    swapSlots(slot, swapWith)
                }
                return true
            }
            return false
        }

        private fun swapSlots(to: Int, foundSlotId: Int) {
            GeneralInventoryActions.cleanCursor()
            if ((to - 36) in 0..8) { // use swap
                //handles hotbar
                ContainerClicker.swap(foundSlotId,
                                      to - 36)
            } else {
                //handles offhand and armor slots
                ContainerClicker.leftClick(foundSlotId)
                ContainerClicker.leftClick(to)
                if (!vCursorStack().isEmpty()) {
                    ContainerClicker.leftClick(foundSlotId) // put back
                }
            }
        }

        private fun bestMatch(to: ProfileItemData, from: List<Int>): Int? {
            return from.filter {
                val ist = Vanilla.playerContainer().`(slots)`[it].`(itemStack)`
                ist.itemType.itemId == to.itemId && to.match(ist) > 0
            }.sortedWith { i, j ->
                val jStack = Vanilla.playerContainer().`(slots)`[j].`(itemStack)`
                val iStack = Vanilla.playerContainer().`(slots)`[i].`(itemStack)`
                if (!jStack.itemType.isStackable) {
                    NbtUtils.compareNbt(jStack.itemType.tag,
                                        iStack.itemType.tag)
                } else {
                    jStack.count.compareTo(iStack.count)
                }
            }.firstOrNull()
        }
    }

}



private fun ProfileItemData.match(stack: ItemStack): Int {
    if (this.enchantments.isEmpty()) return 1
    val tags: AbstractNbtList<*> = (stack.itemType.tag?.get("Enchantments") ?: return 0) as AbstractNbtList<*>
    val v: MutableMap<String, ProfileEnchantmentData> = mutableMapOf()
    v.putAll(enchantments.map { it.id to it })
    tags.forEach {
        if (it is NbtCompound) {
            val id = it["id"];
            if (id != null) {
                v.remove(id.toString().removeSurrounding("\""))
            }
        }
    }
    Log.trace(v.toString())
    return if (v.isEmpty()) 1 else 0
}


private fun Iterable<ProfileItemData>.findIn(from: List<Int>, action: (ProfileItemData, List<Int>) -> Int?): Int? {
    for (element in this) {
        val stack = action(element, from)
        if (stack != null) return stack
    }
    return null
}
