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

data class AnchorStyles(
    val top: Boolean,
    val bottom: Boolean,
    val left: Boolean,
    val right: Boolean
) {
    @Suppress("BooleanLiteralArgument",
              "MemberVisibilityCanBePrivate")
    companion object {
        //@formatter:off
        val none = AnchorStyles(false,
                                false,
                                false,
                                false)
        val all = AnchorStyles(true,
                               true,
                               true,
                               true)
        val noTop = all.copy(top = false)
        val noBottom = all.copy(bottom = false)
        val noLeft = all.copy(left = false)
        val noRight = all.copy(right = false)
        val topOnly = none.copy(top = true)
        val bottomOnly = none.copy(bottom = true)
        val leftOnly = none.copy(left = true)
        val rightOnly = none.copy(right = true)
        val topLeft = none.copy(top = true,
                                left = true)
        val topRight = none.copy(top = true,
                                 right = true)
        val bottomLeft = none.copy(bottom = true,
                                   left = true)
        val bottomRight = none.copy(bottom = true,
                                    right = true)
        val leftRight = none.copy(left = true,
                                  right = true)
        val topBottom = none.copy(top = true,
                                  bottom = true)
        //@formatter:on

        val default = topLeft
    }
}

enum class Overflow {
    UNSET,
    VISIBLE,
    HIDDEN
}
