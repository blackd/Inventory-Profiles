package io.github.jsnimda.inventoryprofiles.item.rule.natives

import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import kotlin.reflect.KProperty

// ============
// Some helper functions for creating rules
// ============
internal operator fun (() -> Rule).getValue(thisRef: Any?, property: KProperty<*>) = this
internal class RuleProvider(private val supplier: () -> Rule, private val postAction: Rule.() -> Unit) {
  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): () -> Rule = {
    supplier().apply(postAction)
  }.also { NATIVE_RULES_MAP[property.name] = it }
}

internal class TypedRuleProvider<T>(
  private val supplier: () -> TypedRule<T>,
  private val postAction: TypedRule<T>.() -> Unit = { },
  private val transform: (ItemType) -> T
) {
  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): () -> Rule = {
    supplier().apply {
      this.transformBy = this@TypedRuleProvider.transform
    }.apply(postAction)
  }.also { NATIVE_RULES_MAP[property.name] = it }
}

internal fun native(supplier: () -> Rule, postAction: Rule.() -> Unit = { }) =
  RuleProvider(supplier, postAction)

internal fun <T> typed(
  supplier: () -> TypedRule<T>,
  postAction: TypedRule<T>.() -> Unit = { },
  transform: (ItemType) -> T
) = TypedRuleProvider(supplier, postAction, transform)
