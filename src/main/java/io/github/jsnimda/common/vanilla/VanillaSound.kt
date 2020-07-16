package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.vanilla.alias.PositionedSoundInstance
import io.github.jsnimda.common.vanilla.alias.SoundEvents

object VanillaSound {

  fun playClick() {
    Vanilla.soundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
  }

}