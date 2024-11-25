/*
 * Inventory Profiles Next
 *
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
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.ModInfo
import java.io.BufferedWriter

abstract class AbstractBlockScreenScriptGenerator: ConfigButtonInfo() {

    val fileEntities = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "blocks-entities-with-screens.txt"
    val fileBlocks = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "blocks-with-screens.txt"
    val fileItems = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "item-with-screens.txt"
    val fileAllItems = VanillaUtil.configDirectory(ModInfo.MOD_ID)  / "all-items.txt"
    val fileMulti = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "multi-screens.txt"
    val fileUnknown = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "unknown-screens.txt"
    val fileNamespaces = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "namespaces.txt"
    val fileScript = VanillaUtil.configDirectory(ModInfo.MOD_ID) / "auto-screens" / "script.txt"

    override val buttonText: String
        get() = "Make Generator Script"


    protected fun store(what: Map<String, List<String>>,
                      where: BufferedWriter) {
        what.forEach { (namespace, ids) ->
            where.append("\n# $namespace\n")
            ids.forEach {
                where.append("\t").append(namespace).append(":").append(it).append("\n")
            }
        }
        where.close()
    }
}
