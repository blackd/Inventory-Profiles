package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.screen.BaseScreen

class RootWidget(val screen: BaseScreen) : Widget() {

  init {
    anchor = AnchorStyles(true, true, true, true)
    size = Size(containerWidth, containerHeight)
  }

}