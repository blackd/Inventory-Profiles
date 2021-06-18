package org.anti_ad.mc.ipnext.parser

import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.common.util.IndentedData
import org.anti_ad.mc.ipnext.gen.RulesLexer
import org.anti_ad.mc.ipnext.gen.RulesParser
import org.anti_ad.mc.ipnext.gen.RulesParser.CustomRuleEOFContext
import org.anti_ad.mc.ipnext.gen.RulesParser.SubRuleContext
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
    content.parseBy(::RulesLexer, ::RulesParser, lexerMode = RulesLexer.mSubRule)
      .subRuleEOF().subRule().map { it.toSubRuleDefinition() }

  // ============
  // private
  // ============
  private fun parseRuleDefinition(content: String): RuleDefinition {
    val parser = content.parseBy(::RulesLexer, ::RulesParser)
    return parser.customRuleEOF().toRuleDefinition()
  }

  private fun CustomRuleEOFContext.toRuleDefinition() =
    RuleDefinition(this.head().RuleName().text, subRuleEOF().subRule().map { it.toSubRuleDefinition() })

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
      return SubRuleDefinition(prefix, name, args.toList())
    }
  }

}