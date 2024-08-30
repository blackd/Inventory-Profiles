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
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.extensions.loggingPath
import org.anti_ad.mc.common.extensions.sanitized
import org.anti_ad.mc.ipnext.NotificationManager
import org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler
import org.anti_ad.mc.ipnext.event.ProfileSwitchHandler
import org.anti_ad.mc.ipnext.event.villagers.VillagerDataManager
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val strCmpLogical = LogicalStringComparator.file()

object ReloadRuleFileButtonInfoDelegate : ConfigButtonClickHandler() {

    override fun onClick(guiClick: () -> Unit) {
        TellPlayer.listenLog(LogBase.LogLevel.INFO) {
            RuleLoader.reload(true)
        }
        guiClick()
    }
}

private val configFolder = VanillaUtil.configDirectory("inventoryprofilesnext")

private fun getFiles(regex: String) =
    configFolder.listFiles(regex).sortedWith { a, b ->
        strCmpLogical.compare(a.name,
                              b.name)
    }

private val definedLoaders: List<Loader> = listOf(LockSlotsLoader,
                                                  RefillSlotsLoader,
                                                  ProfilesLoader,
                                                  RuleLoader,
                                                  HintsLoader,
                                                  VillagerBookmarksLoader)

object ProfilesLoader: Loader, Savable {

    val file: Path
        get() {
            val dir = serverIdentifier(ModSettings.PROFILES_PER_SERVER.booleanValue).sanitized()
            (configFolder / dir ).createDirectories()
            return configFolder / dir / "profiles-V2.txt"
        }

    val oldfile: Path
        get() {
            val dir = serverIdentifier(ModSettings.PROFILES_PER_SERVER.booleanValue).sanitized()
            (configFolder / dir ).createDirectories()
            return configFolder / dir / "profiles.txt"
        }

    val profiles = mutableListOf<ProfileData>()
    val savedProfiles = mutableListOf<ProfileData>()

    @MayThrow
    override fun save() {
        file.writeText(ProfilesConfig.asString(profiles) + "\n\n\n\n" + ProfilesConfig.asString(savedProfiles))
    }

    override fun load() = reload(false)


    override fun doSanityCheck(): Boolean {
        return false
/*
        return oldfile.exists().ifTrue {
            NotificationManager.addNotification("[IPN] - Found incompatible Profiles configuration")
            Log.error("Found old profiles config at:")
            Log.error("\t\t$oldfile.absolutePathString()")
        }
*/
    }

    override fun reload(fromUserInput: Boolean) {
        profiles.clear()
        savedProfiles.clear()
        val temp = mutableListOf<ProfileData>()
        if (fromUserInput) {
            TellPlayer.chat("Loafing profiles...")
        }
        if (file.exists()) {
            tryOrPrint({msg->
                           Log.warn(msg)
                           TellPlayer.chat("Loading Profile settings failed: $msg")}) {
                temp.addAll(ProfilesConfig.getProfiles(file.readText()).also { it.dump() })
                if (fromUserInput) {
                    TellPlayer.chat("Found ${temp.size} profiles")
                }
            }
        }
        savedProfiles.addAll(temp.filter { it.name.uppercase() == "SAVED" })
        profiles.addAll(temp.filter { it.name.uppercase() != "SAVED" })
        if (fromUserInput) {
            TellPlayer.chat("")
        }
        ProfileSwitchHandler.reloadActiveProfile()
    }
}

// ============
// loader
// ============

interface Loader {
    fun load()
    fun reload(fromUserInput: Boolean)
    fun doSanityCheck(): Boolean
}

object CustomDataFileLoader {
    private val loaders = mutableListOf<Loader>()

    fun load() {
        loaders.forEach { it.load() }
    }

    fun reload(fromUserInput: Boolean) {
        if (fromUserInput) {
            TellPlayer.chat("IPN - Reloading configuration...\n")
        }
        loaders.forEach { it.reload(fromUserInput) }
    }

    fun doSanityCheck(): Boolean {
        var res = false
        loaders.forEach {
            if (it.doSanityCheck()) {
                res = true
            }
        }
        if (res) {
            NotificationManager.addNotification("[IPN] - Check the logs for file locations.\n" +
                                                "         This message will appear as long old config files exist.\n" +
                                                "         It's safe to delete them. The new files have different name.")
        }
        return res
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

    override fun load() = reload(false)

    override fun doSanityCheck(): Boolean {
        return false
    }

    private fun internalLoad(fromUserInput: Boolean) {
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
            if (fromUserInput) {
                TellPlayer.chat("Loading stored 'Locked Slots' failed: $e")
            }
            Log.error("Failed to read file ${file.loggingPath}")
        }
    }

    override fun reload(fromUserInput: Boolean) {
        internalLoad(fromUserInput)
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
    private const val regex = "^rules-v2\\.(?:.*\\.)?txt\$"
    private const val regexOld = "^rules\\.(?:.*\\.)?txt\$"

    override fun doSanityCheck(): Boolean {
        return false
/*
        val files = getFiles(regexOld)
        return files.isNotEmpty().ifTrue {
            NotificationManager.addNotification("[IPM] - Found incompatible custom sorting rules configuration/s.")
            Log.error("Found old custom sorting rule configuration/s:")
            files.forEach {
                Log.error("\t\t${it.toAbsolutePath()}")
            }
        }
*/
    }

    override fun load() = reload(false)

    override fun reload(fromUserInput: Boolean) {
        Log.clearIndent()
        Log.trace("[-] Rule reloading...")
        if (fromUserInput) {
            TellPlayer.chat("Loading custom sort rules...")
        }
        val files = getFiles(regex)
        val ruleFiles = mutableListOf(RuleFile(internalFileDisplayName,
                                               internalRulesTxtContent,
                                               fromUserInput))
        for (file in files) {
            try {
                Log.trace("    Trying to read file ${file.name}")

                val content = file.readText()
                ruleFiles.add(RuleFile(file.name,
                                       content,
                                       fromUserInput))
            } catch (e: Exception) {
                Log.error("Failed to read file ${file.loggingPath}")
            }
        }
        Log.trace("[-] Total ${ruleFiles.size} rule files (including <internal>)")

        RuleFileRegister.reloadRuleFiles(ruleFiles, fromUserInput)
        Log.trace("Rule reload end")

        TemporaryRuleParser.onReload()
        if (fromUserInput) {
            TellPlayer.chat("")
        }

    }
}

object HintsLoader: Loader {

    private var firstLoad: Boolean = false

    override fun load() {
        if(!firstLoad) {
            firstLoad = true
            reload(false)
        }
    }

    override fun doSanityCheck(): Boolean {
        return false
    }

    override fun reload(fromUserInput: Boolean) {
        if (fromUserInput) {
            TellPlayer.chat("Loading GUI Hints configs...")
        }
        val path = (configFolder / "integrationHints").also { it.createDirectories() }
        ContainerTypes.reset()
        HintsManagerNG.upgradeOldConfig(configFolder / "ModIntegrationHints.json" , path)
        HintsManagerNG.init(configFolder, path, fromUserInput)
        if (fromUserInput) {
            TellPlayer.chat("")
        }
    }

}

object VillagerBookmarksLoader: Loader, Savable {

    val path: Path
        get() {
            val dir = serverIdentifier(true).sanitized()
            return (configFolder / dir).also { it.createDirectories() }
        }

    override fun load() {
        reload(false)
    }

    override fun doSanityCheck(): Boolean {
        return false
/*
        return VillagerDataManager.checkOldConfig()
*/
    }

    override fun reload(fromUserInput: Boolean) {
        if (fromUserInput) {
            TellPlayer.chat("Loading Villager trading config...")
        }
        VillagerDataManager.init(path, fromUserInput)
        if (fromUserInput) {
            TellPlayer.chat("")
        }
    }

    override fun save() {
        VillagerDataManager.saveIfDirty()
    }

}


// ============
// lock slots loader
// ============

object RefillSlotsLoader : Loader, Savable {

    val file: Path
        get() {
            val dir = serverIdentifier(ModSettings.ENABLE_LOCK_SLOTS_PER_SERVER.booleanValue).sanitized()
            (configFolder / dir ).createDirectories()
            return configFolder / dir / "refillDisabledSlots.txt"
        }

    private var cachedValue = listOf<Int>()

    override fun save() {
        try {
            val slotIndices = AutoRefillHandler.disabledSlots.sorted()
            if (slotIndices == cachedValue) return
            cachedValue = slotIndices
            slotIndices.joinToString("\n").writeToFile(file)
        } catch (e: Exception) {
            Log.error("Failed to write file ${file.loggingPath}")
        }
    }

    override fun load() = reload(false)

    override fun doSanityCheck(): Boolean {
        return false
    }

    private fun internalLoad(fromUserInput: Boolean) {
        cachedValue = listOf()
        try {
            if (file.notExists()) {
                AutoRefillHandler.disabledSlots.clear()
                return
            }
            val content = file.readText()
            val slotIndices = content.lines().mapNotNull { it.trim().toIntOrNull() }
            AutoRefillHandler.disabledSlots.apply {
                clear()
                addAll(slotIndices)
            }
            cachedValue = slotIndices
        } catch (e: Exception) {
            if (fromUserInput) {
                TellPlayer.chat("Loading stored 'Disabled Auto Refill Slots' failed: $e")
            }
            Log.error("Failed to read file ${file.loggingPath}")
        }
    }

    override fun reload(fromUserInput: Boolean) {
        internalLoad(fromUserInput)
    }
}

