package io.github.jsnimda.inventoryprofiles.parser

import io.github.jsnimda.common.util.IndentedData
import io.github.jsnimda.inventoryprofiles.gen.RulesLexer
import io.github.jsnimda.inventoryprofiles.gen.RulesParser
import io.github.jsnimda.inventoryprofiles.gen.RulesParser.CustomRuleEOFContext
import io.github.jsnimda.inventoryprofiles.gen.RulesParser.SubRuleContext
import io.github.jsnimda.inventoryprofiles.item.rule.file.RuleDefinition
import io.github.jsnimda.inventoryprofiles.item.rule.file.SubRuleDefinition

object RuleParser {

  @Throws // throw SyntaxErrorException
  fun parseRuleDefinition(data: IndentedData): RuleDefinition {
    val lines = data.lines
    val text = lines.joinToString("\n") { it.rawText }
    try {
      return parseRuleDefinition(text)
    } catch (e: SyntaxErrorException) {
      throw e.copy(line = lines.getOrNull(e.line)?.lineNumber ?: -1)
    }
  }

  @Throws // may throws
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