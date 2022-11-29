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

package org.anti_ad.mc.ipnext.debug

import net.minecraft.network.IPacket
import net.minecraft.network.PacketDirection
import net.minecraft.network.ProtocolType
import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.common.extensions.usefulName

object DebugFunc {

    fun dumpPacketId() {
        dump(PacketDirection.SERVERBOUND)
        dump(PacketDirection.CLIENTBOUND)
    }

    private fun dump(side: PacketDirection) {
        println(side)
        var packet: IPacket<*>
        for (i in 0..5000) {
            packet = trySwallow { ProtocolType.PLAY.getPacket(side, i) } ?: return
            println("$i ${packet.javaClass.usefulName}")
        }
    }

    // see ServerPlayNetworkHandler onClickWindow line 1202
}
