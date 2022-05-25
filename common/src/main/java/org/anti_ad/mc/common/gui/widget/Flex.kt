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

package org.anti_ad.mc.common.gui.widget

import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Rectangle

class BiFlex(val owner: Widget,
             val axis: Axis = Axis.HORIZONTAL) {

    val normal: Flex = Flex(owner,
                            FlexDirection.of(axis,
                                             false))
    val reverse: Flex = Flex(owner,
                             FlexDirection.of(axis,
                                              true))

    fun addAndFit(child: Widget,
                  anchorSides: Boolean = true,
                  cross: Int = normal.maxCross) {
        val remaining = normal.ownerExtent - normal.offset - reverse.offset
        normal.add(child,
                   remaining,
                   anchorSides,
                   cross,
                   true)
    }
}

enum class Axis {
    HORIZONTAL,
    VERTICAL
}

enum class FlexDirection(val anchor: AnchorStyles,
                         val axis: Axis,
                         val isReverse: Boolean) {
    LEFT_TO_RIGHT/**/(AnchorStyles.noRight /**/,
                      Axis.HORIZONTAL/**/,
                      false),
    TOP_DOWN     /**/(AnchorStyles.noBottom/**/,
                      Axis.VERTICAL  /**/,
                      false),
    RIGHT_TO_LEFT/**/(AnchorStyles.noLeft  /**/,
                      Axis.HORIZONTAL/**/,
                      true),
    BOTTOM_UP    /**/(AnchorStyles.noTop   /**/,
                      Axis.VERTICAL  /**/,
                      true);

    val isHorizontal
        get() = axis == Axis.HORIZONTAL
    val isVertical
        get() = axis == Axis.VERTICAL

    companion object {
        fun of(axis: Axis,
               isReverse: Boolean) =
            values().first { it.axis == axis && it.isReverse == isReverse }
    }
}

// flow layout with stretch
class Flex(val owner: Widget,
           val direction: FlexDirection = FlexDirection.LEFT_TO_RIGHT) {
    var offset = 0

    val ownerExtent
        get() = if (direction.isHorizontal) owner.width else owner.height
    val maxCross // max size perpendicular to direction
        get() = if (direction.isHorizontal) owner.height else owner.width

    fun add(child: Widget,
            extent: Int, // size along direction
            anchorSides: Boolean = true,
            cross: Int = maxCross, // size perpendicular to direction
            isLast: Boolean = false // set anchor fit
           ) {
        // (direction.isHorizontal)
        owner.removeChild(child)
        var x = if (direction.isReverse) ownerExtent - offset - extent else offset
        var y = (maxCross - cross) / 2
        var width = extent
        var height = cross
        var anchor = direction.anchor
        if (direction.isVertical) {
            x = y.also { y = x }
            width = height.also { height = width }
            if (!anchorSides) anchor = anchor.copy(left = false,
                                                   right = false) // direction.isVertical
            if (isLast) anchor = anchor.copy(top = true,
                                             bottom = true)
        } else {
            if (!anchorSides) anchor = anchor.copy(top = false,
                                                   bottom = false) // direction.isHorizontal
            if (isLast) anchor = anchor.copy(left = true,
                                             right = true)
        }
        offset += extent
        child.anchor = anchor
        child.bounds = Rectangle(x,
                                 y,
                                 width,
                                 height)
        owner.addChild(child)
    }

    fun addSpace(dimension: Int) {
        offset += dimension
    }

    fun addAndFit(child: Widget,
                  anchorSides: Boolean = true,
                  cross: Int = maxCross) {
        val remaining = ownerExtent - offset
        add(child,
            remaining,
            anchorSides,
            cross,
            true)
    }
}
