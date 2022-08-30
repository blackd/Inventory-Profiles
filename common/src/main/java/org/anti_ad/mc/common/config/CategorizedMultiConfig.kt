/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.config

fun List<IConfigOption>.toMultiConfig(): CategorizedMultiConfig = CategorizedMultiConfig().apply {
    forEach {
        addConfigOption(it)
    }
}

class CategorizedMultiConfig : ConfigOptionBase(), IConfigElementResettableMultiple {
    val categories = mutableListOf<Pair<String, List<IConfigOption>>>()
    private var currentCategory: MutableList<IConfigOption>? = null

    fun addCategory(categoryName: String) = mutableListOf<IConfigOption>().also {
        currentCategory = it
        categories += categoryName to it
    }

    fun addConfigOption(configOption: IConfigOption) {
        (currentCategory ?: addCategory("")).add(configOption)
    }

    override fun getConfigOptionMap() = getConfigOptionMapFromList()
    override fun getConfigOptionList() = categories.flatMap { it.second }
}
