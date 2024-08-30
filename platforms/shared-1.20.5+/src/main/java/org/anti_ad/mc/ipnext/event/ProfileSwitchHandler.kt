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

import org.anti_ad.mc.alias.component.ComponentType
import org.anti_ad.mc.alias.item.`(componentChanges)`
import org.anti_ad.mc.alias.nbt.NbtCompound
import org.anti_ad.mc.alias.registry.Registries
import org.anti_ad.mc.alias.text.getLiteral
import org.anti_ad.mc.alias.text.getTranslatable
import org.anti_ad.mc.alias.component.DataComponentTypes
import org.anti_ad.mc.alias.nbt.NbtList
import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.extensions.transformOrNull
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.profiles.config.ProfileData
import org.anti_ad.mc.ipnext.profiles.config.ProfileItemData
import org.anti_ad.mc.ipnext.profiles.config.ProfileSlot
import org.anti_ad.mc.ipnext.profiles.config.ProfileSlotId
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.config.EditProfiles
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(vanillaStack)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.ComponentUtils.toFilteredNbtOrNull
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.NbtUtils.compareTo
import org.anti_ad.mc.ipnext.item.customName
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.itemId
import org.anti_ad.mc.ipnext.item.rule.file.RuleFileRegister
import org.anti_ad.mc.ipnext.parser.ProfilesLoader
import org.anti_ad.mc.ipnext.profiles.config.`(asIdentifier)`
import org.anti_ad.mc.ipnext.profiles.config.ProfileComponentData
import java.util.*

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



    private fun toProfileComponentDataOrNull(type: ComponentType<*>, value: Optional<*>): ProfileComponentData? {
        return type.toFilteredNbtOrNull(value).transformOrNull {
            ProfileComponentData(type.toString().`(asIdentifier)`, it)
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
                            add(ProfileItemData(stack.itemType.itemId, customName, vStack.`(componentChanges)`.entrySet().mapNotNull { (type, value) ->
                                toProfileComponentDataOrNull(type, value)
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

    val EMPTY_PROFILE = ProfileData("", ProfileSlotId.NONE, emptyList(), false)

    private var targetProfile: ProfileData = EMPTY_PROFILE

    val monitors: MutableList<ProfileMonitor> = mutableListOf()

    fun applyCurrent(gui: Boolean = false) {
        doApplyProfile = targetProfile.valid
    }

    fun reloadActiveProfile() {
        val activeName = if (targetProfile.valid) activeProfileName else null
        activeProfileId = -1
        init(EMPTY_PROFILE)
        activeName?.let {
            switchToProfileName(it)
            doApplyProfile = false
        }
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
                    val enchMatching = to.match(ist)
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

                RuleFileRegister.getCustomRuleOrEmpty("auto_refill_best").compare(jStack.itemType,
                                                                                  iStack.itemType)
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

private fun ProfileItemData.match(stack: ItemStack): Boolean {
    val stackCustomName = stack.itemType.customName
    return (itemId == stack.itemType.itemId)
           && ((customName.isNotBlank() && stackCustomName.isNotBlank() && customName == stackCustomName)
               || this.components.match(stack))
}

private fun List<ProfileComponentData>?.match(stack: ItemStack): Boolean {
    val changes = stack.itemType.changes
/*
    if (changes.isEmpty && this.isNullOrEmpty()) return true
    if (this.isNullOrEmpty() || changes.isEmpty) return false
*/
    if (this.isNullOrEmpty()) return true

    this.forEach { ct ->
        val type = Registries.DATA_COMPONENT_TYPE[ct.id]
        if (type != null) {
            val component = changes.get(type)
            if (component == null || component.isEmpty) return false
            val nbt = type.toFilteredNbtOrNull(component) ?: NbtCompound()
            val ctComponentNbt = ct.componentNbt
            if (type == DataComponentTypes.ENCHANTMENTS && ctComponentNbt is NbtList && nbt is NbtList) {
                val found = nbt.filter { st ->
                    ctComponentNbt.contains(st)
                }
                if (found.size == ctComponentNbt.size) return true
            }
            if (ctComponentNbt.compareTo(nbt) != 0) return false
        }
    }

    return true
}

private fun Iterable<ProfileItemData>.findIn(from: List<Int>, action: (ProfileItemData, List<Int>) -> Int?): Int? {
    for (element in this) {
        val stack = action(element, from)
        if (stack != null) return stack
    }
    return null
}
