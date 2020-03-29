package io.github.jsnimda.inventoryprofiles.item.rule.custom

import io.github.jsnimda.common.util.compare
import io.github.jsnimda.inventoryprofiles.item.rule.BaseRule
import io.github.jsnimda.inventoryprofiles.item.rule.Rule

class CustomRule(subRules: List<Rule>) : BaseRule() {
  init {
    innerCompare = subRules::compare
  }
}

