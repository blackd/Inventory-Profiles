package io.github.jsnimda.inventoryprofiles.item.rule.custom

import io.github.jsnimda.common.Log
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.natives.NATIVE_RULES_MAP
import io.github.jsnimda.inventoryprofiles.item.rule.parameters.PARAMETERS_MAP

data class SubRuleDefinition(
  val isCustomElseNative: Boolean,
  val name: String,
  val arguments: List<Pair<String, String>>
) {
  private val identifier: String
    get() = "${if (isCustomElseNative) "@" else "::"}$name"

  fun toRule(): Rule =
    if (isCustomElseNative) {
      CustomRuleRegister[name]
    } else { // native
      NATIVE_RULES_MAP[name]?.invoke()
    }?.apply {
      this@SubRuleDefinition.arguments.forEach { (param, arg) ->
        when {
          !PARAMETERS_MAP.containsKey(param) ->
            Log.warn("[inventoryprofiles] Unknown parameter $param for rule $identifier ")
          param !in arguments.keys ->
            Log.warn("[inventoryprofiles] Rule $identifier has no parameter $param")
          else ->
            arguments.setByParse(PARAMETERS_MAP.getValue(param), arg)
        }
      }
    } ?: throw NoSuchElementException("rule $identifier does not exist")

}