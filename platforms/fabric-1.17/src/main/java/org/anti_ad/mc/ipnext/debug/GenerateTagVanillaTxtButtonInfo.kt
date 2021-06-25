package org.anti_ad.mc.ipnext.debug

import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.writeToFile
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.ipnext.client.TellPlayer
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
        ItemTags.getTagGroup().toTagTxtContent().writeToFile(fileHardcoded)
        val server = Vanilla.server()
        server ?: return Unit.also { TellPlayer.chat("Not integrated server!!!") }
        //todo wtf have I done
        server.tagManager.getOrCreateTagGroup(Registry.ITEM_KEY).toTagTxtContent().writeToFile(fileDatapack)
        //server.tagManager.items.toTagTxtContent().writeToFile(fileDatapack)

    } // eventually they are the same ~.~

    val Identifier.omittedString: String // omit minecraft
        get() = if (namespace == "minecraft") path else toString()

    val String.omittedString: String // omit minecraft
        get() = removePrefix("minecraft:")

    fun TagGroup<Item>.toTagTxtContent(): String { // lets sort it
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

}