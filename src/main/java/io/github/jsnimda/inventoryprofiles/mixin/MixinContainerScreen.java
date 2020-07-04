package io.github.jsnimda.inventoryprofiles.mixin;

import io.github.jsnimda.inventoryprofiles.gui.inject.ContainerScreenHandler;
import io.github.jsnimda.inventoryprofiles.gui.inject.InjectWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(HandledScreen.class)
public abstract class MixinContainerScreen<T extends ScreenHandler> extends Screen {

  protected MixinContainerScreen(Text text) {
    super(text);
  }

  @Inject(at = @At("RETURN"), method = "init()V")
  protected void init(CallbackInfo info) {
    List<InjectWidget> list = ContainerScreenHandler.INSTANCE.getContainerInjector((HandledScreen) (Object) this);
    for (InjectWidget iw : list) {
      addButton(iw);
    }
  }

//  @Inject(at = @At("RETURN"), method = "render(IIF)V")
//  public void render(int int_1, int int_2, float float_1, CallbackInfo info) {
//    Tooltips.INSTANCE.renderAll();
//  }

}