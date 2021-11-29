package org.anti_ad.mc.ipnext.item.rule.parameter

import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.common.extensions.usefulName
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.ipnext.item.NbtUtils
import org.anti_ad.mc.ipnext.item.rule.ArgumentType
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.parser.TemporaryRuleParser

object StringArgumentType : ArgumentType<String> {
    override fun toString(value: String) = value
    override fun parse(argument: String) = argument
}

object BooleanArgumentType : ArgumentType<Boolean> {
    override fun toString(value: Boolean) = value.toString()
    override fun parse(argument: String) =
        argument.takeIf { it == "true" || it == "false" }?.toBoolean()
}

class EnumArgumentType<T : Enum<T>>(val enumClass: Class<T>) : ArgumentType<T> {
    override fun toString(value: T) = value.name.lowercase()
    override fun parse(argument: String) =
        trySwallow {
            java.lang.Enum.valueOf(enumClass,
                                   argument.uppercase())
        }
}

object NbtArgumentType : ArgumentType<NbtCompound> {
    override fun toString(value: NbtCompound) = value.toString()
    override fun parse(argument: String) = NbtUtils.parseNbt(argument)
}

object RuleArgumentType : ArgumentType<Rule> {
    override fun toString(value: Rule) = "[${value.javaClass.usefulName}]" //value.toString() // todo
    override fun parse(argument: String) = TemporaryRuleParser.parse(argument)
}

object TagNameArgumentType : ArgumentType<ItemTypeMatcher> {
    override fun toString(value: ItemTypeMatcher) = value.toString()
    override fun parse(argument: String) = ItemTypeMatcher.forTag(argument)
}

object ItemNameArgumentType : ArgumentType<ItemTypeMatcher> {
    override fun toString(value: ItemTypeMatcher) = value.toString()
    override fun parse(argument: String) = ItemTypeMatcher.forItem(argument)
}

object NbtPathArgumentType : ArgumentType<NbtUtils.NbtPath> {
    override fun toString(value: NbtUtils.NbtPath) = value.toString()
    override fun parse(argument: String) = NbtUtils.NbtPath.of(argument)
}
