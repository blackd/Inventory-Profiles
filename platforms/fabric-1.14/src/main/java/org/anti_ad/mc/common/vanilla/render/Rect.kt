package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.vanilla.alias.DrawableHelper
import org.anti_ad.mc.common.vanilla.render.glue.__glue_dummyDrawableHelper_fillGradient
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rFillRect

private val dummyDrawableHelper = object : DrawableHelper() {
    public override fun fillGradient(i: Int,
                     j: Int,
                     k: Int,
                     l: Int,
                     m: Int,
                     n: Int) {
        super.fillGradient(i,
                           j,
                           k,
                           l,
                           m,
                           n)
    }
}

fun initRectGlue() {

    __glue_dummyDrawableHelper_fillGradient = {i: Int,
                                               j: Int,
                                               k: Int,
                                               l: Int,
                                               m: Int,
                                               n: Int ->
        dummyDrawableHelper.fillGradient(i, j, k, l, m, n)
    }

    __glue_rFillRect = { x1, y1, x2, y2, color ->
        DrawableHelper.fill(x1,
                            y1,
                            x2,
                            y2,
                            color)
    }
}