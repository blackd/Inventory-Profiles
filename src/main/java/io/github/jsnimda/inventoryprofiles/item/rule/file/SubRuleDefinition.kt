package io.github.jsnimda.inventoryprofiles.item.rule.file

import io.github.jsnimda.common.Log
import io.github.jsnimda.inventoryprofiles.item.rule.Rule

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

  @Throws // may throws (no need to cache this)
  fun toRule(): Rule {
    val rule = when(prefix) {
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
        if (!rule.arguments.trySetArgument(parameter, arg)) {
          Log.warn("Failed to parse argument '$arg' for parameter $param")
        }
      } // end try set
    } // end for
    return rule
  }
}