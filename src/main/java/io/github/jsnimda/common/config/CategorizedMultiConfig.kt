package io.github.jsnimda.common.config

fun List<IConfigOption>.toMultiConfig(): CategorizedMultiConfig =
  CategorizedMultiConfig().apply { forEach { addConfigOption(it) } }

class CategorizedMultiConfig : ConfigOptionBase(), IConfigElementResettableMultiple {
  val categories = mutableMapOf<String, List<IConfigOption>>()
  private var currentCategory: MutableList<IConfigOption>? = null

  fun addCategory(categoryName: String) = mutableListOf<IConfigOption>().also {
    currentCategory = it
    categories[categoryName] = it
  }

  fun addConfigOption(configOption: IConfigOption) {
    (currentCategory ?: addCategory("")).add(configOption)
  }

  override fun getConfigOptionMap() = getConfigOptionMapFromList()
  override fun getConfigOptionList() = categories.values.flatten()
}