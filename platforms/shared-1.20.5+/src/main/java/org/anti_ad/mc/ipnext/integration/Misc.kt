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

package org.anti_ad.mc.ipnext.integration

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.ipnext.Log
import kotlinx.serialization.json.Json as HiddenJson


internal fun logError(th: Throwable,
                     id: String,
                     fromUserInput: Boolean) {
    if (fromUserInput) {
        TellPlayer.chat("Error parsing hint file: '$id'. Error: '${th.message}'")
    }
    Log.error("Unable to parse hint file: '$id'. Error: ${th.message}", th)
}


internal inline fun <R> tryLog(id: String,
                              fromUserInput: Boolean,
                              onFailure: (Throwable, String, Boolean) -> R,
                              tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        onFailure(e, id, fromUserInput)
    }
}


internal val json = HiddenJson {
    ignoreUnknownKeys = true
    prettyPrint = true
}
