package org.anti_ad.mc.ipnext.debug

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.writeToFile
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.ipnext.ingame.`(getIdentifier)`

// ============
// vanillamapping code depends on mappings
// ============

object GenerateTagVanillaTxtButtonInfo : ConfigButtonInfo() {
    val fileDatapack = VanillaUtil.configDirectory("inventoryprofilesnext") / "tags.vanilla.datapack.txt"
    val fileHardcoded = VanillaUtil.configDirectory("inventoryprofilesnext") / "tags.vanilla.hardcoded.txt"

    override val buttonText: String
        get() = "generate tags.vanilla.txt"

    override fun onClick(widget: ButtonWidget) {
        TellPlayer.chat("Generate tags.vanilla.txt")
        ItemTags.getAllTags().toTagTxtContent().writeToFile(fileHardcoded) //getCollection()
        val server = Vanilla.server()
        server ?: return Unit.also { TellPlayer.chat("Not integrated server!!!") }
        server.tags.getOrEmpty(Registry.ITEM_REGISTRY).toTagTxtContent().writeToFile(fileDatapack)
        //server.tags.items.toTagTxtContent() // func_244266_aF().itemTags.toTagTxtContent()
        //    .writeToFile(fileDatapack) // tagtagManager.items() = forge networkTagManager.items
    } // eventually they are the same ~.~

    val Identifier.omittedString: String // omit minecraft
        get() = if (namespace == "minecraft") path else toString()

    val String.omittedString: String // omit minecraft
        get() = removePrefix("minecraft:")

    fun TagContainer<Item>.toTagTxtContent(): String { // lets sort it
        val list = mutableListOf<Pair<String, MutableList<String>>>()
        for ((identifier, tag) in this.allTags) { // forge tagMap = entries
            //    list += identifier.toString() to tag.allElements.map { Registry.ITEM.`(getIdentifier)`(it).toString() }.toMutableList() // allElements = values
            list += identifier.toString() to tag.values.map { Registry.ITEM.`(getIdentifier)`(it).toString() }.toMutableList() // allElements = values
        }
        list.sortBy { it.first }
        list.forEach { it.second.sort() }
        val omittedList = list.map { (a, b) -> a.omittedString to b.map { it.omittedString } }
        return omittedList.flatMap { (a, b) ->
            listOf("#$a") + b.map { "    $it" } + listOf("")
        }.joinToString("\n")
    }

}