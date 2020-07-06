package io.github.jsnimda.common.vanilla

import net.minecraft.client.audio.SimpleSound
import net.minecraft.util.SoundEvents

object VanillaSound {

  fun playClick() {
    Vanilla.soundManager().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
  } // forge SimpleSound = PositionedSoundInstance

}