package io.github.jsnimda.inventoryprofiles.parser

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.IndentedData
import io.github.jsnimda.inventoryprofiles.gen.RulesLexer
import io.github.jsnimda.inventoryprofiles.gen.RulesParser
import io.github.jsnimda.inventoryprofiles.gen.RulesParser.RuleEntryContext
import io.github.jsnimda.inventoryprofiles.item.rule.CustomRuleDefinition
import io.github.jsnimda.inventoryprofiles.item.rule.CustomRuleDefinition.SubRuleEntry
import org.antlr.v4.runtime.*

/*
rules.txt format:

@my_custom_rule_name
  ::native_rule(parameter = value)
  @some_other_custom_rule
  !@reversed_rule
  #in_tag(match = [first|last])
  namespace:is_item{with_some_nbt: {}}
 */

fun parseRuleEntry(ruleEntry: RuleEntryContext): SubRuleEntry {
  val reverse = ruleEntry.REVERSE() != null
  val rid = ruleEntry.ruleIdentifier()
  val prefix: String? = rid.AT()?.text ?: rid.DOUBLE_COLON()?.text ?: rid.HASHTAG()?.text
  val name = rid.RuleName()?.text ?: rid.ItemName()!!.text
  val nbt = rid.NBT()?.text
  val arguments = ruleEntry.arguments()?.run {
    this.pair().map {
      it.Parameter().text to it.Argument().text
    }
  } ?: listOf()
  return SubRuleEntry(reverse, prefix, name, nbt, arguments)
}

class RulesFileParser(val data: IndentedData, private val fileName: String = "<unknown file>") {
  private val rawTexts = data.rawParagraph
  fun parse(): CustomRuleDefinition? {
    val parser = rulesParser(rawTexts)
    try {
      val ctx = parser.customRuleEOF()
      return CustomRuleDefinition(ctx.head().RuleName().text).apply {
        subRules.addAll(ctx.ruleEntry().map { parseRuleEntry(it) })
      }
    } catch (e: SyntaxErrorException) {
      Log.warn("Syntax Error while parsing $fileName: line ${e.line}:${e.pos} ${e.msg}")
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }
}

class SyntaxErrorException(val line: Int, val pos: Int, val msg: String) : RuntimeException()
private object RulesErrorListener : BaseErrorListener() {
  override fun syntaxError(
    recognizer: Recognizer<*, *>?,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?
  ) {
    throw SyntaxErrorException(line, charPositionInLine, msg)
  }
}

private fun rulesParser(string: String, mode: Int = Lexer.DEFAULT_MODE): RulesParser {
  val lexer = RulesLexer(CharStreams.fromString(string))
  lexer.removeErrorListeners()
  lexer.addErrorListener(RulesErrorListener)
  lexer.mode(mode)
  val tokens = CommonTokenStream(lexer)
  val parser = RulesParser(tokens)
  parser.removeErrorListeners()
  parser.addErrorListener(RulesErrorListener)
  return parser
}
