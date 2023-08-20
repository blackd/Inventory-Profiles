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

@file:Suppress("NOTHING_TO_INLINE")

package org.anti_ad.mc.ipnext.specific

import org.anti_ad.mc.common.vanilla.Vanilla.mc
import org.anti_ad.mc.common.vanilla.alias.SharedConstants
import org.anti_ad.mc.ipnext.IPNInfoManager

inline fun serverIdentifier(perServer: Boolean): String = when {
    !perServer -> {
        ""
    }
    mc().isInSingleplayer -> {
        (mc().server?.saveProperties?.levelName ?: "")
    }
    mc().currentServerEntry?.isRealm == true  -> {
        "@realms"
        //(mc().networkHandler?.connection?.address?.toString()?.replace("/","")?.replace(":","&") ?: "").sanitized()
    }
    mc().currentServerEntry != null -> {
        (mc().currentServerEntry?.address?.replace("/","")?.replace(":","&") ?: "")
    }
    else -> {
        ""
    }
}



inline fun initInfoManager() {
    IPNInfoManager.loader = "fabric"
    IPNInfoManager.mcVersion = SharedConstants.getGameVersion().id ?: "" //releaseTarget
}
