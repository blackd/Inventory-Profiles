package io.github.jsnimda.inventoryprofiles.item.rule

import io.github.jsnimda.common.vanilla.CompoundTag

interface ArgumentType<T> {
  fun validate(argument: String): Boolean
  fun parse(argument: String): T
  fun toString(value: T): String
}

object StringArgumentType : ArgumentType<String> {
  override fun validate(argument: String): Boolean = true
  override fun parse(argument: String): String = argument
  override fun toString(value: String): String = value
}

object BooleanArgumentType : ArgumentType<Boolean> {
  override fun validate(argument: String): Boolean = argument == "true" || argument == "false"
  override fun parse(argument: String): Boolean = argument.toBoolean()
  override fun toString(value: Boolean): String = value.toString()
}

class EnumArgumentType<T : Enum<T>>(val enumClass: Class<T>) : ArgumentType<T> {
  val strings = enumClass.enumConstants.map { it.name.toLowerCase() }
  override fun validate(argument: String): Boolean =
    argument.toLowerCase() in strings

  override fun parse(argument: String): T {
    return java.lang.Enum.valueOf(enumClass, argument.toUpperCase())
  }

  override fun toString(value: T): String = value.name.toLowerCase()
}

object NbtArgumentType : ArgumentType<CompoundTag> {
  override fun validate(argument: String): Boolean = TODO()
  override fun parse(argument: String): CompoundTag {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toString(value: CompoundTag): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

object ComparatorArgumentType : ArgumentType<Rule> {
  override fun validate(argument: String): Boolean = TODO()
  override fun parse(argument: String): Rule {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toString(value: Rule): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

object TagNameArgumentType : ArgumentType<String> {
  override fun validate(argument: String): Boolean = TODO()
  override fun parse(argument: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toString(value: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

object ItemNameArgumentType : ArgumentType<String> {
  override fun validate(argument: String): Boolean = TODO()
  override fun parse(argument: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toString(value: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

object NbtPathArgumentType : ArgumentType<String> {
  override fun validate(argument: String): Boolean = TODO()
  override fun parse(argument: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toString(value: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
