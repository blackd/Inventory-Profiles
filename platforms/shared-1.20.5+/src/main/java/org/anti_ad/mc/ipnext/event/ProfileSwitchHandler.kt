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
import org.anti_ad.mc.ipnext.profiles.config.ProfileItemData
import org.anti_ad.mc.ipnext.profiles.config.ProfileSlot
import org.anti_ad.mc.ipnext.profiles.config.ProfileSlotId
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.getLiteral
import org.anti_ad.mc.common.vanilla.alias.getTranslatable
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.DataComponentTypes
import org.anti_ad.mc.ipnext.config.EditProfiles
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.ingame.`(asString)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(vanillaStack)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.`(componentsToNbt)`
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.NbtUtils
import org.anti_ad.mc.ipnext.item.customName
import org.anti_ad.mc.ipnext.item.hasPotionName
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isStackable
import org.anti_ad.mc.ipnext.item.itemId
import org.anti_ad.mc.ipnext.item.potionEffects
import org.anti_ad.mc.ipnext.item.vanillaStack
import org.anti_ad.mc.ipnext.parser.ProfilesLoader
import org.anti_ad.mc.ipnext.profiles.config.ProfileComponentData
import kotlin.reflect.typeOf

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
                    val vStack = Vanilla.playerContainer().`(slots)`[slot].`(vanillaStack)`
                    if (!stack.isEmpty()) {
                        add(ProfileSlot(ProfileSlotId.valueOf(slot), mutableListOf<ProfileItemData>().apply {
                            val customName = if (EditProfiles.INCLUDE_CUSTOM_NAME.booleanValue) stack.itemType.customName else ""
                            add(ProfileItemData(stack.itemType.itemId, customName, vStack.componentChanges.entrySet().map { (type, value) ->
                                ProfileComponentData(type.toString(), if (value.isPresent) value.get().toString() else "")

                            } ))
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
        monitors.forEach {
            if (it.findAndSwap(sourceSlots)) {
                sourceSlots = sourceSlots - it.slot
            }
        }
        LockedSlotKeeper.init()
        if (targetProfile.active != ProfileSlotId.NONE) {
            val activate: Int = targetProfile.active.slotId;
            Vanilla.playerInventory().`(selectedSlot)` = activate - 36
        }
    }

    private var targetProfile: ProfileData = ProfileData("", ProfileSlotId.NONE, emptyList(), false)

    val monitors: MutableList<ProfileMonitor> = mutableListOf()

    fun applyCurrent(gui: Boolean = false) {
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
        if (ProfilesLoader.profiles.isNotEmpty()) {
            init(ProfilesLoader.profiles[nextOrFirst()])
        }
    }

    fun nextProfile(gui: Boolean = false) {
        if (ProfilesLoader.profiles.isNotEmpty()) {
            init(ProfilesLoader.profiles[prevOrLast()])
        }
    }

    class ProfileMonitor(val slot: Int, private val targetValues: List<ProfileItemData>) {

        fun findAndSwap(sourceSlots: List<Int>): Boolean {
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
                    val enchMatching = to.matchEnchantments(ist)
                    val customNameMatch = if (to.customName.isNotBlank()) {
                        ist.itemType.customName == to.customName
                    } else {
                        true
                    }
                    res = ist.itemType.itemId == to.itemId && customNameMatch && enchMatching
                }
                res
            }.sortedWith { i, j ->
                val jStack = Vanilla.playerContainer().`(slots)`[j].`(itemStack)`
                val iStack = Vanilla.playerContainer().`(slots)`[i].`(itemStack)`
                if (!jStack.itemType.isStackable) {
                    NbtUtils.compareNbt(jStack.itemType.tag?.get(DataComponentTypes.ENTITY_DATA)?.copyNbt(),
                                        iStack.itemType.tag?.get(DataComponentTypes.ENTITY_DATA)?.copyNbt())
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

private fun ProfileItemData.matchEnchantments(stack: ItemStack): Boolean {
    val stackNbt = stack.vanillaStack.`(componentsToNbt)`
    val profileNbt = NbtUtils.parseNbt("") //TODO fix maching
    return when {
        stackNbt == null && this.components == null -> {
            true
        }
        stackNbt == profileNbt -> true
        else -> {
            false
        }
    }
}


private fun Iterable<ProfileItemData>.findIn(from: List<Int>, action: (ProfileItemData, List<Int>) -> Int?): Int? {
    for (element in this) {
        val stack = action(element, from)
        if (stack != null) return stack
    }
    return null
}
