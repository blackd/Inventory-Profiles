package io.github.jsnimda.inventoryprofiles.item.rule.custom

import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Rule

object RegisteredCustomRules {

  private val rulesFiles = mutableListOf<RulesFile>()
  private val cachedRuleDefinitions = mutableMapOf<String, CustomRuleDefinition?>()

  fun reload(rulesFiles: List<RulesFile>) {
    rulesFiles.forEach { it.init() }
    cachedRuleDefinitions.clear()
    this.rulesFiles.clear()
    this.rulesFiles.addAll(rulesFiles)
  }

  fun getOrEmpty(ruleName: String): Rule = get(ruleName) ?: EmptyRule()

  operator fun get(ruleName: String): CustomRule? {
    if (cachedRuleDefinitions.containsKey(ruleName))
      return cachedRuleDefinitions.getValue(ruleName)?.createCustomRule()
    getUncached(ruleName).let {
      cachedRuleDefinitions[ruleName] = it?.second
      return it?.first
    }
  }

  private fun getUncached(ruleName: String): Pair<CustomRule, CustomRuleDefinition>? {
    rulesFiles.asReversed().forEach {
      it[ruleName]?.let { pair -> return pair }
    }
    return null
  }

}