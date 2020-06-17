package io.github.jsnimda.inventoryprofiles.item.rule.natives

import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.Parameter
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import kotlin.reflect.KProperty

// ============
// Some helper functions for creating rules
// ============
internal operator fun (() -> Rule).getValue(thisRef: Any?, property: KProperty<*>) = this
internal class RuleProvider<R : Rule>(private val supplier: () -> R, private val postAction: R.() -> Unit) {
  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): () -> Rule = {
    supplier().apply(postAction)
  }.also { NATIVE_RULES_MAP[property.name] = it }
}

internal class TypedRuleProvider<T>(
  private val supplier: () -> TypedRule<T>,
  private val valueOf: (ItemType) -> T
) {
  private val args = mutableListOf<Pair<Parameter<Any>, Any>>() // additional param
  private val postActions = mutableListOf<TypedRule<T>.() -> Unit>()
  fun <P : Any> param(parameter: Parameter<P>, value: P) = // custom param on specific rule
    this.also { @Suppress("UNCHECKED_CAST") args.add(parameter as Parameter<Any> to value as Any) }

  fun post(postAction: TypedRule<T>.() -> Unit) =
    this.also { postActions.add(postAction) }

  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): () -> Rule = {
    supplier().apply {
      this.valueOf = this@TypedRuleProvider.valueOf
      arguments.apply { args.forEach { defineParameter(it.first, it.second) } }
      postActions.forEach { it() }
    }
  }.also { NATIVE_RULES_MAP[property.name] = it }
}

internal fun <R : Rule> native(supplier: () -> R, postAction: R.() -> Unit = { }) =
  RuleProvider(supplier, postAction)

internal fun rule(supplier: () -> Rule, postAction: Rule.() -> Unit = { }) =
  RuleProvider(supplier, postAction)

internal fun <T> typed(
  supplier: () -> TypedRule<T>,
  valueOf: (ItemType) -> T
) = TypedRuleProvider(supplier, valueOf)
