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

package org.anti_ad.mc.ipnext.profiles.config


import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.common.extensions.tryOrPrint
import org.anti_ad.mc.common.gen.ProfilesLexer
import org.anti_ad.mc.common.gen.ProfilesParser
import org.anti_ad.mc.ipnext.parser.parseBy

object ProfilesConfig {

    @MayThrow // throw SyntaxErrorException
    fun parseRuleDefinition(text: String) {
        tryOrPrint(Log::warn) {
            val profiles = getProfiles(text)
            profiles.dump()
        }
    }

    @MayThrow // may throws
    fun getProfiles(content: String): List<ProfileData> =
            content.parseBy(::ProfilesLexer,
                            ::ProfilesParser,
                            lexerMode = ProfilesLexer.DEFAULT_MODE).script().toProfileList()

    fun asString(profiles: List<ProfileData>): String {
        var s = ""
        profiles.forEach{
             s += "${it}\n"
        }
        return s
    }
}

fun List<ProfileData>.dump() {
    Log.trace("Loaded fallowing profiles...")
    Log.clearIndent()
    Log.indent {
        forEach { profile ->
            Log.trace {
                if (profile.active != ProfileSlotId.NONE) {
                    "${profile.name} activate slot ${profile.active.name}"
                } else {
                    profile.name
                }
            }
            Log.indent {
                profile.slots.forEach { slotDef ->
                    Log.trace(slotDef.id.name)
                    Log.indent {
                        slotDef.items.forEach { item ->
                            val i = item.itemId + " -> " +
                                    item.enchantments.joinToString { enchContext ->
                                        "${enchContext.id}:${enchContext.level}"
                                    }
                            Log.trace(i)
                        }
                    }
                }
            }
        }
    }
    Log.clearIndent()
}

private fun ProfilesParser.ScriptContext.toProfileList(): List<ProfileData> {
    return mutableListOf<ProfileData>().apply {
        profile().forEach { profile ->
            add(ProfileData(profile.Id().text, ProfileSlotId.valueOfOrFAKE(profile.activeSlotName()?.text), mutableListOf<ProfileSlot>().apply {
                profile.slotsDef().slotDef().forEach { slotDef ->
                    add(ProfileSlot(ProfileSlotId.valueOf(slotDef.slotname().text), mutableListOf<ProfileItemData>().apply {
                        slotDef.itemDef().forEach { itemDef ->
                            val potion: String = itemDef.potion()?.enchantment()?.name()?.NamespacedId()?.text?.removeSurrounding("\"") ?: ""
                            add(ProfileItemData(itemDef.itemName().NamespacedId().text.removeSurrounding("\""),
                                                itemDef.itemName().customName()?.STRING()?.text?.removeSurrounding("\"") ?: "",
                                                potion,
                                                mutableListOf<ProfileEnchantmentData>().apply {
                                                    itemDef.enchantments()?.enchantment()?.forEach { ench ->
                                                        add(ProfileEnchantmentData(ench.name().NamespacedId().text.removeSurrounding("\""), ench.level().toNumber()))
                                                    }
                                                }))
                        }
                    }))
                }
            }))
        }
    }
}

fun String.fromEnchantmentLevel(): Int {
    return try {
        this.substring(IntRange(0, this.length - 2)).toInt()
    } catch (e: NumberFormatException) {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            10
        }
    }
}

private fun ProfilesParser.LevelContext.toNumber(): Int {
    return try {
        this.Level().text.substring(IntRange(0, this.Level().text.length - 2)).toInt()
    } catch (e: NumberFormatException) {
        try {
            this.Level().text.toInt()
        } catch (e: NumberFormatException) {
            Log.warn("Enchantment level '${this.Level().text}' at line: ${this.start.line}, position: ${this.start.charPositionInLine} is invalid!")
            0
        }

    }
}

data class ProfileData(val name: String,
                       val active: ProfileSlotId,
                       val slots: List<ProfileSlot>,
                       val valid: Boolean = true) {

    override fun toString(): String {
        val start = if (active != ProfileSlotId.NONE) {
            "profile $name activate ${active.name}"
        } else {
            "profile $name"
        }
        return "$start\n%s\n".format(slots.joinToString(separator = "\n\t", prefix = "\t") {
            it.toString()
        })
    }

}

data class ProfileSlot(val id: ProfileSlotId,
                       val items: List<ProfileItemData>) {

    override fun toString(): String {
        return id.toString() + "\n" + items.joinToString(separator = "\n\t\t", prefix = "\t\t") { it.toString() }
    }

}

data class ProfileItemData(val itemId: String,
                           val customName: String,
                           val potion: String,
                           val enchantments: List<ProfileEnchantmentData>) {

    override fun toString(): String {
        return when {
            (!enchantments.isEmpty()) -> {
                "${itemIdString()} -> \"Enchantments\" : [" + enchantments.joinToString(separator = ",") { it.toString() } + "]"
            }
            (potion != "") -> {
                "${itemIdString()} -> \"Potion\" : {id:\"$potion\"}"
            }
            else -> {
                itemIdString()
            }
        }
    }
    private fun itemIdString(): String {
        return if (customName.isNotBlank()) {
            "\"$itemId\"(\"$customName\")"
        } else {
            "\"$itemId\""
        }
    }
}

data class ProfileEnchantmentData(val id: String,
                                 val level: Int) {

    override fun toString(): String {
        return "{id:\"$id\",lvl:$level}"
    }
}

private var slotIdToProfileSlotId: MutableMap<Int, ProfileSlotId> = mutableMapOf()

fun ProfileSlotId.addSlotId(slotId: Int) {
    slotIdToProfileSlotId[slotId] = this
}


enum class ProfileSlotId(val slotId: Int) {

    NONE(-1),
    HOT1(36),
    HOT2(37),
    HOT3(38),
    HOT4(39),
    HOT5(40),
    HOT6(41),
    HOT7(42),
    HOT8(43),
    HOT9(44),
    FEET(8),
    LEGS(7),
    CHEST(6),
    HEAD(5),
    OFFHAND(45);


    companion object {
        fun valueOfOrFAKE(v: String?): ProfileSlotId {
            if (v == null) {
                return NONE;
            }
            return valueOf(v)
        }
        fun valueOf(i: Int): ProfileSlotId {
            return slotIdToProfileSlotId.get(i) ?: NONE
        }
    }

    init {
        this.addSlotId(slotId)
    }

    fun slotId(): Int {
        return slotId
    }


}


fun main(args: Array<String>) {
    val testConfig = """profile Main activate HOT1
	HOT1
		"minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
		"minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
	HOT2
		"minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5}]
		"minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
		"minecraft:netherite_axe"
		"minecraft:diamond_axe"
	HOT3
		"minecraft:water_bucket"
	HOT4
		"minecraft:terracotta"
	CHEST
		"minecraft:netherite_chestplate"
	OFFHAND
		"minecraft:shield" -> "Enchantments" : [{id:"minecraft:unbreaking",lvl:3},{id:"minecraft:mending",lvl:1}]
	LEGS
		"minecraft:netherite_leggings" -> "Enchantments" : [{id:"minecraft:fire_protection",lvl:3},{id:"minecraft:unbreaking",lvl:3}]
	FEET
		"minecraft:netherite_boots" -> "Enchantments" : [{id:"minecraft:protection",lvl:4},{id:"minecraft:unbreaking",lvl:3}]

profile PvP activate HOT5
	HOT3
		"minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
		"minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
	HOT5
		"minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
		"minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1},{id:"minecraft:smite",lvl:5},{id:"minecraft:unbreaking",lvl:3}]
		"minecraft:diamond_axe"
	HOT4
		"minecraft:iron_pickaxe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:4},{id:"minecraft:fortune",lvl:3}]
	HOT7
		"minecraft:splash_potion" -> "Potion" : {id:"minecraft:strong_harming"}
	HOT6
		"minecraft:splash_potion" -> "Potion" : {id:"minecraft:harming"}
	HOT2
		"minecraft:splash_potion" -> "Potion" : {id:"minecraft:weakness"}
	HOT1
		"minecraft:splash_potion" -> "Potion" : {id:"minecraft:regeneration"}
	HOT8
		"minecraft:splash_potion" -> "Potion" : {id:"minecraft:long_turtle_master"}
	HOT9
    HOT1
"""

    val r = ProfilesConfig.getProfiles(testConfig);
    println(ProfilesConfig.asString(r))
}
