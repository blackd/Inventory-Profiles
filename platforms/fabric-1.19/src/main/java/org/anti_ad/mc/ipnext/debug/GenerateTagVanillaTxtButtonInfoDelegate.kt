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

//import org.anti_ad.mc.common.vanilla.alias.TagGroup
import net.minecraft.util.registry.RegistryEntryList
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.gui.widgets.ConfigButtonClickHandler
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.Item
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import kotlin.io.path.bufferedWriter
import kotlin.io.path.name

// ============
// vanillamapping code depends on mappings
// ============

object GenerateTagVanillaTxtButtonInfoDelegate : ConfigButtonClickHandler() {
    val fileDatapack = VanillaUtil.configDirectory("inventoryprofilesnext") / "tags.combined.txt"

    private fun RegistryEntryList.Named<Item>.toMutableListOf(): MutableList<Identifier> {

        val res = mutableListOf<Identifier>()
        this.forEach {
            Registry.ITEM.getId(it.value()).also { id ->
                if (id != Registry.ITEM.defaultId) {
                    res.add(id)
                }
            }
        }
        return res
    }


    override fun onClick(guiClick: () -> Unit) {
        TellPlayer.chat("Generating ${fileDatapack.name} ...")
        val server = Vanilla.server()
        server ?: return Unit.also { TellPlayer.chat("This works best in single player game... Giving up!") }

        val m = mutableMapOf<Identifier, MutableList<Identifier>>()
        Registry.ITEM.streamTagsAndEntries().forEach {
            m[it.first.id] = it.second.toMutableListOf()
        }
        with (fileDatapack.bufferedWriter()) {
            m.keys.sorted().forEach { key ->
                this.appendLine("#${key.omittedString}")
                m[key]?.sorted()?.forEach { value ->
                    this.appendLine("    ${value.omittedString}")
                }
            }
            this.close()
        }

        Log.traceIf {
            Registry.ITEM.streamTagsAndEntries().forEach {
                Log.trace {
                    "${it.first.id} ->"
                }
                Log.indent(4) {
                    it.second.stream().forEach { entry ->
                        Log.trace {
                            "${entry.key.get().value})"
                        }
                    }
                }
            }
        }

        //server.tagManager.getOrCreateTagGroup(Registry.ITEM_KEY).toTagTxtContent().writeToFile(fileDatapack)
        //server.tagManager.items.toTagTxtContent().writeToFile(fileDatapack)

    } // eventually they are the same ~.~

    private val Identifier.omittedString: String // omit minecraft
        get() = if (namespace == "minecraft") path else toString()

    private val String.omittedString: String // omit minecraft
        get() = removePrefix("minecraft:")
/*
    private fun TagGroup<Item>.toTagTxtContent(): String { // lets sort it
        val list = mutableListOf<Pair<String, MutableList<String>>>()
        for ((identifier, tag) in tags) {
            list += identifier.toString() to tag.values().map { Registry.ITEM.`(getIdentifier)`(it).toString() }
                .toMutableList()
        }
        list.sortBy { it.first }
        list.forEach { it.second.sort() }
        val omittedList = list.map { (a, b) -> a.omittedString to b.map { it.omittedString } }
        return omittedList.flatMap { (a, b) ->
            listOf("#$a") + b.map { "    $it" } + listOf("")
        }.joinToString("\n")
    }
*/
}
