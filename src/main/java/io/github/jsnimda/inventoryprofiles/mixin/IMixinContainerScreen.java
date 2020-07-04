package io.github.jsnimda.inventoryprofiles.mixin;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerScreen.class)
public interface IMixinContainerScreen {

  @Accessor("focusedSlot")
  Slot getFocusedSlot();

  @Accessor("x")
  int getContainerX();

  @Accessor("y")
  int getContainerY();

  @Accessor("containerWidth")
  int getContainerWidth();

  @Accessor("containerHeight")
  int getContainerHeight();

}
