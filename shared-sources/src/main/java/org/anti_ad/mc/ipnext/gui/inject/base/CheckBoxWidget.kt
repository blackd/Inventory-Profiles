package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.ipnext.config.ModSettings

class CheckBoxWidget : SortButtonWidget {

    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    var highlightTx = 0
    var highlightTy = 0
    var highlightTooltip: String = ""

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        val oldTx = tx
        val oldTy = ty
        val oldTooltipText = tooltipText
        if (ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing()) {
            tx = highlightTx
            ty = highlightTy
            tooltipText = highlightTooltip
        }
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        tx = oldTx
        ty = oldTy
        tooltipText = oldTooltipText
    }
}