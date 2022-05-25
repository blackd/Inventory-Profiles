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

package org.anti_ad.mc.common.vanilla.alias.glue

import org.anti_ad.mc.common.Log

var __glue_I18n_translate: (String, objects: Array<out Any?>) -> String = {string: String, objects: Array<out Any?> ->
    Log.glueError("__glue_I18n_translate not initialized!")
    string
}

object I18n {
    fun translate(string: String,
                  vararg objects: Any?): String = __glue_I18n_translate(string,
                                                                        objects)

    fun translateOrNull(string: String,
                        vararg objects: Any?): String? =
        translate(string,
                  *objects).takeIf { it != string }

    fun translateOrEmpty(string: String,
                         vararg objects: Any?): String = translateOrNull(string,
                                                                         *objects) ?: ""

    inline fun translateOrElse(string: String,
                               vararg objects: Any?,
                               elseValue: () -> String): String =
        translateOrNull(string,
                        *objects) ?: elseValue()
}
