package org.anti_ad.mc.ipnext.gui.inject.base

open class ProfileButtonWidget: SortButtonWidget {
    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        return super.mouseClicked(x,y,button) && visible
    }
}