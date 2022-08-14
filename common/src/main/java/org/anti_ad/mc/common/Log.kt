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

package org.anti_ad.mc.common

import org.anti_ad.mc.common.Log.LogLevel.*
import org.anti_ad.mc.common.extensions.tryCatch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Log {
    private const val id = "inventoryprofilesnext"
    val innerLogger: Logger = LogManager.getLogger(id)

    var shouldDebug: () -> Boolean = { true }
    var shouldTrace: () -> Boolean = { true }

    fun error(message: String) = innerLogger.error("[$id] $message").also {
        onLog(ERROR,
              message)
    }

    fun error(message: String, tw: Throwable) = innerLogger.error("[$id] $message", tw).also {
        onLog(ERROR,
              message)
    }

    fun glueError(message: String) = error("$message -- NOTE --  This IS NOT IPN problem. Some other mod have changed the startup sequence and caused this. Exception that fallows may have information on which one.", Exception("Glue Error Trace"))

    fun warn(message: String) = innerLogger.warn("[$id] $message").also {
        onLog(WARN,
              message)
    }

    fun info(message: String) = innerLogger.info("[$id] $message").also {
        onLog(INFO,
              message)
    }
//  fun printDebug(message: String) = innerLogger.debug("[$id] $message")

    fun debug(message: String) = debug { message }
    fun debug(message: () -> String) {
        if (shouldDebug()) {
            val messageString = message()
            innerLogger.info("[$id/DEBUG] $messageString").also {
                onLog(DEBUG,
                      messageString)
            }
        }
    }

    // for trace
    private var indent = 0

    inline fun indent(unit: () -> Unit) {
        indent()
        unit()
        unindent()
    }

    inline fun indent(times: Int, unit: () -> Unit) {
        repeat(times) {
            indent()
        }
        unit()
        repeat(times) {
            unindent()
        }
    }

    fun indent() {
        indent++
    }

    fun unindent() {
        indent--
    }

    fun clearIndent() {
        indent = 0
    }

    val kept = mutableListOf<String>()
    var keeping: Boolean = false

    fun keep(reason: String,  unit: () -> Unit) {
        kept.clear()
        keeping = true
        val shouldTraceOld = shouldTrace
        val shouldDebugOld = shouldDebug
        tryCatch({
                     keeping = false
                     shouldDebug = { true }
                     shouldTrace = { true }
                     indent = 0
                     trace("Kept logs for $reason")
                     kept.forEach {
                         trace(it)
                     }
                     trace("Operation terminated by:")
                     it.stackTrace.forEach { element ->
                         trace(element.toString())
                     }
                 },
                 unit)
        indent = 0;
        shouldDebug = shouldDebugOld
        shouldTrace = shouldTraceOld
    }

    private inline fun getMessageString(message: () -> String): String = Thread.currentThread().id.toString() + ": " + " ".repeat(indent * 4) + message()

    fun trace(vararg messages: String) {
        messages.forEach {
            trace { it }
        }
    }


    fun traceIf(function: () -> Unit) {
        if (shouldTrace()) {
            function()
        }
    }

    fun trace(message: String) = trace { message }
    fun trace(message: String, tw: Throwable) = trace({message}, tw = tw)
    fun trace(message: () -> String) = trace(message, tw = null)
    fun trace(message: () -> String, tw: Throwable? = null) {
        if (keeping) {
            kept.add(getMessageString(message))
        }
        if (shouldTrace()) {
            val messageString = getMessageString(message)
            tw?.let {
                innerLogger.info("[$id/TRACE] $messageString", it).also {
                    onLog(TRACE,
                          messageString)
                }
            } ?: innerLogger.info("[$id/TRACE] $messageString").also {
                    onLog(TRACE,
                          messageString)
            }

        }
    }

    data class LogMessage(val level: LogLevel,
                          val message: String)

    enum class LogLevel : Comparable<LogLevel> {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ;
    }

    private val logListener = mutableSetOf<(LogMessage) -> Unit>()

    private fun onLog(level: LogLevel,
                      message: String) {
        val logMessage = LogMessage(level,
                                    message)
        logListener.forEach { it(logMessage) }
    }

    fun addLogListener(listener: (LogMessage) -> Unit) {
        logListener.add(listener)
    }

    fun removeLogListener(listener: (LogMessage) -> Unit) {
        logListener.remove(listener)
    }

    inline fun withLogListener(noinline listener: (LogMessage) -> Unit,
                               block: () -> Unit) {
        addLogListener(listener)
        block()
        removeLogListener(listener)
    }

    inline fun withLogListener(level: LogLevel,
                               noinline listener: (LogMessage) -> Unit,
                               block: () -> Unit) {
        withLogListener({ logMessage ->
                            if (logMessage.level >= level) {
                                listener(logMessage)
                            }
                        },
                        block)
    }

}
