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


import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.createDirectories
import org.anti_ad.mc.common.extensions.div

import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.vanilla.Vanilla

import org.anti_ad.mc.common.vanilla.glue.VanillaUtil

import kotlin.io.path.bufferedWriter

object GenerateTagsAsJson: AbstractBlockScreenScriptGenerator() {

    override fun onClick(widget: ButtonWidget) {
        (VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens").createDirectories()
        Vanilla.mc().execute() {
            TellPlayer.chat("Generating...")

            val items = mutableMapOf<String, MutableList<String>>()
            val blocks = mutableMapOf<String, MutableList<String>>()
            val unknowns = mutableMapOf<String, MutableList<String>>()
            val blockEntities = mutableMapOf<String, MutableList<String>>()
            val multis = mutableListOf<String>()
            val namespaces: MutableSet<String> = mutableSetOf()

            extractBlockInfo(namespaces, blocks, items, blockEntities, unknowns, multis)

            TellPlayer.chat("Generating items-with-screens.txt")
            store(items, fileItems.bufferedWriter())
            TellPlayer.chat("Generating blocks-with-screens.txt")
            store(blocks, fileBlocks.bufferedWriter())
            TellPlayer.chat("Generating blocks-entities-with-screens.txt")
            store(blockEntities, fileEntities.bufferedWriter())
            TellPlayer.chat("Generating unknown-screens.txt")
            store(unknowns, fileUnknown.bufferedWriter())

            with(fileMulti.bufferedWriter()) {
                multis.forEach {
                    append(it).append("\n")
                }
                close()
            }

            with(fileNamespaces.bufferedWriter()) {
                namespaces.forEach {
                    append(it).append("\n")
                }
                close()
            }

            val script = generateCommands(namespaces, blocks, blockEntities, items)
            with(fileScript.bufferedWriter()) {
                script.forEach {
                    append(it).append("\n")
                }
                close()
            }

        }
    }
}
