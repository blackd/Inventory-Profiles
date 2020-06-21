package io.github.jsnimda.inventoryprofiles.item.rule.custom

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.IndentedDataFileParser
import io.github.jsnimda.common.util.consumeReversed
import io.github.jsnimda.inventoryprofiles.parser.RuleParser
import io.github.jsnimda.inventoryprofiles.parser.SyntaxErrorException

class RulesFile(private val fileName: String, private val content: String) {

  private val rules = mutableListOf<CustomRuleDefinition>()
  private val rulesMap = mutableMapOf<String, MutableList<CustomRuleDefinition>>()

  private fun addAll(rules: List<CustomRuleDefinition>) {
    this.rules.addAll(rules)
    rules.forEach { rulesMap.getOrPut(it.ruleName) { mutableListOf() }.add(it) }
    Log.debug { "Added ${rules.size} rules: " + rules.map { it.ruleName } }
  }

  operator fun get(ruleName: String): Pair<CustomRule, CustomRuleDefinition>? {
    if (!rulesMap.containsKey(ruleName)) return null
    rulesMap.getValue(ruleName).consumeReversed {
      try {
        return it.createCustomRule() to it
      } catch (e: Exception) { // NoSuchElementException or IllegalStateException
        Log.error("Error while creating rule $ruleName (at file $fileName): ${e.message}")
        if (e !is NoSuchElementException && e !is IllegalStateException) {
          e.printStackTrace()
        }
      }
    }
    // list is empty
    rulesMap.remove(ruleName)
    return null
  }

  fun init() {
    Log.debug("Loading file $fileName")
    IndentedDataFileParser.parse(content, fileName).subData.mapNotNull {
      try {
        RuleParser.parseCustomRule(it)
      } catch (e: SyntaxErrorException) {
        Log.error("Syntax Error while parsing $fileName: ${e.line}:${e.pos} ${e.msg}")
        null
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }.let { addAll(it) }
    Log.debug("$fileName parse finished")
  }

}