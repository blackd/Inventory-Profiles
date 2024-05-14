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

import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.common.gen.RulesLexer
import org.anti_ad.mc.common.gen.RulesParser
import org.anti_ad.mc.common.gen.RulesParser.CustomRuleEOFContext
import org.anti_ad.mc.common.gen.RulesParser.SubRuleContext
import org.anti_ad.mc.common.util.IndentedData
import org.anti_ad.mc.ipnext.item.rule.file.RuleDefinition
import org.anti_ad.mc.ipnext.item.rule.file.SubRuleDefinition

object RuleParser {

    @MayThrow // throw SyntaxErrorException
    fun parseRuleDefinition(data: IndentedData): RuleDefinition {
        val lines = data.lines
        val text = lines.joinToString("\n") { it.rawText }
        try {
            return parseRuleDefinition(text)
        } catch (e: SyntaxErrorException) {
            throw e.copy(
                line = lines.getOrNull(e.line - 1)?.lineNumber ?: -1, // line number starts at 1 ~.~
                pos = e.pos + 1 // antlr start at 0 but text editor start at 1
            )
        }
    }

    @MayThrow // may throws
    fun parseSubRule(content: String): List<SubRuleDefinition> =
        content.parseBy(::RulesLexer,
                        ::RulesParser,
                        lexerMode = RulesLexer.mSubRule)
            .subRuleEOF().subRule().map { it.toSubRuleDefinition() }

    // ============
    // private
    // ============
    private fun parseRuleDefinition(content: String): RuleDefinition {
        val parser = content.parseBy(::RulesLexer,
                                     ::RulesParser)
        return parser.customRuleEOF().toRuleDefinition()
    }

    private fun CustomRuleEOFContext.toRuleDefinition() =
        RuleDefinition(this.head().RuleName().text,
                       subRuleEOF().subRule().map { it.toSubRuleDefinition() })

    private fun SubRuleContext.toSubRuleDefinition(): SubRuleDefinition {
        with(subRuleIdentifier()) {
            val args = linkedMapOf<String, String>()
            if (REVERSE() != null) args["reverse"] = "true"
            val prefix = AT()?.let { "@" } ?: "::"
            val name: String = when {
                AT() != null || DOUBLE_COLON() != null -> RuleName().text
                else -> (HASHTAG()?.let { "tag" } ?: "item").let { itemOrTag -> // #tag or item
                    NBT()?.text?.let {
                        args["require_nbt"] = "required"
                        args["nbt"] = it
                    }
                    args["${itemOrTag}_name"] = NamespacedId().text
                    "is_$itemOrTag"
                }
            }
            arguments()?.pair()?.forEach {
                args[it.Parameter().text] = it.Argument().text
            }
            return SubRuleDefinition(prefix,
                                     name,
                                     args.toList())
        }
    }

}
