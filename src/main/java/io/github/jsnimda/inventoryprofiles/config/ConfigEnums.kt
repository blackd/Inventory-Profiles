package io.github.jsnimda.inventoryprofiles.config

import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.inventoryprofiles.item.rule.Rule
import io.github.jsnimda.inventoryprofiles.item.rule.file.RuleFileRegister

private const val ENUM = "inventoryprofiles.enum"

enum class SortingMethod(private val ruleName: String?) {
  DEFAULT("default"),
  ITEM_NAME("item_name"),
  ITEM_ID("item_id"),
  RAW_ID("raw_id"),
  CUSTOM(null);

  val rule: Rule
    get() = ruleName?.let { RuleFileRegister.getCustomRuleOrEmpty(it) } ?: TODO("custom rule")

  override fun toString(): String =
    I18n.translate("$ENUM.sorting_method.${name.toLowerCase()}")
}

enum class SortingMethodIndividual {
  GLOBAL,
  DEFAULT,
  ITEM_NAME,
  ITEM_ID,
  RAW_ID,
  CUSTOM;

  val rule: Rule
    get() =
      if (this == GLOBAL)
        ModSettings.SORT_ORDER.value.rule
      else
        SortingMethod.values()[ordinal - 1].rule

  override fun toString(): String =
    if (this == GLOBAL)
      I18n.translate(
        "$ENUM.sorting_method.global",
        ModSettings.SORT_ORDER.value.toString().substringBefore('(').trim()
      )
    else
      I18n.translate("$ENUM.sorting_method.${name.toLowerCase()}")
}

enum class PostAction {
  NONE,
  GROUP_IN_ROWS,
  GROUP_IN_COLUMNS,
  DISTRIBUTE_EVENLY,
  SHUFFLE,
  FILL_ONE,
  REVERSE;

  override fun toString(): String =
    I18n.translate("$ENUM.post_action.${name.toLowerCase()}")
}

enum class ThresholdUnit {
  ABSOLUTE,
  PERCENTAGE;

  override fun toString(): String =
    I18n.translate("$ENUM.threshold_unit.${name.toLowerCase()}")
}
