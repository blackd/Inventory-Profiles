package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.screen.BaseScreen
import io.github.jsnimda.common.gui.widget.AnchorStyles

class RootWidget(val screen: BaseScreen) : Widget() {
  override val allowParent: Boolean
    get() = false

  init {
    anchor = AnchorStyles.all
    size = Size(containerWidth, containerHeight)
  }
}