package io.github.jsnimda.inventoryprofiles.item.rule.file

import io.github.jsnimda.common.annotation.MayThrow
import io.github.jsnimda.common.extensions.compare
import io.github.jsnimda.inventoryprofiles.item.rule.BaseRule
import io.github.jsnimda.inventoryprofiles.item.rule.Rule

class CustomRule(subRules: List<Rule>) : BaseRule() {
  init {
    comparator = subRules::compare
  }
}

class SelfReferenceException(message: String) : RuntimeException(message)

/*
  RuleDefinition: (syntax ok, but might be unusable (logic error))
  @rule_name
    ::some_native(para = arg)
    @other_rule
    @more_other rule
*/
class RuleDefinition(val ruleName: String, private val subRules: List<SubRuleDefinition>) {
  var status = Status.LAZY
    private set

  @get:MayThrow // may throws
  var ruleList: List<Rule>? = null // by subRules
    get() {
      if (status == Status.INITIALIZING) {
        status = Status.FAILED
        throw SelfReferenceException("loop (?) detected")
      }
      if (status == Status.LAZY) {
        status = Status.INITIALIZING
        field = subRules.map { it.toRule() }
          .also { status = Status.SUCCESS }
      }
      return field // SUCCESS or FAILED
    }
    private set // do not call setter

  @MayThrow // may throws
  fun createCustomRule(): CustomRule? =
    ruleList?.let { CustomRule(it) }

  enum class Status {
    LAZY, // not yet
    INITIALIZING,
    SUCCESS,
    FAILED
  }
}