/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
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

package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.input.GlobalScreenEventListener
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.MerchantScreen
import org.anti_ad.mc.common.vanilla.render.rScreenSize

object ScreenEventHandler {
    fun onScreenInit(target: Screen,
                     addWidget: (ClickableWidget) -> Unit) {
        if (target is ContainerScreen<*>) {
            ContainerScreenEventHandler.onScreenInit(target,
                                                     addWidget)
        }
    }

    fun onScreenRemoved(target: Screen) {
        if (target is ContainerScreen<*>) {
            ContainerScreenEventHandler.onScreenRemoved(target)
        }
    }

    private var trackedScreenSize by detectable(Size(0,
                                                     0)) { _, (width, height) ->
        GlobalScreenEventListener.onResize(width,
                                           height)
    }

    fun preRender() {
        InsertWidgetHandler.preScreenRender()
        trackedScreenSize = rScreenSize
        ContainerScreenEventHandler.preRender()
    }

    fun postRender() {
        ContainerScreenEventHandler.postRender()
    }
}
