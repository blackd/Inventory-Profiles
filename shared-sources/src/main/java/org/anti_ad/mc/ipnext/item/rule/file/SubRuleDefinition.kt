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

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.ipnext.item.rule.Rule

class MissingParameterException(message: String) : RuntimeException(message)

/*
  SubRuleDefinition:
  @default
  or
  ::native(para = arg)
 */
class SubRuleDefinition(
    val prefix: String, // "@" or "::"
    val name: String,
    arguments: List<Pair<String, String>> // listOf(para to arg)
) {
    val arguments = arguments.map { (a, b) -> a.trim() to b.trim() }
    private val identifier: String
        get() = "$prefix$name"

    @MayThrow // may throws (no need to cache this)
    fun toRule(): Rule {
        val rule = when (prefix) {
            "@" -> RuleFileRegister.getCustomRule(name)
            "::" -> RuleFileRegister.getNativeRule(name)
            else -> null
        }
        rule ?: throw NoSuchElementException("rule $identifier is broken rule or does not exist")
        for ((param, arg) in arguments) {
            if (param !in rule.arguments) {
                Log.warn("Rule $identifier has no parameter $param")
            } else { // try set parameter
                val parameter = RuleFileRegister.getParameter(param)
                parameter ?: error("$identifier defined nonexistent parameter $param")
                if (!rule.arguments.trySetArgument(parameter,
                                                   arg)
                ) {
                    Log.warn("Failed to parse argument '$arg' for parameter $param")
                }
            } // end try set
        } // end for
        val missingParameters = rule.arguments.missingParameters
        if (missingParameters.isNotEmpty()) {
            throw MissingParameterException("required parameter is missing: " + missingParameters.joinToString())
        }
        return rule
    }
}
