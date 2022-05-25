/*
 * Inventory Profiles Next
 * 
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

package org.anti_ad.mc.ipnext.util

fun String.sanitized(): String {
    if (this == ".." || this == ".") return "-dot_dot"
    return if (this.isNotEmpty()) {
        "-" + this.replace("/","(slash)")
            .replace("\\","(bslash)")
            .replace(":", "(colon)")
            .replace("<", "(lt)")
            .replace(">","(gt)")
            .replace("|","(pipe)")
            .replace("?","(qm)")
            .replace("*", "(asterisk)")
            .replace("\"","(dquote)")
    } else {
        this
    }
}
