package io.github.jsnimda.inventoryprofiles.parser

import io.github.jsnimda.common.util.IndentedData
import io.github.jsnimda.inventoryprofiles.gen.RulesLexer
import io.github.jsnimda.inventoryprofiles.gen.RulesParser
import io.github.jsnimda.inventoryprofiles.gen.RulesParser.CustomRuleEOFContext
import io.github.jsnimda.inventoryprofiles.gen.RulesParser.SubRuleContext
import io.github.jsnimda.inventoryprofiles.item.rule.custom.CustomRuleDefinition
import io.github.jsnimda.inventoryprofiles.item.rule.custom.SubRuleDefinition

object RuleParser {

  // throw SyntaxErrorException
  fun parseCustomRule(data: IndentedData): CustomRuleDefinition {
    val lines = data.lines
    val text = lines.joinToString("\n")
    try {
      val parser = text.parseBy(::RulesLexer, ::RulesParser)
      return parser.customRuleEOF().toCustomRuleDefinition()
    } catch (e: SyntaxErrorException) {
      throw e.copy(line = lines.getOrNull(e.line)?.lineNumber ?: -1)
    }
  }

  fun parseSubRule(text: String) =
    text.parseBy(::RulesLexer, ::RulesParser, lexerMode = RulesLexer.mSubRule)
      .subRuleEOF().subRule().toSubRuleDefinition()

  private fun CustomRuleEOFContext.toCustomRuleDefinition() = CustomRuleDefinition(this.head().RuleName().text,
    subRule().map { it.toSubRuleDefinition() })

  private fun SubRuleContext.toSubRuleDefinition(): SubRuleDefinition {
    with(subRuleIdentifier()) {
      val args = linkedMapOf<String, String>()
      if (REVERSE() != null) args["reverse"] = "true"
      val isCustomElseNative = AT() != null
      val name: String = when {
        AT() != null || DOUBLE_COLON() != null -> RuleName().text
        else -> (if (HASHTAG() != null) "item" else "tag").let { itemOrTag -> // #tag or item
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
      return SubRuleDefinition(isCustomElseNative, name, args.toList())
    }
  }

}