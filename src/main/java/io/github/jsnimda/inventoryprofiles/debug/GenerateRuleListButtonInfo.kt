package io.github.jsnimda.inventoryprofiles.debug

import io.github.jsnimda.common.gui.widgets.ButtonWidget
import io.github.jsnimda.common.gui.widgets.ConfigButtonInfo
import io.github.jsnimda.common.util.div
import io.github.jsnimda.common.util.usefulName
import io.github.jsnimda.common.util.writeToFile
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.inventoryprofiles.item.rule.native.NativeRules
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.BooleanArgumentType
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.EnumArgumentType
import io.github.jsnimda.inventoryprofiles.item.rule.parameter.NativeParameters

object GenerateRuleListButtonInfo : ConfigButtonInfo() {
  val file = VanillaUtil.configDirectory("inventoryprofiles") / "native_rules.txt"

  override val buttonText: String
    get() = "generate native_rules.txt"

  override fun onClick(widget: ButtonWidget) {
    var s = "Parameter:\n"
    for ((name, parameter) in NativeParameters.map) {
      s += "    $name: " + when (val arg = parameter.argumentType) {
        is BooleanArgumentType -> "true/false"
        is EnumArgumentType ->
          arg.enumClass.enumConstants?.joinToString("/") { ((it as? Enum<*>)?.name ?: it.toString()).toLowerCase() }
        else -> "[${arg.javaClass.usefulName}]"
      }
      s += "\n"
    }
    s += "\n"
    s += "Native Rules:\n"
    for ((name, ruleSupplier) in NativeRules.map) {
      s += "    ::$name"
      val rule = ruleSupplier()
      val pairList = rule.arguments.dumpAsPairList()
      if (pairList.isNotEmpty()) {
        s += "\n        (${pairList.joinToString { (k, v) -> "$k = $v" }})"
      }
      s += "\n"
    }
    s.writeToFile(file)
  }

}