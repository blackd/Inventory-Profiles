package io.github.jsnimda.inventoryprofiles.item.rule.file

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.IndentedDataFileParser
import io.github.jsnimda.inventoryprofiles.parser.RuleParser
import io.github.jsnimda.inventoryprofiles.parser.SyntaxErrorException

/*
  RuleFile:
  [content] -> [RuleDefinition]+
  get [RuleDefinition]: fail -> log and remove
 */
class RuleFile(val fileName: String, val content: String) {
  // for same name definition, later overrides former
  val rulesMap = mutableMapOf<String, MutableList<RuleDefinition>>()

  fun parseContent() {
    Log.debug("Parsing file $fileName")
    val data = IndentedDataFileParser.parse(content, fileName)
    for (subData in data.subData) {
      Log.debug("  parsing rule: ${subData.text}")
      try {
        val definition = RuleParser.parseRuleDefinition(subData)
        // then add to rules
        rulesMap.getOrPut(definition.ruleName, { mutableListOf() }).add(definition)
      } catch (e: SyntaxErrorException) {
        Log.warn("Found syntax error while parsing ${subData.text} in file $fileName")
        Log.warn("  at: ${e.line}:${e.pos} ${e.msg}")
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    Log.debug {
      "  Added ${rulesMap.values.flatten().size} rules: " +
          rulesMap.map { (name, list) -> if (list.size == 1) name else "$name x${list.size}" }
    }
    Log.debug("  $fileName parse finished")
  }
}