package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.vanilla.alias.PositionedSoundInstance
import org.anti_ad.mc.common.vanilla.alias.SoundEvents
import org.anti_ad.mc.common.vanilla.alias.SoundInstance
import org.anti_ad.mc.common.vanilla.glue.IVanillaSound
import org.anti_ad.mc.common.vanilla.glue.__glue_vanillaSound

fun initVanillaSound() {
    __glue_vanillaSound = VanillaSound
}

object VanillaSound: IVanillaSound {

    override fun playClick() {
        Vanilla.soundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK,
                                                                   1.0f))
    }

    fun play(sound: SoundInstance) = Vanilla.soundManager().play(sound)

    fun play(sound: SoundInstance, delay: Int) = Vanilla.soundManager().playDelayed(sound, delay)

}