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

package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.alias.client.gui.screen.ingame.ContainerScreen
import org.anti_ad.mc.alias.screen.Container
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.ipnext.gui.widgets.Hintable
import org.anti_ad.mc.common.gui.widgets.Widget

abstract class InsertableWidget: Widget() {

    abstract fun postBackgroundRender(context: NativeContext,
                                      mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float);

    abstract val screen: ContainerScreen<*>
    abstract val container: Container

    val hintableList = mutableListOf<Hintable>()

    override fun render(context: NativeContext,
                        mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(context, mouseX, mouseY, partialTicks)
    }

    abstract fun postForegroundRender(context: NativeContext,
                                      mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float)

}
