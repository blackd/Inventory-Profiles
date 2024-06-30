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

import org.anti_ad.mc.alias.registry.`(REGISTRIES-BLOCK-IDS)`
import org.anti_ad.mc.alias.registry.`(REGISTRIES-BLOCK_ENTITY_TYPES-IDS)`
import org.anti_ad.mc.alias.registry.`(REGISTRIES-CONTAINER-IDS)`
import org.anti_ad.mc.alias.registry.`(REGISTRIES-ITEM-IDS)`
import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.extensions.plus
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.Vanilla.`(sendCommandMessage)`
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Modpacks
import java.lang.Thread.sleep

fun generateCommands(@Suppress("UNUSED_PARAMETER")
                     namespaces: MutableSet<String>,
                     blocks: MutableMap<String, MutableList<String>>,
                     blockEntities: MutableMap<String, MutableList<String>>,
                     items: MutableMap<String, MutableList<String>>,
                     alsoGiveCommands: Boolean = false): List<String> {
    val script = mutableListOf<String>()
    val blocksMax = if (blocks.isEmpty()) 0 else blocks.values.sortedByDescending { it.size }[0].size
    val itemsMax = if (items.isEmpty()) 0 else items.values.sortedByDescending  { it.size }[0].size
    val bEntMax = if (blockEntities.isEmpty()) 0 else blockEntities.values.sortedByDescending  { it.size }[0].size
    val width = maxOf( blocksMax, itemsMax, bEntMax) * 2
    val px = Vanilla.px.toLong()
    val py = Vanilla.py.toLong()
    val pz = Vanilla.pz.toLong()

    var pos = 1



    //script.add("/fill ~ ~-1 ~ ~${len} ~-1 ~${width} minecraft:smooth_stone" )

    val playerName = Vanilla.player().gameProfile.name

    script.add("/tp $playerName ~ ~ ~ 180 0")

    //script.add("/fill ${px-5} ${py-1} ${pz + 3} ${px+width+5} ${py-1} ${pz+1} minecraft:glass")

    script.add("/fill ${px-3} ${py-1} ${pz + 4} ${px+width+2} ${py-1} $pz minecraft:glass")
    script.add("/fill ${px-4} ${py-1} ${pz + 4} ${px+width+2} ${py+1} ${pz + 4} minecraft:black_stained_glass")
    script.add("/fill ${px-4} ${py-1} ${pz + 4} ${px-4} ${py+1} ${pz - 1} minecraft:black_stained_glass")
    script.add("/fill ${px+width+3} ${py-1} ${pz + 4} ${px+width+3} ${py+1} ${pz - 1} minecraft:black_stained_glass")



    //fill ~-5 ~-1 ~3 ~25 ~-1 ~ minecraft:black_stained_glass//

    genRowScript(px, py, pz, script, playerName, width)

    blocks.keys.forEach { namespace ->
        val blocksToProcess = blocks[namespace]
        if (blocksToProcess != null) {
///setblock 1000 126 905 minecraft:oak_sign[rotation=12,waterlogged=false]{Text1:"{\"text\":\"test\"}"}
            script.add("/setblock ${px - 2} $py ${pz - pos} minecraft:oak_sign[rotation=12,waterlogged=false]{Text1:\"{\\\"text\\\":\\\"$namespace\\\"}\"} destroy")
            genRowScript(px, py, pz - pos, script, playerName, width)
            var blockPos = 0
            blocksToProcess.forEach { blockName ->
                script.add("/setblock ${px + blockPos} $py ${pz - pos} $namespace:$blockName destroy")
                blockPos += 2
                sleep(20)
            }
            pos += 4
        }
    }

    script.add("/fill ${px-3} ${py-1} ${pz - pos - 3} ${px+width+2} ${py-1} ${pz - pos} minecraft:glass")
    script.add("/fill ${px-4} ${py-1} ${pz - pos - 3} ${px+width+2} ${py+1} ${pz - pos - 3} minecraft:black_stained_glass")
    script.add("/fill ${px-4} ${py-1} ${pz - pos - 3} ${px-4} ${py+1} ${pz - pos } minecraft:black_stained_glass")
    script.add("/fill ${px+width+3} ${py-1} ${pz - pos - 3} ${px+width+3} ${py+1} ${pz - pos} minecraft:black_stained_glass")

    if (alsoGiveCommands) {
        items.keys.forEach {namespace ->
            items[namespace]?.forEach {
                script.add("/give @a $namespace:$it")
            }
        }
    }
    return  script
}

private fun genRowScript(x: Long,
                         y: Long,
                         z: Long,
                         script: MutableList<String>,
                         @Suppress("UNUSED_PARAMETER") playerName: String,
                         width: Int) {
//
    script.add("/fill ${x-4} ${y-1} $z ${x-4} ${y+1} ${z-3} minecraft:black_stained_glass")
    script.add("/fill ${x-3} ${y-1} $z ${x-1} ${y-1} ${z-3} minecraft:glass")
    script.add("/fill $x ${y-1} $z ${x + width} ${y-1} ${z-3} minecraft:smooth_stone")
    script.add("/fill ${x + width} ${y-1} $z ${x + width + 2} ${y-1} ${z-3} minecraft:glass")
    script.add("/fill ${x + width + 3} ${y-1} $z ${x + width + 3} ${y + 1} ${z-3} minecraft:black_stained_glass")
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

fun extractAllItems(items: MutableMap<String, MutableList<String>>) {
    val tmp: MutableMap<String, MutableList<String>> = mutableMapOf()
    `(REGISTRIES-ITEM-IDS)`.forEach {
        tmp.getOrPut(it.namespace) {
            mutableListOf()
        }.add(it.path)
    }
    tmp.forEach { (key, value) ->
        value.sort()
        items[key] = value
    }
}


object ModpackInputHandler: IInputHandler {

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        if (ModSettings.FOR_MODPACK_DEVS.value) {
            Modpacks.GEN_TEST_ARENA.isActivated().ifTrue {
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
                        Vanilla.player().`(sendCommandMessage)`(it)
                    }
                }
                return true
            }

            return false

        }
        return false
    }

}
