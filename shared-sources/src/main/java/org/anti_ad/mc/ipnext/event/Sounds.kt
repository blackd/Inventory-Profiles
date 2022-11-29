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

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaSound
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.vanilla.*
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.ModInfo.MOD_ID

enum class Sounds(private val loc: String, pitch: Float = 1.0F) {

    REFILL_STEP_NOTIFY("tool_refill_step_ping");
    //private val registryObject: Any?

    private val defaultPitch: Float

    init {
        try {
            this.defaultPitch = pitch
            //registryObject = VanillaSound.registerSound(loc)

        } catch (t: Throwable) {
            t.printStackTrace()
            throw ExceptionInInitializerError(t)
        }
    }

    private fun register() {

    }

    private val soundEvent = VanillaSound.createSoundEvent(Identifier(MOD_ID, loc))

    fun play()  {
        VanillaSound.play(PositionedSoundInstance.master(soundEvent, defaultPitch, .75F))
    }

    fun play(pitch: Float) = VanillaSound.play(PositionedSoundInstance.master(soundEvent, pitch, .75f))

    fun play(pitch: Float, delay: Int) = VanillaSound.play(PositionedSoundInstance.master(soundEvent, pitch, .75f), delay)


    companion object {
        fun registerAll() {
            REFILL_STEP_NOTIFY.register()
        }
    }
}
