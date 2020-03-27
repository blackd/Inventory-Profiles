package io.github.jsnimda.inventoryprofiles.item.rule

class Arguments {
  private val defaultValues = mutableMapOf<String, Any?>()
  private val values = mutableMapOf<String, Any?>()
  val initialized // all required parameter is configured
    get() = values.values.all { it != null }
  val keys
    get() = defaultValues.keys

  private fun define(key: String, value: Any?) {
    defaultValues[key] = value
    values[key] = value
  }

  fun <T : Any> defineParameter(parameter: Parameter<T>, defaultValue: T) {
    define(parameter.name, defaultValue)
  }

  fun defineParameter(parameter: Parameter<*>) {  // required parameter
    define(parameter.name, null)
  }

  @Suppress("UNCHECKED_CAST")
  operator fun <T : Any> get(parameter: Parameter<T>): T =
    values.getValue(parameter.name) as T

  operator fun <T : Any> set(parameter: Parameter<T>, value: T) {
    values[parameter.name] = value
  }
}