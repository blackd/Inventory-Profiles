package io.github.jsnimda.inventoryprofiles.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.gui.inject.SortButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.container.Container;
import net.minecraft.text.Text;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen<T extends Container> extends Screen {

  @Shadow
  protected int containerWidth;
  @Shadow
  protected int containerHeight;
  @Shadow
  protected int left;
  @Shadow
  protected int top;

  protected MixinAbstractContainerScreen(Text text_1) {
    super(text_1);
    // Auto-generated constructor stub
  }

  @Inject(at = @At("TAIL"), method = "init()V")
  protected void init(CallbackInfo info) {
    List<AbstractButtonWidget> buttons = SortButtonWidget.getButtons(this, left, top, containerWidth, containerHeight);
    buttons.forEach(x -> this.addButton(x));
  }
}