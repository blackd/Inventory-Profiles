package io.github.jsnimda.inventoryprofiles.item.rule

class Parameter<T : Any>(
  val name: String,
  val argumentType: ArgumentType<T>
)
