package io.github.jsnimda.inventoryprofiles.item.rule.file

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.annotation.ThrowsCaught
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

  @ThrowsCaught
  fun parseContent() {
    Log.trace("[-] Parsing file $fileName")
    val data = IndentedDataFileParser.parse(content, fileName)
    for (subData in data.subData) {
      Log.trace("    - parsing rule: ${subData.text}")
      try {
        @ThrowsCaught
        val definition = RuleParser.parseRuleDefinition(subData)
        // then add to rules
        rulesMap.getOrPut(definition.ruleName, { mutableListOf() }).add(definition)
      } catch (e: SyntaxErrorException) {
        Log.warn("Syntax error in '$fileName' (${subData.text})")
        Log.warn("  > at: ${e.line}:${e.pos} ${e.msg}")
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    Log.trace {
      "    Added ${rulesMap.values.flatten().size} rules: " +
          rulesMap.map { (name, list) -> if (list.size == 1) name else "$name x${list.size}" }
    }
    Log.trace("    $fileName parse finished")
  }
}