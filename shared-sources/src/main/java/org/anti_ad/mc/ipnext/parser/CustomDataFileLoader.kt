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

package org.anti_ad.mc.ipnext.parser

import org.anti_ad.mc.common.LogBase
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.common.extensions.createDirectories
import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.exists
import org.anti_ad.mc.common.extensions.listFiles
import org.anti_ad.mc.common.extensions.name
import org.anti_ad.mc.common.extensions.tryOrPrint
import org.anti_ad.mc.common.extensions.writeToFile
import org.anti_ad.mc.common.gui.widgets.ConfigButtonClickHandler
import org.anti_ad.mc.ipnext.profiles.config.ProfileData
import org.anti_ad.mc.ipnext.profiles.config.ProfilesConfig
import org.anti_ad.mc.ipnext.profiles.config.dump
import org.anti_ad.mc.common.util.LogicalStringComparator
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.item.rule.file.RuleFile
import org.anti_ad.mc.ipnext.item.rule.file.RuleFileRegister
import org.anti_ad.mc.ipnext.specific.serverIdentifier
import org.anti_ad.mc.common.extensions.dashedSanitized
import org.anti_ad.mc.common.extensions.loggingPath
import org.anti_ad.mc.common.extensions.sanitized
import org.anti_ad.mc.ipnext.event.villagers.VillagerDataManager
import org.anti_ad.mc.ipnext.integration.SlotIntegrationHints
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val strCmpLogical = LogicalStringComparator.file()

object ReloadRuleFileButtonInfoDelegate : ConfigButtonClickHandler() {

    override fun onClick(guiClick: () -> Unit) {
        TellPlayer.listenLog(LogBase.LogLevel.INFO) {
            RuleLoader.reload()
        }
        guiClick()
        val fileNames = RuleFileRegister.loadedFileNames.filter { it != RuleLoader.internalFileDisplayName }
        TellPlayer.chat("Reloaded ${fileNames.size} files: $fileNames")
    }
}

private val configFolder = VanillaUtil.configDirectory("inventoryprofilesnext")

private fun getFiles(regex: String) =
    configFolder.listFiles(regex).sortedWith { a, b ->
        strCmpLogical.compare(a.name,
                              b.name)
    }

private val definedLoaders: List<Loader> = listOf(LockSlotsLoader,
                                                  ProfilesLoader,
                                                  RuleLoader,
                                                  HintsLoader,
                                                  VillagerBookmarksLoader)

object ProfilesLoader: Loader, Savable {

    val file: Path
        get() {
            val dir = serverIdentifier(ModSettings.PROFILES_PER_SERVER.booleanValue).sanitized()
            (configFolder / dir ).createDirectories()
            return configFolder / dir / "profiles.txt"
        }
    private val fileOld: Path
        get() {
            return configFolder / "profiles${serverIdentifier(ModSettings.PROFILES_PER_SERVER.booleanValue).dashedSanitized()}.txt"
        }

    val profiles = mutableListOf<ProfileData>()
    val savedProfiles = mutableListOf<ProfileData>()

    @MayThrow
    override fun save() {
        file.writeText(ProfilesConfig.asString(profiles) + "\n\n\n\n" + ProfilesConfig.asString(savedProfiles))
    }

    override fun load() {
        reload()
    }

    override fun reload() {
        profiles.clear()
        savedProfiles.clear()
        val temp = mutableListOf<ProfileData>()
        if (fileOld.exists() && file.notExists()) {
            val content = fileOld.readText()
            content.writeToFile(file)
            fileOld.deleteExisting()
        }
        if (file.exists()) {
            tryOrPrint({msg->
                           Log.warn(msg)
                           TellPlayer.chat("Loading Profile settings failed: $msg")}) {
                temp.addAll(ProfilesConfig.getProfiles(file.readText()).also { it.dump() })
            }
        }
        savedProfiles.addAll(temp.filter { it.name.uppercase() == "SAVED" })
        profiles.addAll(temp.filter { it.name.uppercase() != "SAVED" })
    }
}

// ============
// loader
// ============

interface Loader {
    fun load()
    fun reload()
}

object CustomDataFileLoader {
    private val loaders = mutableListOf<Loader>()

    fun load() {
        loaders.forEach { it.load() }
    }

    fun reload() {
        loaders.forEach { it.reload() }
    }

    init {
        loaders.addAll(definedLoaders)
    }
}

// ============
// lock slots loader
// ============

object LockSlotsLoader : Loader, Savable {

    val file: Path
        get() {
            val dir = serverIdentifier(ModSettings.ENABLE_LOCK_SLOTS_PER_SERVER.booleanValue).sanitized()
            (configFolder / dir ).createDirectories()
            return configFolder / dir / "lockSlots.txt"
        }

    val fileOld: Path
        get() {
            return configFolder / "lockSlots${serverIdentifier(ModSettings.ENABLE_LOCK_SLOTS_PER_SERVER.booleanValue).dashedSanitized()}.txt"
        }


    private var cachedValue = listOf<Int>()

    override fun save() {
        try {
            val slotIndices = LockSlotsHandler.lockedInvSlotsStoredValue.sorted()
            if (slotIndices == cachedValue) return
            cachedValue = slotIndices
            slotIndices.joinToString("\n").writeToFile(file)
        } catch (e: Exception) {
            Log.error("Failed to write file ${file.loggingPath}")
        }
    }

    override fun load() = reload()

    private fun internalLoad() {
        cachedValue = listOf()
        try {
            if (fileOld.exists() && file.notExists()) {
                val content = fileOld.readText()
                content.writeToFile(file)
                fileOld.deleteExisting()
            }
            if (file.notExists()) {
                LockSlotsHandler.lockedInvSlotsStoredValue.clear()
                return
            }
            val content = file.readText()
            val slotIndices = content.lines().mapNotNull { it.trim().toIntOrNull() }
            LockSlotsHandler.lockedInvSlotsStoredValue.apply {
                clear()
                addAll(slotIndices)
            }
            cachedValue = slotIndices
        } catch (e: Exception) {
            Log.error("Failed to read file ${file.loggingPath}")
        }
    }

    override fun reload() {
        internalLoad()
    }
}

// ============
// rule loader
// ============
object RuleLoader : Loader {
    const val internalFileDisplayName = "<internal rules.txt>"
    private val internalRulesTxtContent
        get() = VanillaUtil.getResourceAsString("inventoryprofilesnext:config/rules.txt") ?: ""
            .also { Log.error("Failed to load in-jar file inventoryprofilesnext:config/rules.txt") }
    private const val regex = "^rules\\.(?:.*\\.)?txt\$"

    override fun load() = reload()

    override fun reload() {
        Log.clearIndent()
        Log.trace("[-] Rule reloading...")
        val files = getFiles(regex)
        val ruleFiles = mutableListOf(RuleFile(internalFileDisplayName,
                                               internalRulesTxtContent))
        for (file in files) {
            try {
                Log.trace("    Trying to read file ${file.name}")
                val content = file.readText()
                ruleFiles.add(RuleFile(file.name,
                                       content))
            } catch (e: Exception) {
                Log.error("Failed to read file ${file.loggingPath}")
            }
        }
        Log.trace("[-] Total ${ruleFiles.size} rule files (including <internal>)")
        RuleFileRegister.reloadRuleFiles(ruleFiles)
        Log.trace("Rule reload end")

        TemporaryRuleParser.onReload()
    }
}

object HintsLoader: Loader {

    private var firstLoad: Boolean = false

    override fun load() {
        if(!firstLoad) {
            firstLoad = true
            reload()
        }
    }

    override fun reload() {
        val path = (configFolder / "integrationHints").also { it.createDirectories() }
        ContainerTypes.reset()
        HintsManagerNG.upgradeOldConfig(configFolder / "ModIntegrationHints.json" , path)
        HintsManagerNG.init(configFolder, path)
    }

}

object VillagerBookmarksLoader: Loader, Savable {

    val path: Path
        get() {
            val dir = serverIdentifier(true).sanitized()
            return (configFolder / dir).also { it.createDirectories() }
        }

    override fun load() {
        reload()
    }

    override fun reload() {
        VillagerDataManager.init(path)
    }

    override fun save() {
        VillagerDataManager.saveIfDirty()
    }

}

object SlotSettingsLoader: Loader {

    private var firstLoad: Boolean = false

    override fun load() {
        if(!firstLoad) {
            firstLoad = true
            reload()
        }
    }

    override fun reload() {
        val path = (configFolder / "integrationHints").also { it.createDirectories() }
        SlotIntegrationHints.init(configFolder, path)
    }

}
