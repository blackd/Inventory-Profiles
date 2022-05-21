@file:Suppress("NOTHING_TO_INLINE")

package org.anti_ad.mc.ipnext.specific

import org.anti_ad.mc.common.extensions.orDefault
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla.mc
import org.anti_ad.mc.common.vanilla.alias.SharedConstants
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.util.sanitized

inline fun serverIdentifier(perServer: Boolean): String = when {
    !perServer -> {
        ""
    }
    mc().isInSingleplayer -> {
        (mc().server?.saveProperties?.levelName ?: "").sanitized()
    }
    mc().isConnectedToRealms -> {
        "@relms".sanitized()
        //(mc().networkHandler?.connection?.address?.toString()?.replace("/","")?.replace(":","&") ?: "").sanitized()
    }
    mc().currentServerEntry != null -> {
        (mc().currentServerEntry?.address?.replace("/","")?.replace(":","&") ?: "").sanitized()
    }
    else -> {
        ""
    }
}

fun detectServerType(): String {
    return when {
        mc().isInSingleplayer -> {
            "vanilla"
        }
        mc().isConnectedToRealms -> {
            "realms"
        }
        mc().currentServerEntry != null -> {
            val brand = mc().player?.serverBrand.orDefault { "vanilla" }.lowercase()
            when {
                brand.contains("fabric") -> "vanilla"
                brand.contains("forge") -> "vanilla"
                brand.contains("paper") -> "paper"
                brand.contains("spigot") -> "paper"
                brand.contains("bukkit") -> "paper"
                else -> "vanilla"
            }
        }
        else -> {
            "vanilla"
        }
    }
}

inline fun initInfoManager() {
    InfoManager.loader = "fabric"
    InfoManager.modName = ModInfo.MOD_NAME
    InfoManager.modId = ModInfo.MOD_ID
    InfoManager.version = ModInfo.MOD_VERSION
    InfoManager.mcVersion = SharedConstants.getGameVersion().releaseTarget
}
