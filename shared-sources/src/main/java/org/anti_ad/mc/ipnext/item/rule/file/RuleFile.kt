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
import org.anti_ad.mc.common.annotation.ThrowsCaught
import org.anti_ad.mc.common.util.IndentedDataFileParser
import org.anti_ad.mc.ipnext.parser.RuleParser
import org.anti_ad.mc.ipnext.parser.SyntaxErrorException

/*
  RuleFile:
  [content] -> [RuleDefinition]+
  get [RuleDefinition]: fail -> log and remove
 */
class RuleFile(val fileName: String,
               private val content: String) {
    // for same name definition, later overrides former
    val rulesMap = mutableMapOf<String, MutableList<RuleDefinition>>()

    @ThrowsCaught
    fun parseContent() {
        Log.trace("[-] Parsing file $fileName")
        val data = IndentedDataFileParser.parse(content.lines().preprocessRules(),
                                                fileName)
        for (subData in data.subData) {
            Log.trace("    - parsing rule: ${subData.text}")
            try {
                @ThrowsCaught
                val definition = RuleParser.parseRuleDefinition(subData)
                // then add to rules
                rulesMap.getOrPut(definition.ruleName) { mutableListOf() }.add(definition)
            } catch (e: SyntaxErrorException) {
                Log.warn("Syntax error in '$fileName' (${subData.text})")
                Log.warn("  > at: ${e.line}:${e.pos} ${e.msg}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Log.trace {
            "    Added ${rulesMap.values.flatten().size} rules: " +
                    rulesMap.map { (name, list) -> if (list.size == 1) name else "$name x${list.size}" }
        }
        Log.trace("    $fileName parse finished")
    }
}
