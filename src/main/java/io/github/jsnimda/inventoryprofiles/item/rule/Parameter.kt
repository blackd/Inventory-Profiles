package io.github.jsnimda.inventoryprofiles.item.rule

class Parameter<T>(
  val name: String,
  val argumentType: ArgumentType<T>,
  val required: Boolean = false
)
