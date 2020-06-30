package io.github.jsnimda.inventoryprofiles.item.rule.file

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.ifTrue
import io.github.jsnimda.common.util.usefulName
import io.github.jsnimda.inventoryprofiles.item.rule.EmptyRule
import io.github.jsnimda.inventoryprofiles.item.rule.Parameter
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.native.NativeRules
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.NativeParameters

/*
  rulesFiles: [a.txt] -> [b.txt] -> [c.txt] -> ... -> [z.txt]
              low priority                     high priority (i.e. z.txt overrides a.txt)

  find rule [my_rule]: find in cached ?-> find from [z.txt] to [a.txt]
    found: add to cached, not found: denoted as not found

*/
object RuleFileRegister {
  private val ruleFiles = mutableListOf<RuleFile>()
  private val cachedRules = mutableMapOf<String, RuleDefinition?>() // store RuleDefinition of SUCCESS

  fun reloadRuleFiles(ruleFiles: List<RuleFile>) {
    Log.debug("Rule file parse step: (1.) parse indent (2.) parse rule")
    ruleFiles.forEach { it.parseContent() }
    clear()
    this.ruleFiles.addAll(ruleFiles)
    checkOverrides()
    validateRules()
  }

  private fun clear() {
    ruleFiles.clear()
    cachedRules.clear()
    names.clear()
  }

  private val names = mutableSetOf<String>()
  private fun checkOverrides() { // and log
    Log.debug("Check overrides...")
    for (ruleFile in ruleFiles) {
      for (name in ruleFile.rulesMap.keys) {
        if (name in names) {
          Log.info("Rule $name overrode by file ${ruleFile.fileName}")
        }
        names.add(name)
      }
    }
  }

  private fun validateRules() {
    Log.debug("Validate rules...")
    for (name in names) {
      Log.debug("Validating rule $name")
      getCustomRule(name) ?: Log.debug("rule $name failed to parse")
    }
  }

  fun getCustomRuleOrEmpty(ruleName: String): Rule =
    getCustomRule(ruleName) ?: EmptyRule()
      .also { Log.warn("Rule @$ruleName not found") }


  // ============
  // ~.~ methods
  // ============
  fun getCustomRule(ruleName: String): CustomRule? {
    val ruleDefinition =
      if (cachedRules.containsKey(ruleName)) cachedRules.getValue(ruleName) else searchAndPutCustomRule(ruleName)
    return ruleDefinition?.createCustomRule() // should not throw
  }

  fun getNativeRule(ruleName: String): Rule? =
    NativeRules.map[ruleName]?.invoke()

  fun getParameter(parameterName: String): Parameter<*>? =
    NativeParameters.map[parameterName]

  // ============
  // private
  // ============
  private fun searchAndPutCustomRule(ruleName: String): RuleDefinition? {
    Log.debug("Searching rule @$ruleName...")
    val ruleDefinition = RuleFinder(ruleName).searchCustomRule()
    cachedRules[ruleName] = ruleDefinition
    // then remove empty file
    ruleFiles.removeAll { it.rulesMap.isEmpty().ifTrue { Log.debug("Removed validated file ${it.fileName}") } }
    return ruleDefinition
  }

  private class RuleFinder(val ruleName: String) {
    fun searchCustomRule(): RuleDefinition? {
      for (ruleFile in ruleFiles.reversed()) { // asReversed here might cause no such element (list update)
        val rulesMap = ruleFile.rulesMap
        if (!rulesMap.containsKey(ruleName)) continue
        val fileName = ruleFile.fileName
        Log.debug("Searching rule @$ruleName at file $fileName")
        val list = rulesMap.getValue(ruleName)
        val ruleDefinition = findUsableRule(list.asReversed(), fileName) // list won't update
        // result found, -> remove key
        rulesMap.remove(ruleName)
        if (ruleDefinition != null) {
          Log.debug("Found one at file $fileName")
          return ruleDefinition
        } else {
          Log.debug("None of them usable at file $fileName")
        }
      }
      Log.debug("@$ruleName not found in all files")
      return null
    }

    private fun findUsableRule(list: List<RuleDefinition>, fileName: String): RuleDefinition? { // from 0+
      for (ruleDefinition in list) {
        try {
          ruleDefinition.createCustomRule()
          if (ruleDefinition.status == RuleDefinition.Status.SUCCESS) {
            return ruleDefinition // no throws, meaning success
          }
          Log.error("interesting rule @$ruleName (at file $fileName)") // shouldn't go here
        } catch (e: Exception) {
          Log.warn("Found error while creating rule '@$ruleName' (at file $fileName)")
          Log.warn("  ${e.javaClass.usefulName}: ${e.message}")
          when (e) {
            is NoSuchElementException,
            is SelfReferenceException,
            -> Unit // do nothing
            else -> e.printStackTrace()
          }
          continue // next rule definition
        }
      }
      return null // no search
    }
  }
}