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

package org.anti_ad.mc.common.vanilla.glue

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.config.options.ConfigHotkey

var __glue_vanillaScreenUtil: IVanillaScreenUtil? = null

val VanillaScreenUtil: IVanillaScreenUtil
    get() = __glue_vanillaScreenUtil ?: DummyVanillaScreenUtil



private object DummyVanillaScreenUtil: IVanillaScreenUtil {

    override fun closeScreen() {
        Log.glueError("VanillaScreenUtil Not Initialized")
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openScreen(screen: IScreenMarker) {
        Log.glueError("VanillaScreenUtil Not Initialized")
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openScreenNullable(screen: IScreenMarker?) {
        Log.glueError("VanillaScreenUtil Not Initialized")
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openDistinctScreen(screen: IScreenMarker) {
        Log.glueError("VanillaScreenUtil Not Initialized")
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openDistinctScreenQuiet(screen: IScreenMarker) {
        Log.glueError("VanillaScreenUtil Not Initialized")
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openScreenConfigOptionHotkeyDialog(configOption: ConfigHotkey) {
        Log.glueError("VanillaScreenUtil Not Initialized")
        TODO("Glue Not Initialized! Report an ISSUE")
    }
}



interface IScreenMarker

interface IVanillaScreenUtil {
    fun closeScreen()
    fun openScreen(screen: IScreenMarker)
    fun openScreenNullable(screen: IScreenMarker?)
    fun openDistinctScreen(screen: IScreenMarker)

    fun openDistinctScreenQuiet(screen: IScreenMarker)

    fun openScreenConfigOptionHotkeyDialog(configOption: ConfigHotkey)
}
