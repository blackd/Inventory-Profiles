package io.github.jsnimda.common.config


class CategorizedConfigOptions : ConfigOptionBase(), IConfigElementResettableMultiple {
  val categories: LinkedHashMap<String, List<IConfigOption>> = linkedMapOf()
  private var _currentCategory: MutableList<IConfigOption>? = null
  private var currentCategory: MutableList<IConfigOption>
    get() = _currentCategory ?: addCategory("")
    set(value) {
      _currentCategory = value
    }

  fun addCategory(categoryName: String) = mutableListOf<IConfigOption>().also {
    currentCategory = it
    categories[categoryName] = it
  }

  fun addConfigOption(configOption: IConfigOption) {
    currentCategory.add(configOption)
  }

  override fun getConfigOptionsMap() = getConfigOptionsMapFromList()

  override fun getConfigOptionsList() = categories.values.flatten()
}