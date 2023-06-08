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

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.ConnectionProtocol
import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.common.extensions.usefulName

object DebugFunc {

    fun dumpPacketId() {
        dump(PacketFlow.SERVERBOUND)
        dump(PacketFlow.CLIENTBOUND)
    }

    private fun dump(side: PacketFlow) {
        println(side)
        var packet: Packet<*>
        for (i in 0..5000) {
            packet = trySwallow {
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                ConnectionProtocol.PLAY.createPacket(side, i, null)
            } ?: return
            println("$i ${packet.javaClass.usefulName}")
        }
    }

    // see ServerPlayNetworkHandler onClickWindow line 1202
}
