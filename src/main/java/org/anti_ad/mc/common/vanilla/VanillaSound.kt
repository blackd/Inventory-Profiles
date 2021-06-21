package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.vanilla.alias.PositionedSoundInstance
import org.anti_ad.mc.common.vanilla.alias.SoundEvents

object VanillaSound {

    fun playClick() {
        Vanilla.soundManager().play(PositionedSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,
                                                                  1.0f))
    }

}