package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.render.COLOR_HUD_LABEL
import org.anti_ad.mc.common.vanilla.render.COLOR_HUD_TEXT
import org.anti_ad.mc.common.vanilla.render.COLOR_HUD_TEXT_BG
import org.anti_ad.mc.common.vanilla.render.glue.rDrawText
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText

class HudText(text: String) : Widget() {
    init {
        this.text = text
        size = Size(2 + rMeasureText(text),
                    9)
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        if (text.isEmpty()) return
        rFillRect(absoluteBounds,
                  COLOR_HUD_TEXT_BG)
        rDrawText(text,
                  screenX + 1,
                  screenY + 1,
                  COLOR_HUD_TEXT)
    }
}

class HudLabeledText(private var label: String, text: String) : Widget() {
    constructor(pair: Pair<String, String>): this(pair.first, pair.second)

    private val labelLen: Int
    private val textLen: Int
    init {
        this.text = text
        label = "$label: "
        this.labelLen = rMeasureText(label)
        this.textLen = rMeasureText(text)
        size = Size(2 + rMeasureText("$label$text"),
                    9)
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        if (text.isEmpty()) return
        rFillRect(absoluteBounds,
                  COLOR_HUD_TEXT_BG)
        rDrawText(label,
                  screenX + 1,
                  screenY + 1,
                  COLOR_HUD_LABEL)
        rDrawText(text,
                  screenX + labelLen,
                  screenY + 1,
                  COLOR_HUD_TEXT)
    }
}