package org.anti_ad.mc.ipnext.debug

import io.netty.buffer.Unpooled
import net.minecraft.network.NetworkSide
import net.minecraft.network.NetworkState
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.common.extensions.usefulName

object DebugFunc {

    fun dumpPacketId() {
        dump(NetworkSide.SERVERBOUND)
        dump(NetworkSide.CLIENTBOUND)
    }

    private fun dump(side: NetworkSide) {
        println(side)
        var packet: Packet<*>
        for (i in 0..5000) {
            packet = trySwallow { NetworkState.PLAY.getPacketHandler(side, i, PacketByteBuf(Unpooled.buffer())) } ?: return
            println("$i ${packet.javaClass.usefulName}")
        }
    }

    // see ServerPlayNetworkHandler onClickWindow line 1202
}