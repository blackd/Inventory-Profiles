package org.anti_ad.mc.ipnext.debug


import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.createDirectories
import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.plus
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Registry
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
            Registry.CONTAINER.ids.forEach {
                if (it.namespace != "minecraft") {
                    namespaces.add(it.namespace)
                    val inBlocks = Registry.BLOCK.ids.contains(it)
                    val inItems = Registry.ITEM.ids.contains(it)
                    val inBlockEntities = Registry.BLOCK_ENTITY_TYPE.ids.contains(it)
                    if (inBlocks) {
                        blocks.getOrPut(it.namespace) {
                            mutableListOf()
                        }.add(it.path)
                    }
                    if (inItems && !inBlocks && !inBlockEntities) {
                        items.getOrPut(it.namespace) {
                            mutableListOf()
                        }.add(it.path)
                    }
                    if (inBlockEntities && !inItems && !inBlocks) {
                        blockEntities.getOrPut(it.namespace) {
                            mutableListOf()
                        }.add(it.path)
                    }
                    val count = inBlockEntities + inBlocks + inItems
                    if (count == 0) {
                        unknowns.getOrPut(it.namespace) {
                            mutableListOf()
                        }.add(it.path)
                    }
                    if (count > 1) {
                        val line = "$it  ->  "  + (if (inBlocks) "block, " else "") + (if (inItems) "items, " else "") + (if (inBlockEntities) "blockEntities" else "")
                        multis.add(line)
                    }
                }
            }
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
