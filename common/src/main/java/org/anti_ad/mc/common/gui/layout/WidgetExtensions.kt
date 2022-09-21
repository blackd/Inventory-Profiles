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

package org.anti_ad.mc.common.gui.layout

import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Point

fun Widget.moveToCenter() {
    parent?.let { parent ->
        location =
            Point((parent.width - width) / 2,
                  (parent.height - height) / 2)
    }
}

fun Widget.fillParent() = this.apply {
    anchor = AnchorStyles.all
    top = 0
    left = 0
    right = 0
    bottom = 0
}

fun Widget.setTopLeft(top: Int,
                      left: Int) {
    anchor = AnchorStyles.topLeft
    this.top = top
    this.left = left
}

fun Widget.setTopRight(top: Int,
                       right: Int) {
    anchor = AnchorStyles.topRight
    this.top = top
    this.right = right
}

fun Widget.setBottomLeft(bottom: Int,
                         left: Int) {
    anchor = AnchorStyles.bottomLeft
    this.bottom = bottom
    this.left = left
}

fun Widget.setBottomRight(bottom: Int,
                          right: Int) {
    anchor = AnchorStyles.bottomRight
    this.bottom = bottom
    this.right = right
}

fun Widget.setHMiddle(bottom: Int,
                      top: Int) {
    anchor = AnchorStyles.leftRight
    this.bottom = bottom
    this.top = top
}

fun Widget.setVMiddle(left: Int,
                      right: Int) {
    anchor = AnchorStyles.topBottom
    this.left = left
    this.right = right
}
