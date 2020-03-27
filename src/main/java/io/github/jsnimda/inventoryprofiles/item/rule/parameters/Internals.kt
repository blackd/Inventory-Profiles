package io.github.jsnimda.inventoryprofiles.item.rule.parameters

import io.github.jsnimda.inventoryprofiles.item.rule.*
import kotlin.reflect.KProperty

// ============
// Some helper functions for creating parameters
// ============

internal class ParameterProvider<T : Any>(private val argumentType: ArgumentType<T>) {
  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Parameter<T> =
    Parameter(property.name, argumentType).also { PARAMETERS_MAP[property.name] = it }
}

internal operator fun <T : Any> Parameter<T>.getValue(thisRef: Any?, property: KProperty<*>) = this
internal fun <T : Any> parameterOf(argumentType: ArgumentType<T>) = ParameterProvider(argumentType)
internal inline fun <reified T : Enum<T>> enum() = ParameterProvider(EnumArgumentType(T::class.java))
internal val any_string = ParameterProvider(StringArgumentType)
internal val type_boolean = ParameterProvider(BooleanArgumentType)
internal val any_nbt = ParameterProvider(NbtArgumentType)
internal val any_comparator = ParameterProvider(ComparatorArgumentType)
internal val any_tag_name = ParameterProvider(TagNameArgumentType)
internal val any_item_name = ParameterProvider(ItemNameArgumentType)
internal val any_nbt_path = ParameterProvider(NbtPathArgumentType)
