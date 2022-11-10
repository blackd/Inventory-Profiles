/*
 * Inventory Profiles Next
 *
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

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.profiles.config.ProfileData
import org.anti_ad.mc.ipnext.profiles.config.ProfileEnchantmentData
import org.anti_ad.mc.ipnext.profiles.config.ProfileItemData
import org.anti_ad.mc.ipnext.profiles.config.ProfileSlot
import org.anti_ad.mc.ipnext.profiles.config.ProfileSlotId
import org.anti_ad.mc.ipnext.profiles.config.fromEnchantmentLevel
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.AbstractNbtList
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.common.vanilla.alias.NbtString
import org.anti_ad.mc.common.vanilla.alias.getLiteral
import org.anti_ad.mc.common.vanilla.alias.getTranslatable
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.IPNInfoManager
import org.anti_ad.mc.ipnext.config.EditProfiles
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.ingame.`(asString)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.NbtUtils
import org.anti_ad.mc.ipnext.item.customName
import org.anti_ad.mc.ipnext.item.hasPotionName
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isStackable
import org.anti_ad.mc.ipnext.item.itemId
import org.anti_ad.mc.ipnext.item.potionEffects
import org.anti_ad.mc.ipnext.parser.ProfilesLoader

object ProfileSwitchHandler: IInputHandler {

    val activeProfileName: String?
        get() {
            return if (targetProfile.valid) targetProfile.name else null
        }

    var activeProfileId = 0

    private var doApplyProfile: Boolean = false

    private val allSlots: List<Int> = (9..45).toList() + (5..8).toList()
    private val hotBarSlots: List<Int> = (0+36..8+36).toList()

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
                            val potion: String = when {
                                (stack.itemType.potionEffects.isNotEmpty()) -> {
                                    var p = ""
                                    stack.itemType.tag?.get("Potion")?.let { potion ->
                                        if (potion is NbtString) {
                                            p = potion.`(asString)`.removeSurrounding("\"")
                                        }
                                    }
                                    p
                                }
                                else -> {
                                    ""
                                }
                            }
                            val customName = if (EditProfiles.INCLUDE_CUSTOM_NAME.booleanValue) stack.itemType.customName else ""
                            add(ProfileItemData(stack.itemType.itemId, customName, potion, mutableListOf<ProfileEnchantmentData>().apply {
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
        //Vanilla.inGameHud().setSubtitle(LiteralText(targetProfile.name))
        if (GuiSettings.ENABLE_PROFILES_ANNOUNCEMENT.booleanValue) {
            Vanilla.inGameHud().setOverlayMessage(getLiteral(targetProfile.name), false)
        }
        var sourceSlots = allSlots
        val clicks = mutableListOf<Pair<Int, Int>>()
        monitors.forEach {
            if (it.findAndSwap(sourceSlots, clicks)) {
                sourceSlots = sourceSlots - it.slot
            }
        }
        LockedSlotKeeper.init()
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

    fun applyCurrent(gui: Boolean = false) {
        IPNInfoManager.event(lazy { if (gui) "gui/" else {""} + "applyCurrent" })
        doApplyProfile = targetProfile.valid
    }

    fun init(newProfile: ProfileData) {
        if (targetProfile == newProfile) {
            targetProfile = newProfile
            doApplyProfile = targetProfile.valid

        } else {
            monitors.clear()
            targetProfile = newProfile
            if (targetProfile.valid) {
                targetProfile.slots.forEach {
                    monitors.add(ProfileMonitor(it.id.slotId, it.items))
                }
                doApplyProfile = true
            }
        }
    }

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {

        if (!VanillaUtil.inGame()) return false

        if (Hotkeys.APPLY_PROFILE.isActivated()) {
            applyCurrent()
            return true
        }
        if (Hotkeys.NEXT_PROFILE.isActivated()) {
            nextProfile()
            return true
        }
        if (Hotkeys.PREV_PROFILE.isActivated()) {
            prevProfile()
            return true
        }
        if (Hotkeys.PROFILE_1.isActivated()) {
            val name = EditProfiles.QUICK_SLOT_1_PROFILE.value
            if (name != EditProfiles.QUICK_SLOT_1_PROFILE.defaultValue) {
                switchToProfileName(name)
            }
            return true
        }
        if (Hotkeys.PROFILE_2.isActivated()) {
            val name = EditProfiles.QUICK_SLOT_2_PROFILE.value
            if (name != EditProfiles.QUICK_SLOT_2_PROFILE.defaultValue) {
                switchToProfileName(name)
            }
            return true
        }
        if (Hotkeys.PROFILE_3.isActivated()) {
            val name = EditProfiles.QUICK_SLOT_3_PROFILE.value
            if (name != EditProfiles.QUICK_SLOT_3_PROFILE.defaultValue) {
                switchToProfileName(name)
            }
            return true
        }


        if (Hotkeys.SAVE_AS_PROFILE.isActivated()) {
            val p = createProfileFromCurrentState()
            ProfilesLoader.savedProfiles.add(p)
            ProfilesLoader.save()
            VanillaUtil.chat(getTranslatable("inventoryprofiles.profiles.created_new_saved"))
            Log.trace("\n$p")
            return true
        }
        return false
    }

    private fun switchToProfileName(name: String) {
        val index = byName(name)
        if (index != -1) {
            activeProfileId = index
            init(ProfilesLoader.profiles[index])
        }
    }

    fun prevProfile(gui: Boolean = false) {
        IPNInfoManager.event(lazy { if (gui) "gui/" else {""} + "prevProfile" })
        if (ProfilesLoader.profiles.isNotEmpty()) {
            init(ProfilesLoader.profiles[nextOrFirst()])
        }
    }

    fun nextProfile(gui: Boolean = false) {
        IPNInfoManager.event(lazy { if (gui) "gui/" else {""} + "nextProfile"})
        if (ProfilesLoader.profiles.isNotEmpty()) {
            init(ProfilesLoader.profiles[prevOrLast()])
        }
    }

    class ProfileMonitor(val slot: Int, private val targetValues: List<ProfileItemData>) {

        fun findAndSwap(sourceSlots: List<Int>,
                        clicks: MutableList<Pair<Int, Int>>): Boolean {
            val currentItem = Vanilla.playerContainer().`(slots)`[slot].`(itemStack)`
            Log.trace("found ${currentItem.itemType.itemId} in slot $slot")
            if (targetValues.isNotEmpty()) {
                val swapWith: Int? = targetValues.findIn(sourceSlots, ::bestMatch)
                if (swapWith != null) {
                    if (slot != swapWith) {
                        Log.trace("swapping $swapWith to $slot")
                        //clicks.add(Pair(slot, swapWith))
                        swapSlots(slot, swapWith)
                    }
                    return true
                }
            } else {
                val preferLocked = true
                val preferNonHotbar = true
                var targets = if (preferLocked && LockedSlotKeeper.emptyLockedSlots.isNotEmpty()) {
                    LockedSlotKeeper.emptyLockedSlots.toList()
                } else {
                    LockedSlotKeeper.emptyNonLockedSlots.toList()
                }

                if (preferNonHotbar) {
                    val noHotBar = targets - hotBarSlots
                    if (noHotBar.isNotEmpty()) {
                        targets = noHotBar
                    }
                }

                targets.forEach {
                    if (Vanilla.playerContainer().`(slots)`[it].`(itemStack)`.isEmpty()) {
                        swapSlots(slot, it)
                        return true
                    }
                }
            }
            return false
        }

        private fun swapSlots(to: Int, foundSlotId: Int) {
            AutoRefillHandler.profilesSwappedItems.add(to)
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
                var res = false
                if (!ist.isEmpty()) {
                    val isPotion = ist.itemType.hasPotionName
                    val enchMatching = !isPotion && to.matchEnchantments(ist)
                    val potionMaching = to.potion != "" && isPotion && to.matchPotion(ist)
                    val customNameMatch = if (to.customName.isNotBlank()) {
                        ist.itemType.customName == to.customName
                    } else {
                        true
                    }
                    res = ist.itemType.itemId == to.itemId && customNameMatch && (enchMatching || potionMaching)
                }
                res
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

    private fun byName(name: String): Int {
        ProfilesLoader.profiles.forEachIndexed { index, profileData ->
            if (profileData.name == name) {
                return index
            }
        }
        return  -1
    }


    private fun nextOrFirst(): Int {
        val next  = activeProfileId + 1

        activeProfileId = if (next < ProfilesLoader.profiles.size) {
            next
        } else {
            0
        }
        return  activeProfileId

    }

    private fun prevOrLast(): Int {
        val next  = activeProfileId - 1
        activeProfileId = if (next >= 0) {
            next
        } else {
            ProfilesLoader.profiles.size - 1
        }
        return activeProfileId
    }


}

private fun ProfileItemData.matchPotion(stack: ItemStack): Boolean {
    if (this.potion == "") return false
    if (stack.itemType.potionEffects.isEmpty()) return false
    val toPotion = stack.itemType.tag?.get("Potion") ?: return this.potion == ""
    val toPotionName = toPotion.`(asString)`
    return toPotionName == this.potion

}

private fun ProfileItemData.matchEnchantments(stack: ItemStack): Boolean {
    //if (this.enchantments.isEmpty()) return 1
    val enchs = stack.itemType.tag?.get("Enchantments") ?: return this.enchantments.isEmpty()

    val tags: AbstractNbtList<*> = enchs as AbstractNbtList<*>
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
    return v.isEmpty()
}


private fun Iterable<ProfileItemData>.findIn(from: List<Int>, action: (ProfileItemData, List<Int>) -> Int?): Int? {
    for (element in this) {
        val stack = action(element, from)
        if (stack != null) return stack
    }
    return null
}
