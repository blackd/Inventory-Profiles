package org.anti_ad.mc.ipnext.item.rule.file

import com.yevdo.jwildcard.JWildcard
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.vanilla.alias.`(REGISTRIES-BLOCK-IDS)`
import org.anti_ad.mc.common.vanilla.alias.`(REGISTRIES-BLOCK_ENTITY_TYPES-IDS)`

import org.anti_ad.mc.common.vanilla.alias.`(REGISTRIES-ITEM-IDS)`
import java.util.*

private val WHITESPACE = "\\s+".toRegex()

private val itemSet: SortedSet<String> = getAllItemsSet()

fun List<String>.preprocessRules(): List<String> {
    val res = mutableListOf<String>()
    forEach {
        res.addAll(it.processWildcards(itemSet))
    }
    Log.trace {
        "Expanded Rules:\n\t|${res.joinToString(separator = "\n\t|")}"
    }
    return res
}

fun String.getIndent(): String {
    var s = ""
    forEach {  char ->
        if (char == ' ' || char == '\t' || char == '!') {
            s += char
        } else {
            return@forEach
        }
    }
    return s
}

fun String.processWildcards(candidates: SortedSet<String>): List<String> {
    val res = if (contains('*') || contains('?')) {
        val indent = getIndent()
        val collect = mutableListOf<String>()
        var expansionScript: MutableList<Any> = mutableListOf()
        this.removePrefix(indent).split(" ", "\t").forEach { wc ->

            if (wc.contains('*') || wc.contains('?')) {
                val toExpand = if (!wc.contains(':')) {
                    "minecraft:$wc"
                } else {
                    wc
                }
                val expanded = mutableListOf<String>()
                candidates.forEach {
                    if (JWildcard.matches(toExpand, it)) {
                        expanded.add(it)
                    }
                }
                expansionScript.add(expanded)
            } else if(wc.isNotBlank()) {
                expansionScript.add(wc)
            }
        }
        expansionScript = collapseStrings(expansionScript)
        explode("", expansionScript, collect)
        collect.map {
            "$indent$it"
        }
    } else {
        mutableListOf(this)
    }
    return res.ifEmpty {
        mutableListOf(this.replace("*", "").replace("?",""))
    }
}

private fun concatRule(start: String, rest: String): String {
    return if (start.isNotBlank()) {
        "$start $rest"
    } else {
        rest
    }
}

private fun explode(start: String, script: List<Any>, target: MutableList<String>) {
    if (script.isNotEmpty()) {
        if (script[0] is String) {
            val root = concatRule(start, script[0].toString())
            if (script.size > 1) {
                explode(root, script.subList(1, script.size ), target)
            } else {
                target.add(root)
            }
        } else {
            (script[0] as List<*>).forEach {
                val root = concatRule(start, it.toString())
                if (script.size > 1) {
                    explode(root, script.subList(1, script.size), target)
                } else {
                    target.add(root)
                }
            }
        }
    }
}

private fun collapseStrings(expansionScript: MutableList<Any>):  MutableList<Any> {
    val res = mutableListOf<Any>()
    var accumulator = ""
    expansionScript.forEach {
        accumulator = if (it is String) {
            "$accumulator $it"
        } else {
            res.add(accumulator)
            res.add(it)
            ""
        }
    }
    if (accumulator.isNotBlank()) {
        res.add(accumulator)
    }
    return res
}


fun getAllItemsSet(): SortedSet<String> {


    val res = mutableSetOf<String>()

    `(REGISTRIES-BLOCK-IDS)`.forEach {
        res.add("${it.namespace}:${it.path}")
    }
    `(REGISTRIES-ITEM-IDS)`.forEach {
        res.add("${it.namespace}:${it.path}")
    }
    `(REGISTRIES-BLOCK_ENTITY_TYPES-IDS)`.forEach {
        res.add("${it.namespace}:${it.path}")
    }
    return res.toSortedSet()
}
