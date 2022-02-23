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

    private val soundEvent =  SoundEvent(Identifier(MOD_ID, loc))

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