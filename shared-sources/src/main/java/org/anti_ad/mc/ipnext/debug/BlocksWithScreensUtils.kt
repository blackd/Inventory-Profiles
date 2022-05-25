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

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.extensions.plus
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.Vanilla.`(sendChatMessage)`
import org.anti_ad.mc.ipnext.config.Modpacks

fun generateCommands(namespaces: MutableSet<String>,
                     blocks: MutableMap<String, MutableList<String>>,
                     blockEntities: MutableMap<String, MutableList<String>>,
                     items: MutableMap<String, MutableList<String>>,
                     alsoGiveCommands: Boolean = false): List<String> {
    val script = mutableListOf<String>()
    val blocksMax = if (blocks.isEmpty()) 0 else blocks.values.sortedByDescending { it.size }[0].size
    val itemsMax = if (items.isEmpty()) 0 else items.values.sortedByDescending  { it.size }[0].size
    val bEntMax = if (blockEntities.isEmpty()) 0 else blockEntities.values.sortedByDescending  { it.size }[0].size
    val width = maxOf(blocksMax, itemsMax, bEntMax) * 2
    if ((width+10) * (namespaces.size * 3 + 10) < 32768) {

        script.add("/fill ~-5 ~-1 ~-5 ~${namespaces.size * 3 + 5} ~-1 ~${width + 5} minecraft:black_stained_glass" )
        script.add("/fill ~ ~-1 ~ ~${namespaces.size * 3} ~-1 ~${width} minecraft:smooth_stone" )

        script.add("/fill ~-5 ~ ~-5 ~${namespaces.size * 3 + 5} ~1 ~-5 minecraft:glass")
        script.add("/fill ~-5 ~ ~-5 ~-5 ~1 ~${width + 5} minecraft:glass")
        script.add("/fill ~-5 ~ ~${width + 5} ~${namespaces.size * 3 + 5} ~1 ~${width + 5} minecraft:glass")
        script.add("/fill ~${namespaces.size * 3 + 5} ~ ~-5 ~${namespaces.size * 3 + 5} ~1 ~${width + 5} minecraft:glass")

        script.add("/fill ~-5 ~1 ~-5 ~${namespaces.size * 3 + 5} ~-1 ~-5 minecraft:smooth_stone" )
    } else {
        script.add("this thing is too big to make with single command.... that sucks")
    }
    var pos = 1
    namespaces.forEach { namespace ->
        val blocksToProcess = blocks[namespace]
        if (blocksToProcess != null) {
            script.add("/setblock ~$pos ~ ~-2 minecraft:oak_sign{Text1:\"{\\\"text\\\":\\\"$namespace\\\"}\"} destroy")

            var blockPos = 1
            blocksToProcess.forEach { blockName ->
                script.add("/setblock ~$pos ~ ~$blockPos $namespace:$blockName destroy")
                blockPos += 2
            }
            pos += 4
        }
    }
    if (alsoGiveCommands) {
        items.keys.forEach {namespace ->
            items[namespace]?.forEach {
                script.add("/give @a $namespace:$it")
            }
        }
    }
    return  script
}


fun extractBlockInfo(namespaces: MutableSet<String>,
                     blocks: MutableMap<String, MutableList<String>>,
                     items: MutableMap<String, MutableList<String>>,
                     blockEntities: MutableMap<String, MutableList<String>>,
                     unknowns: MutableMap<String, MutableList<String>>,
                     multis: MutableList<String>) {
    `(REGISTRIES-CONTAINER-IDS)`.forEach {
        if (it.namespace != "minecraft") {
            namespaces.add(it.namespace)
            val inBlocks = `(REGISTRIES-BLOCK-IDS)`.contains(it)
            val inItems = `(REGISTRIES-ITEM-IDS)`.contains(it)
            val inBlockEntities = `(REGISTRIES-BLOCK_ENTITY_TYPES-IDS)`.contains(it)
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
                val line = "$it  ->  " + (if (inBlocks) "block, " else "") + (if (inItems) "items, " else "") + (if (inBlockEntities) "blockEntities" else "")
                multis.add(line)
            }
        }
    }
}


object ModpackInputHandler: IInputHandler {

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        return Modpacks.GEN_TEST_ARENA.isActivated().ifTrue {
            Vanilla.mc().execute() {
                TellPlayer.chat("Generating...")

                val items = mutableMapOf<String, MutableList<String>>()
                val blocks = mutableMapOf<String, MutableList<String>>()
                val unknowns = mutableMapOf<String, MutableList<String>>()
                val blockEntities = mutableMapOf<String, MutableList<String>>()
                val multis = mutableListOf<String>()
                val namespaces: MutableSet<String> = mutableSetOf()

                extractBlockInfo(namespaces, blocks, items, blockEntities, unknowns, multis)

                val script = generateCommands(namespaces, blocks, blockEntities, items, true)
                script.forEach {
                    Vanilla.player().`(sendChatMessage)`(it)
                }
            }
        }
    }

}
