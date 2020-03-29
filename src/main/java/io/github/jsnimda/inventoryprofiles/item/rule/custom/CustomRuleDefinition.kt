package io.github.jsnimda.inventoryprofiles.item.rule.custom

import io.github.jsnimda.inventoryprofiles.item.rule.Rule

class CustomRuleDefinition(val ruleName: String, private val subRules: List<SubRuleDefinition>) {
  var status = Status.LAZY
    private set

  private fun getPrefix(isCustomElseNative: Boolean) =
    if (isCustomElseNative) "@" else "::"

  private val subRulesInstance: List<Rule>?
    get() = if (status == Status.FAILED) null else _subRulesInstance
  private val _subRulesInstance: List<Rule> by lazy {
    try {
      if (status != Status.LAZY) error("loop (?) detected")
      status = Status.INITIALIZING
      subRules.map { it.toRule() }
    } catch (e: Exception) {
      status = Status.FAILED
      throw e
    }
  }

  fun createCustomRule(): CustomRule =
    subRulesInstance?.let { CustomRule(it) } ?: error("failed since the last time")

  enum class Status {
    LAZY,
    INITIALIZING,
    LOADED,
    FAILED
  }
}