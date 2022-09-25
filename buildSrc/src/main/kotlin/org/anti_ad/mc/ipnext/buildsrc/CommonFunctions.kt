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

package org.anti_ad.mc.ipnext.buildsrc

import org.gradle.api.Task

fun addTaskToDepTree(indent: Int, task: Task, cache: MutableSet<String>): String {
    var dt = ""
    val name = "${task.project.name}:${task.name}"
    if (!cache.contains(name)) {

        0.rangeTo(indent).forEach {
            dt += "  "
        }
        cache.add(name)
        dt += "$name\n"
        task.taskDependencies.getDependencies(task).forEach {

            dt += addTaskToDepTree(indent + 1, it, cache)
        }
    }
    return dt
}
