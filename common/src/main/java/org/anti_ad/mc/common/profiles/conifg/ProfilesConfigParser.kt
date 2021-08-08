package org.anti_ad.mc.common.profiles.conifg


import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.common.gen.ProfilesLexer
import org.anti_ad.mc.common.gen.ProfilesParser
import org.anti_ad.mc.ipnext.parser.parseBy

object ProfilesConfig {

    @MayThrow // throw SyntaxErrorException
    fun parseRuleDefinition(text: String) {
        val profiles = getProfiles(text)
        if (Log.shouldTrace()) {
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

    fun addProfile(p: ProfileData) {

    }

}

fun List<ProfileData>.dump() {
    Log.trace("Loaded fallowing profiles...")
    Log.clearIndent()
    Log.indent {
        forEach { profile ->
            Log.trace {
                if (profile.active != null) {
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
                            add(ProfileItemData(itemDef.itemName().text.removeSurrounding("\""), mutableListOf<ProfileEnchantmentData>().apply {
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
                           val enchantments: List<ProfileEnchantmentData>) {

    override fun toString(): String {
        return if(enchantments.isEmpty()) {
            "\"$itemId\""
        } else {
            "\"$itemId\"" +  " -> [" + enchantments.joinToString(separator = ",") { it.toString() } + "]"
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
    val testConfig = """profile Main
    HOT1
        "minecraft:netherite_axe" -> [{id:"minecraft:efficiency",lvl:5},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
        "minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
    HOT2
        "minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
        "minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]

profile PvP activate HOT5
    HOT5
        "minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
        "minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
    OFFHAND
        "minecraft:netherite_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
        "minecraft:diamond_axe" -> "Enchantments" : [{id:"minecraft:efficiency",lvl:5s},{id:"minecraft:silk_touch",lvl:1s},{id:"minecraft:smite",lvl:5s},{id:"minecraft:unbreaking",lvl:3s}]
        "minecraft:diamond_axe"
"""

    val r = ProfilesConfig.getProfiles(testConfig);
    println(ProfilesConfig.asString(r))
}