/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.common.vanilla.accessors.menu

import net.minecraft.client.gui.screen.ingame.AnvilScreen
import net.minecraft.screen.ForgingScreenHandler


@Suppress("UnusedReceiverParameter")
val ForgingScreenHandler.`(inputSlotIndices)`
    get() = listOf(ForgingScreenHandler.FIRST_INPUT_SLOT_INDEX, ForgingScreenHandler.SECOND_INPUT_SLOT_INDEX)

val AnvilScreen.`(nameField)`
    get() = this.nameField

var AnvilScreen.`(nameFieldText)`
    get() = this.nameField.text
    set(value) {
        this.nameField.text = value
    }
