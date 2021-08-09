package org.anti_ad.mc.ipnext.debug

import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.usefulName
import org.anti_ad.mc.common.extensions.writeToFile
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.item.rule.parameter.BooleanArgumentType
import org.anti_ad.mc.ipnext.item.rule.parameter.EnumArgumentType
import org.anti_ad.mc.ipnext.item.rule.parameter.NativeParameters
import org.anti_ad.mc.ipnext.item.rule.vnative.NativeRules

object GenerateRuleListButtonInfo : ConfigButtonInfo() {
    val file = VanillaUtil.configDirectory("inventoryprofilesnext") / "native_rules.txt"

    override val buttonText: String
        get() = "generate native_rules.txt"

    override fun onClick(widget: ButtonWidget) {
        var s = "Parameter:\n"
        for ((name, parameter) in NativeParameters.map) {
            s += "    $name: " + when (val arg = parameter.argumentType) {
                is BooleanArgumentType -> "true/false"
                is EnumArgumentType ->
                    arg.enumClass.enumConstants?.joinToString("/") {
                        ((it as? Enum<*>)?.name ?: it.toString()).toLowerCase()
                    }
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