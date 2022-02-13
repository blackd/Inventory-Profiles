package org.anti_ad.mc.ipnext.debug

import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import java.io.BufferedWriter

abstract class AbstractBlockScreenScriptGenerator: ConfigButtonInfo() {

    val fileEntities = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "blocks-entities-with-screens.txt"
    val fileBlocks = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "blocks-with-screens.txt"
    val fileItems = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "item-with-screens.txt"
    val fileMulti = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "multi-screens.txt"
    val fileUnknown = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "unknown-screens.txt"
    val fileNamespaces = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "namespaces.txt"
    val fileScript = VanillaUtil.configDirectory("inventoryprofilesnext") / "auto-screens" / "script.txt"

    override val buttonText: String
        get() = "Make Generator Script"


    protected fun generateCommands(namespaces: MutableSet<String>,
                                 blocks: MutableMap<String, MutableList<String>>,
                                 blockEntities: MutableMap<String, MutableList<String>>,
                                 items: MutableMap<String, MutableList<String>>): List<String> {
        val script = mutableListOf<String>()
        val blocksMax = if (blocks.isEmpty()) 0 else blocks.values.sortedByDescending { it.size }[0].size
        val itemsMax = if (items.isEmpty()) 0 else items.values.sortedByDescending  { it.size }[0].size
        val bEntMax = if (blockEntities.isEmpty()) 0 else blockEntities.values.sortedByDescending  { it.size }[0].size
        val width = maxOf(blocksMax, itemsMax, bEntMax)
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
                    blockPos++
                }
                pos += 4
            }
        }
        return  script
    }

    protected fun store(what: Map<String, List<String>>,
                      where: BufferedWriter) {
        what.forEach { (namespace, ids) ->
            ids.forEach {
                where.append(namespace).append(":").append(it).append("\n")
            }
        }
        where.close()
    }
}