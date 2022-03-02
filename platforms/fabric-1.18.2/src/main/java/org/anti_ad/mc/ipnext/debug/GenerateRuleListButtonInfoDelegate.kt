package org.anti_ad.mc.ipnext.debug

import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.usefulName
import org.anti_ad.mc.common.extensions.writeToFile
import org.anti_ad.mc.common.gui.widgets.ConfigButtonClickHandler
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.item.rule.natives.NativeRules
import org.anti_ad.mc.ipnext.item.rule.parameter.BooleanArgumentType
import org.anti_ad.mc.ipnext.item.rule.parameter.EnumArgumentType
import org.anti_ad.mc.ipnext.item.rule.parameter.NativeParameters

object GenerateRuleListButtonInfoDelegate : ConfigButtonClickHandler() {
    private val file = VanillaUtil.configDirectory("inventoryprofilesnext") / "native_rules.txt"

    override fun onClick(guiClick: () -> Unit) {
        var s = "Parameter:\n"
        for ((name, parameter) in NativeParameters.map) {
            s += "    $name: " + when (val arg = parameter.argumentType) {
                is BooleanArgumentType -> "true/false"
                is EnumArgumentType ->
                    arg.enumClass.enumConstants?.joinToString("/") {
                        ((it as? Enum<*>)?.name ?: it.toString()).lowercase()
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