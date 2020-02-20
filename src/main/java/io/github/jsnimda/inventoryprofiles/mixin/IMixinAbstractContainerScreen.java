package io.github.jsnimda.inventoryprofiles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;

@Mixin(ContainerScreen.class)
public interface IMixinAbstractContainerScreen {

  @Accessor("focusedSlot")
  Slot getFocusedSlot();

}
