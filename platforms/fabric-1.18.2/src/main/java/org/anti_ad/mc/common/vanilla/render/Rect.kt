/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    }
}
