package org.anti_ad.mc.common.gui.screen

import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.gui.widget.AnchorStyles
import org.anti_ad.mc.common.gui.widget.moveToCenter
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.render.rFillOutline
import org.anti_ad.mc.common.vanilla.render.rRenderBlackOverlay

private const val COLOR_BORDER = -0x666667
private const val COLOR_BG = -0x1000000

open class BaseDialog : BaseOverlay {
    constructor(text: Text) : super(text)
    constructor() : super()

    var renderBlackOverlay = true
    var closeWhenClickOutside = true

    val dialogWidget =
        object : Widget() {
            override fun render(mouseX: Int,
                                mouseY: Int,
                                partialTicks: Float) {
                rFillOutline(absoluteBounds,
                             COLOR_BG,
                             COLOR_BORDER)
                super.render(mouseX,
                             mouseY,
                             partialTicks)
            }
        }.apply {
            anchor = AnchorStyles.none
            addWidget(this)
            moveToCenter()
            sizeChanged += {
                moveToCenter()
            }
            rootWidget.mouseClicked += { (x, y, button), handled ->
                handled || (button == 0 && closeWhenClickOutside && !contains(x,
                                                                              y)).ifTrue { closeScreen() }
            }
        }

    override fun renderParentPost(mouseX: Int,
                                  mouseY: Int,
                                  partialTicks: Float) {
        if (renderBlackOverlay) {
            rRenderBlackOverlay()
        }
    }
}