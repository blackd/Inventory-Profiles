/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2024 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipn.events.api

fun interface Action<T, R> {
    fun doIt(context: T): R?
}

abstract class ActionExecutor<T, R> {

    protected val actions: MutableList<Pair<Action<T, R>, () -> Boolean>> = mutableListOf()

    private fun add(action: Action<T, R>, condition: () -> Boolean) {
        actions.add(Pair(action, condition))
    }

    open fun doIt(context: T): R? {
        for ((action, condition) in actions) {
            if (condition()) {
                action.doIt(context)?.let {
                    if (it !is Unit) return it
                }
            }
        }
        return null
    }

    open val adder: PredicateActionBuilder
        get() {
            return PredicateActionBuilder()
        }

    open inner class PredicateActionBuilder {

        open lateinit var condition: () -> Boolean
        lateinit var action: Action<T, R>

        open fun action(action: Action<T, R>): PredicateActionBuilder {
            this.action = action
            return this
        }
        open fun predicate(condition: () -> Boolean): PredicateActionBuilder {
            this.condition = condition
            return this
        }

        fun build() {
            add(action, condition)
        }
    }
}

class SimpleActionExecutor<T> : ActionExecutor<T, Unit>()

class PredicateActionGroup<T, R>(val predicate: () -> Boolean): ActionExecutor<T, R?>(), Action<T, R?> {

    override fun doIt(context: T): R? {
        if (predicate()) {
            return super.doIt(context)
        }
        return null
    }
}
