package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen

object ContainerScreenHandler {
  fun getContainerInjector(screen: ContainerScreen<*>): InjectWidget {
    return InjectWidget().apply {
      addWidget(SortingButtonContainer(screen))
    }
  }
}