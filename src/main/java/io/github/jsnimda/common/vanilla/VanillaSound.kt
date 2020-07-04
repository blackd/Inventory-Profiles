package io.github.jsnimda.common.vanilla

import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents

object VanillaSound {

  fun playClick() {
    Vanilla.soundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
  }

}