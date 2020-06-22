package io.github.jsnimda.common.config

fun List<IConfigOption>.toConfigElement(): IConfigElementResettableMultiple =
  CategorizedConfigOptions().apply { forEach { addConfigOption(it) } }

class CategorizedConfigOptions : ConfigOptionBase(), IConfigElementResettableMultiple {
  val categories = mutableMapOf<String, List<IConfigOption>>()
  private var mCurrentCategory: MutableList<IConfigOption>? = null
  private val currentCategory
    get() = mCurrentCategory ?: addCategory("")

  fun addCategory(categoryName: String) = mutableListOf<IConfigOption>().also {
    mCurrentCategory = it
    categories[categoryName] = it
  }

  fun addConfigOption(configOption: IConfigOption) {
    currentCategory.add(configOption)
  }

  override fun getConfigOptionsMap() = getConfigOptionsMapFromList()

  override fun getConfigOptionsList() = categories.values.flatten()
}