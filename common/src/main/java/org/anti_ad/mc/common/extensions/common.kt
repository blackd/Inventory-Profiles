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

package org.anti_ad.mc.common.extensions


// ============
// tryCatch()
// ============
// tryOrElse    handle error
// tryCatch     printStackTrace
// trySwallow   swallow error

// use this functions if the exceptions is unusual and not expected (typically IOException)

inline fun <R> trySwallow(tryToRun: () -> R): R? = tryOrElse({ null },
                                                             tryToRun)

inline fun <R> trySwallow(failureValue: R,
                          tryToRun: () -> R): R = tryOrElse({ failureValue },
                                                            tryToRun)

inline fun <R> tryOrPrint(printFailure: (String) -> Unit,
                          tryToRun: () -> R): R? =
    tryOrPrint(null,
               printFailure,
               tryToRun)

inline fun <R> tryOrPrint(failureValue: R,
                          printFailure: (String) -> Unit,
                          tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        printFailure(e.toString())
        failureValue
    }
}

//inline fun <R> tryCatchAlsoPrint(printFailure: (String) -> Unit, tryToRun: () -> R): R? =
//  tryCatchAlsoPrint(null, printFailure, tryToRun)
//
//inline fun <R> tryCatchAlsoPrint(failureValue: R, printFailure: (String) -> Unit, tryToRun: () -> R): R {
//  return try {
//    tryToRun()
//  } catch (e: Throwable) {
//    e.printStackTrace()
//    printFailure(e.toString())
//    failureValue
//  }
//}

inline fun <R> tryCatch(tryToRun: () -> R): R? = tryCatch({ null },
                                                          tryToRun)

inline fun <R> tryCatch(failureValue: R,
                        tryToRun: () -> R): R = tryCatch({ failureValue },
                                                         tryToRun)

inline fun <R> tryCatch(onFailure: (Throwable) -> R,
                        tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        e.printStackTrace()
        onFailure(e)
    }
}

inline fun <R> tryOrElse(onFailure: (Throwable) -> R,
                         tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        onFailure(e)
    }
}

// ============
// Event
// ============
