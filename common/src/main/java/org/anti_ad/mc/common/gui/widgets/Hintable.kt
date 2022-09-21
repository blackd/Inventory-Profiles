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

package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.extensions.lnot
import org.anti_ad.mc.ipnext.integration.ButtonPositionHint
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.alpha
import org.anti_ad.mc.common.vanilla.render.asGreen
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.opaque

interface Hintable {

    var hints: ButtonPositionHint

    var underManagement: Boolean

    var hintManagementRenderer: HintManagementRenderer

    fun renderUnderManagement() = hintManagementRenderer.renderUnderManagement()

    fun moveLeft(step: Int) {
        if (this is Widget) {
            hints.dirty = true
            if (this.anchor.right) {
                hints.horizontalOffset = hints.horizontalOffset + step
            } else {
                hints.horizontalOffset = hints.horizontalOffset - step
            }

        }
    }

    fun moveRight(step: Int) {

        if (this is Widget) {
            hints.dirty = true
            if (this.anchor.right) {
                hints.horizontalOffset = hints.horizontalOffset - step
            } else {
                hints.horizontalOffset = hints.horizontalOffset + step
            }
        }
    }

    fun moveUp(step: Int) {
        if (this is Widget) {
            hints.dirty = true
            if (this.anchor.bottom) {
                hints.bottom = hints.bottom + step
            } else {
                hints.top = hints.top - step
            }
        }
    }
    fun moveDown(step: Int) {
        if (this is Widget) {
            hints.dirty = true
            if (this.anchor.bottom) {
                hints.bottom = hints.bottom - step
            } else {
                hints.top = hints.top + step
            }
        }
    }


    class HintManagementRenderer(val target: Hintable) {
        var tick = 0
        var alphaChannel = 0
        var step = 5

        val widget: Widget = target as Widget

        var pingStep = 50;

        fun renderUnderManagement() {
            if (target.underManagement) {
                tick++
                if (tick == 3) {
                    tick = 0
                    alphaChannel += step
                    if (alphaChannel >= 150 || alphaChannel <= 0) step = step.lnot()

                    if (pingStep > 0) {
                        rDrawOutline(widget.absoluteBounds.inflated(pingStep),
                                     0x00ff00.opaque)
                        rDrawOutline(widget.absoluteBounds.inflated(pingStep+2),
                                     0x00ff00.opaque)
                        rDrawOutline(widget.absoluteBounds.inflated(pingStep+4),
                                     0x00ff00.opaque)
                        pingStep -= 5
                    }
                }
                with(widget) {
                    rFillRect(Rectangle(absoluteBounds.x,
                                        absoluteBounds.y,
                                        absoluteBounds.width,
                                        absoluteBounds.height),
                              235.asGreen().alpha(alphaChannel))
                }
            } else {
                pingStep = 50
            }
        }
    }
}
