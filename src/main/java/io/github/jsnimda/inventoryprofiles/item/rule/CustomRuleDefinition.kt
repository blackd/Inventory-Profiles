package io.github.jsnimda.inventoryprofiles.item.rule

class CustomRuleDefinition(val ruleName: String) {
  val subRules = mutableListOf<SubRuleEntry>()

  data class SubRuleEntry(
    val reverse: Boolean,
    val prefix: String?,
    val name: String,
    val nbt: String?,
    val arguments: List<Pair<String, String>>
  )
}