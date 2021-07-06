package org.anti_ad.mc.ipnext.parser

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.extensions.*
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.util.LogicalStringComparator
import org.anti_ad.mc.common.vanilla.Vanilla.mc
import org.anti_ad.mc.common.vanilla.alias.ClientWorld
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.glue.loggingPath
import org.anti_ad.mc.ipnext.client.TellPlayer
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.item.rule.file.RuleFile
import org.anti_ad.mc.ipnext.item.rule.file.RuleFileRegister
import java.nio.file.Path
import java.util.*
import kotlin.concurrent.schedule

private val strCmpLogical = LogicalStringComparator.file()

object ReloadRuleFileButtonInfo : ConfigButtonInfo() {
    override val buttonText: String
        get() = I18n.translate("inventoryprofiles.gui.config.button.reload_rule_files")

    override fun onClick(widget: ButtonWidget) {
        TellPlayer.listenLog(Log.LogLevel.INFO) {
            RuleLoader.reload()
        }
        widget.active = false
        widget.text = I18n.translate("inventoryprofiles.gui.config.button.reload_rule_files.reloaded")
        Timer().schedule(5000) { // reset after 5 sec
            widget.text = buttonText
            widget.active = true
        }
        val fileNames = RuleFileRegister.loadedFileNames.filter { it != RuleLoader.internalFileDisplayName }
        TellPlayer.chat("Reloaded ${fileNames.size} files: $fileNames")
    }
}

object OpenConfigFolderButtonInfo : ConfigButtonInfo() {
    override val buttonText: String
        get() = I18n.translate("inventoryprofiles.gui.config.button.open_config_folder")

    override fun onClick(widget: ButtonWidget) {
        VanillaUtil.open(configFolder.toFile())
    }
}

private val configFolder = VanillaUtil.configDirectory("inventoryprofilesnext")
private fun getFiles(regex: String) =
    configFolder.listFiles(regex).sortedWith { a, b ->
        strCmpLogical.compare(a.name,
                              b.name)
    }

private val definedLoaders: List<Loader> = listOf(LockSlotsLoader,
                                                  RuleLoader)

// ============
// loader
// ============

interface Loader {
    fun reload(clientWorld: ClientWorld? = null)
}

object CustomDataFileLoader {
    private val loaders = mutableListOf<Loader>()

    fun load(clientWorld: ClientWorld) {
        reload(clientWorld)
    }

    fun reload(clientWorld: ClientWorld? = null) {
        loaders.forEach { it.reload(clientWorld) }
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
        val id: String = when {
            !ModSettings.ENABLE_LOCK_SLOTS_PER_SERVER.booleanValue -> {
                return configFolder / "lockSlots.txt"
            }
            mc().isSingleplayer -> {
                mc().integratedServer?.serverConfiguration?.worldName ?: ""
            }
            mc().isConnectedToRealms -> {
                mc().connection?.networkManager?.remoteAddress?.toString()?.replace("/","")?.replace(":","&") ?: ""
            }
            mc().currentServerData != null -> {
                mc().currentServerData?.serverIP?.replace("/","")?.replace(":","&") ?: ""
            }
            else -> {
                return configFolder / "lockSlots.txt"
            }
        }
        return configFolder / "lockSlots-$id.txt"
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

    override fun load(clientWorld: Any?) {
        val world = clientWorld as ClientWorld
        internalLoad(world)
    }

    private fun internalLoad(clientWorld: ClientWorld?) {

        cachedValue = listOf()
        try {
            if (!file.exists()) {
                LockSlotsHandler.lockedInvSlotsStoredValue.clear()
                return
            }
            val content = file.readToString()
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

    override fun reload(clientWorld: ClientWorld?) {
        internalLoad(clientWorld)
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

    override fun reload(clientWorld: ClientWorld?) {
        Log.clearIndent()
        Log.trace("[-] Rule reloading...")
        val files = getFiles(regex)
        val ruleFiles = mutableListOf(RuleFile(internalFileDisplayName,
                                               internalRulesTxtContent))
        for (file in files) {
            try {
                Log.trace("    Trying to read file ${file.name}")
                val content = file.readToString()
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