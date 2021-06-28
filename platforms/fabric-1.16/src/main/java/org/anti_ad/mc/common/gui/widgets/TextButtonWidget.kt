package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.vanilla.render.glue.rDrawText
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText


open class TextButtonWidget : ButtonWidget {

    constructor(text: String,
                clickEvent: (button: Int) -> Unit) : super(clickEvent) {
        setAllText(text)
    }

    constructor(text: String,
                clickEvent: () -> Unit) : super(clickEvent) {
        setAllText(text)
    }

    constructor(text: String) : super() {
        setAllText(text)
    }

    constructor() : this("")

    init {
        height = 9
        updateWidth()
    }

    fun setAllText(text: String) {
        this.text = text
        this.hoverText = text
        this.inactiveText = text
        updateWidth()
    }

    var hoverText = ""
    var inactiveText = ""
    var hovered: Boolean = false
    val displayText: String
        get() = if (active) if (hovered) hoverText else text else inactiveText

    var pressableMargin = 2

    fun updateWidth() {
        width = rMeasureText(displayText)
    }

    override fun renderButton(hovered: Boolean) {
        this.hovered = hovered
        updateWidth()
        rDrawText(displayText,
                  screenX,
                  screenY,
                  -0x1)
    }

    override fun contains(mouseX: Int,
                          mouseY: Int): Boolean =
        absoluteBounds.inflated(pressableMargin).contains(mouseX,
                                                          mouseY)

}