package io.github.jsnimda.inventoryprofiles.item.rule.parameter

import io.github.jsnimda.common.util.trySwallow
import io.github.jsnimda.common.vanilla.alias.CompoundTag
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.NbtUtils
import io.github.jsnimda.inventoryprofiles.item.rule.ArgumentType
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.parser.TemporaryRuleParser

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
  override fun toString(value: T) = value.name.toLowerCase()
  override fun parse(argument: String) =
    trySwallow { java.lang.Enum.valueOf(enumClass, argument.toUpperCase()) }
}

object NbtArgumentType : ArgumentType<CompoundTag> {
  override fun toString(value: CompoundTag) = value.toString()
  override fun parse(argument: String) = NbtUtils.parseNbt(argument)
}

object RuleArgumentType :  ArgumentType<Rule> {
  override fun toString(value: Rule) = "(todo)" // todo
  override fun parse(argument: String) = TemporaryRuleParser.parse(argument)
}

object TagNameArgumentType : ArgumentType<ItemTypeMatcher> {
  override fun toString(value: ItemTypeMatcher) = value.toString()
  override fun parse(argument: String): ItemTypeMatcher? {
    TODO("Not yet implemented")
  }
}

object ItemNameArgumentType : ArgumentType<ItemTypeMatcher> {
  override fun toString(value: ItemTypeMatcher) = value.toString()
  override fun parse(argument: String): ItemTypeMatcher? {
    TODO("Not yet implemented")
  }
}

object NbtPathArgumentType : ArgumentType<String> {
  override fun parse(argument: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toString(value: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
