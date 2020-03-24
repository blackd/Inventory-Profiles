package io.github.jsnimda.inventoryprofiles.item.rule

class Arguments {
  val values = mutableMapOf<String, String>()
  private val argumentObjects = mutableMapOf<String, Any>()
  fun defineParameter(parameter: Parameter<*>, defaultValue: String) {

  }

  fun <T> defineParameter(parameter: Parameter<T>, defaultValue: T) {

  }

  operator fun <T> get(parameter: Parameter<T>): T {
    TODO()
  }
}