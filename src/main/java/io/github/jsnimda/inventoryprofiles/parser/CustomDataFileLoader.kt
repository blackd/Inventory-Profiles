package io.github.jsnimda.inventoryprofiles.parser

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.gui.widgets.ButtonWidget
import io.github.jsnimda.common.gui.widgets.ConfigButtonInfo
import io.github.jsnimda.common.util.LogicalStringComparator
import io.github.jsnimda.common.util.listFiles
import io.github.jsnimda.common.util.name
import io.github.jsnimda.common.util.readFileToString
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.loggingPath
import io.github.jsnimda.inventoryprofiles.event.TellPlayer
import io.github.jsnimda.inventoryprofiles.item.rule.file.RuleFile
import io.github.jsnimda.inventoryprofiles.item.rule.file.RuleFileRegister
import java.util.*
import kotlin.concurrent.schedule

private val strCmpLogical = LogicalStringComparator.file()

object ReloadRuleFileButtonInfo : ConfigButtonInfo() {
  override val buttonText: String
    get() = I18n.translate("inventoryprofiles.gui.config.button.reload_rule_files")

  override fun onClick(widget: ButtonWidget) {
    TellPlayer.listenLog(Log.LogLevel.WARN) {
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

val configFolder = VanillaUtil.configDirectory("inventoryprofiles")
fun getFiles(regex: String) =
  configFolder.listFiles(regex).sortedWith { a, b -> strCmpLogical.compare(a.name, b.name) }

// ============
// loader
// ============

interface Loader {
  fun reload()
}

object CustomDataFileLoader {
  private val loaders = mutableListOf<Loader>()

  fun load() {
    reload()
  }

  fun reload() {
    loaders.forEach { it.reload() }
  }

  init {
    loaders.add(RuleLoader)
  }
}

// ============
// rule loader
// ============
object RuleLoader : Loader {
  const val internalFileDisplayName = "<internal rules.txt>"
  private val internalRulesTxtContent = VanillaUtil.getResourceAsString("inventoryprofiles:config/rules.txt") ?: ""
    .also { Log.error("Failed to load in-jar file inventoryprofiles:config/rules.txt") }
  private const val regex = "^rules\\.(?:.*\\.)?txt\$"

  override fun reload() {
    Log.debug("Rule reloading")
    val files = getFiles(regex)
    val ruleFiles = mutableListOf(RuleFile(internalFileDisplayName, internalRulesTxtContent))
    for (file in files) {
      try {
        Log.debug("Trying to read file ${file.name}")
        val content = file.readFileToString()
        ruleFiles.add(RuleFile(file.name, content))
      } catch (e: Exception) {
        Log.error("Failed to read file ${file.loggingPath}")
      }
    }
    Log.debug("Total ${ruleFiles.size} rule files (including <internal>)")
    RuleFileRegister.reloadRuleFiles(ruleFiles)
    Log.debug("Rule reload end")

    TemporaryRuleParser.onReload()
  }
}