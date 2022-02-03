package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.Hintable
import org.anti_ad.mc.common.integration.ButtonPositionHint
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawSprite

abstract class TexturedButtonWidget : ButtonWidget, Hintable {
    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    abstract val texture: IdentifierHolder
    abstract val texturePt: Point
    abstract val hoveringTexturePt: Point

    open var tx = 0
    open var ty = 0
    open var tooltipText: String = ""

    abstract override var hints: ButtonPositionHint

    override var underManagement: Boolean = false

    override fun renderButton(hovered: Boolean) {
        val textureLocation = if (hovered) hoveringTexturePt else texturePt
        rDrawSprite(Sprite(texture,
                           Rectangle(textureLocation,
                                     size)),
                    screenX,
                    screenY)
    }

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        return super.mouseClicked(x,y,button) && visible
    }

}