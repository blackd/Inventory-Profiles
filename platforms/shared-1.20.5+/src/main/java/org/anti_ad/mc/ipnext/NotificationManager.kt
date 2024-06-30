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

package org.anti_ad.mc.ipnext

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.vanilla.Vanilla
import java.util.Timer
import java.util.concurrent.locks.*
import kotlin.concurrent.schedule

object NotificationManager {

    private val readWriteLock = ReentrantReadWriteLock()

    private val notifications = mutableListOf<String>()

    private var timer: Timer? = null

    fun addNotification(s: String) {
        readWriteLock.writeLock().lock()
        try {
            notifications.add(s)
            if (timer == null) {
                timer =  Timer("IPN Notifications timer", true).also { self ->
                    self.schedule(delay = 1000*10) {
                        Vanilla.mc().execute {
                            readWriteLock.readLock().lock()
                            try {
                                notifications.forEach { msg ->
                                    TellPlayer.chat(msg)
                                }
                                notifications.clear()
                            } finally {
                                readWriteLock.readLock().unlock()
                            }
                            readWriteLock.writeLock().lock()
                            try {
                                if (notifications.isEmpty()) {
                                    timer?.cancel()
                                    timer = null
                                }
                            } finally {
                                readWriteLock.writeLock().unlock()
                            }
                        }
                        readWriteLock.writeLock().lock()
                        try {
                            timer?.cancel()
                            timer = null
                        } finally {
                            readWriteLock.writeLock().unlock()
                        }
                    }
                }
            }
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

}
