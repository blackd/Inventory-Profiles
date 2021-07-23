
package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.vanilla.alias.DrawableHelper
import org.anti_ad.mc.common.vanilla.render.glue.__glue_dummyDrawableHelper_fillGradient
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rFillRect


private val dummyDrawableHelper = object : DrawableHelper() {
    fun fillGradient(i: Int,
                     j: Int,
                     k: Int,
                     l: Int,
                     m: Int,
                     n: Int) {
        super.fillGradient(rMatrixStack,
                           i,
                           j,
                           k,
                           l,
                           m,
                           n)
//    super.func_238468_a_(rMatrixStack, i, j, k, l, m, n)
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
        DrawableHelper.fill(rMatrixStack,
                            x1,
                            y1,
                            x2,
                            y2,
                            color)
        //  DrawableHelper.func_238467_a_(rMatrixStack, x1, y1, x2, y2, color)
    }
}