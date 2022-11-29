/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
