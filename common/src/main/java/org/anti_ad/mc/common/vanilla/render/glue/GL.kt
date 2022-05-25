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

package org.anti_ad.mc.common.vanilla.render.glue

import org.anti_ad.mc.common.Log

var __glue_rStandardGlState: () -> Unit = {
    Log.glueError("__glue_rStandardGlState is not initialized!")
}

val rStandardGlState
    get() = __glue_rStandardGlState

var __glue_rClearDepth: () -> Unit = {
    Log.glueError("__glue_rClearDepth is not initialized!")
}

val rClearDepth
    get() = __glue_rClearDepth
