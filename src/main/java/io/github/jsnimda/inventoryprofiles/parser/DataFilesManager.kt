package io.github.jsnimda.inventoryprofiles.parser

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.options.ConfigButton
import io.github.jsnimda.common.gui.widgets.ConfigButtonInfo
import io.github.jsnimda.common.util.*
import io.github.jsnimda.common.vanilla.VanillaUtils
import io.github.jsnimda.inventoryprofiles.item.rule.custom.CustomRuleRegister
import io.github.jsnimda.inventoryprofiles.item.rule.custom.RulesFile
import java.nio.file.Path

private val strCmpLogical = LogicalStringComparator.file()

object ReloadRuleFileButtonInfo : ConfigButtonInfo() {

}

object OpenConfigFolderButtonInfo : ConfigButtonInfo() {

}

object DataFilesManager {
  val internalRulesTxtContent = VanillaUtils.getResourceAsString("inventoryprofiles:config/rules.txt") ?: ""
  val internalRulesTxt = "<internal rules.txt>" to internalRulesTxtContent

  fun load() {
    reload()
  }

  fun reload() {
    loadRules()
  }

  private fun loadRules() {
    try {
      (listOf(internalRulesTxt) + readFiles("^rules\\.(?:.*\\.)?txt\$"))
        .map { RulesFile(it.first, it.second) }
        .let { CustomRuleRegister.reload(it) }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun readFiles(regex: String): List<Pair<String, String>> =
    VanillaUtils.configDirectory("inventoryprofiles").listFiles(regex)
      .sortedWith(Comparator { a, b -> strCmpLogical.compare(a.name, b.name) })
      .let { readFiles(it) }

  private fun readFiles(paths: List<Path>): List<Pair<String, String>> =
    paths.mapNotNull { path ->
      tryOrNull(onFailure = { Log.error("Failed to load file $path") }) { path.readFileToString() }
        ?.let { path.name to it }
    }

}