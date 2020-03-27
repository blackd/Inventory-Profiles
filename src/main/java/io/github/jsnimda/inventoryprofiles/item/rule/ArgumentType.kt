package io.github.jsnimda.inventoryprofiles.item.rule

interface ArgumentType<T : Any> {
  fun validate(argument: String): Boolean
  fun parse(argument: String): T
  fun toString(value: T): String
}
