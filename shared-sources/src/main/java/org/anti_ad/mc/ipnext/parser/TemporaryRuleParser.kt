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

package org.anti_ad.mc.ipnext.parser

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.annotation.ThrowsCaught
import org.anti_ad.mc.common.extensions.usefulName
import org.anti_ad.mc.ipnext.item.rule.EmptyRule
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.rule.file.CustomRule
import org.anti_ad.mc.ipnext.item.rule.file.MissingParameterException
import org.anti_ad.mc.ipnext.item.rule.file.SelfReferenceException

// add to reload
object TemporaryRuleParser {
    private val cachedMap = mutableMapOf<String, Rule>()

    fun parse(content: String): Rule {
        return cachedMap.getOrElse(content,
                                   { innerParse(content) })
    }

    @ThrowsCaught
    private fun innerParse(content: String): Rule {
        val subRules = try {
            @ThrowsCaught
            RuleParser.parseSubRule(content)
        } catch (e: SyntaxErrorException) {
            Log.warn("Syntax error in '$content'")
            Log.warn("  > at: ${e.line}:${e.pos} ${e.msg}")
            return EmptyRule
        } catch (e: Exception) {
            e.printStackTrace()
            return EmptyRule
        }
        return try { // see RuleFileRegister.findUsableRule() for try catch
            @ThrowsCaught
            val result = CustomRule(subRules.map { it.toRule() })
            cachedMap[content] = result
            result
        } catch (e: Exception) {
            Log.warn("Error in '$content'")
            Log.warn("  > ${e.javaClass.usefulName}: ${e.message}")
            when (e) {
                is NoSuchElementException,
                is SelfReferenceException,
                is MissingParameterException,
                -> Unit // do nothing
                else -> e.printStackTrace()
            }
            EmptyRule
        }
    }

    fun onReload() {
        cachedMap.clear()
    }
}
