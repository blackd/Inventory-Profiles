package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.extensions.lnot
import org.anti_ad.mc.common.integration.ButtonPositionHint
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
