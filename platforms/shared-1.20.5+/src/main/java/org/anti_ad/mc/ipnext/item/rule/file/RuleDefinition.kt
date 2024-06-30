/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.item.rule.file

import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.common.extensions.compare
import org.anti_ad.mc.ipnext.item.rule.BaseRule
import org.anti_ad.mc.ipnext.item.rule.Rule

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
class RuleDefinition(val ruleName: String,
                     private val subRules: List<SubRuleDefinition>) {
    var status = Status.LAZY
        private set

    @get:MayThrow // may throw
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
