package org.anti_ad.mc

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
