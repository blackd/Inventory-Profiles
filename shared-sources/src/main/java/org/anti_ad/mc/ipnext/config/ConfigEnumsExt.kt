package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.rule.file.RuleFileRegister
import org.anti_ad.mc.ipnext.parser.TemporaryRuleParser

fun SortingMethodIndividual.rule(customContent: String): Rule {
    return when (this) {
        SortingMethodIndividual.GLOBAL -> ModSettings.SORT_ORDER.value.rule
        SortingMethodIndividual.CUSTOM -> TemporaryRuleParser.parse(customContent)
        else -> SortingMethod.values()[ordinal - 1].rule
    }
}

private val SortingMethod.rule: Rule
    get() = ruleName?.let { RuleFileRegister.getCustomRuleOrEmpty(it) }
            ?: TemporaryRuleParser.parse(ModSettings.CUSTOM_RULE.value)