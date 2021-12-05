@file:Suppress("NOTHING_TO_INLINE")

package org.anti_ad.mc.ipnext.specific

import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla.mc
import org.anti_ad.mc.common.vanilla.alias.SharedConstants
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.util.sanitized

inline fun serverIdentifier(perServer: Boolean): String = when {
    !perServer -> {
        ""
    }
    mc().isLocalServer -> { //isSingleplayer
        (mc().singleplayerServer?.worldData?.levelName ?: "").sanitized() //serverConfiguration?.worldName ?: "" // integratedServer
    }
    mc().isConnectedToRealms -> {
        (mc().connection?.connection?.remoteAddress?.toString()?.replace("/","")?.replace(":","&") ?: "").sanitized()
    }
    mc().currentServer != null -> {
        (mc().currentServer?.ip?.replace("/","")?.replace(":","&") ?: "").sanitized()
    }
    else -> {
        ""
    }
}

inline fun initInfoManager() {
    InfoManager.loader = "forge"
    InfoManager.modName = ModInfo.MOD_NAME
    InfoManager.modId = ModInfo.MOD_ID
    InfoManager.version = ModInfo.getModVersion()
    InfoManager.mcVersion = SharedConstants.m_183709_().releaseTarget
}
