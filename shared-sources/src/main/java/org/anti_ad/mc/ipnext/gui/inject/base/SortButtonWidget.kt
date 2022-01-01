package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.ipnext.config.GuiSettings

open class SortButtonWidget : TexturedButtonWidget {

    companion object {
        private val TEXTURE = IdentifierHolder("inventoryprofilesnext",
                                               "textures/gui/gui_buttons.png")
    }

    init {
        size = Size(10,
                    10)
    }

    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    override val texture: IdentifierHolder
        get() = TEXTURE

    override val texturePt: Point
        get() = Point(tx, ty)

    override val hoveringTexturePt: Point
        get() = Point(tx, ty + 10)

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        if (GuiSettings.SHOW_BUTTON_TOOLTIPS.booleanValue && contains(mouseX,
                                                                      mouseY) && tooltipText.isNotEmpty()) {
            Tooltips.addTooltip(tooltipText,
                                mouseX,
                                mouseY)
        }
    }

}