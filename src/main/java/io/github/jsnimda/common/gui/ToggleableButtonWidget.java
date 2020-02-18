package io.github.jsnimda.common.gui;

import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

public class ToggleableButtonWidget extends AbstractButtonWidget {

  protected final BiConsumer<ToggleableButtonWidget, Integer> pressAction;

  public ToggleableButtonWidget(int i, int j, int k, int l, String string, BiConsumer<ToggleableButtonWidget, Integer> pressAction) {
    super(i, j, k, l, string);
    this.pressAction = pressAction;
  }

  @Override
  public boolean mouseClicked(double d, double e, int i) { // ref: AbstractButtonWidget
    if (this.active && this.visible) {
      boolean bl = this.clicked(d, e);
      if (bl) {
        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
        this.pressAction.accept(this, i);
        return true;
      }

      return false;
   } else {
      return false;
   }
  }

  @Override
  public boolean keyPressed(int i, int j, int k) { // ref: AbstractPressableButtonWidget
     if (this.active && this.visible) {
        if (i != 257 && i != 32 && i != 335) {
           return false;
        } else {
           this.playDownSound(MinecraftClient.getInstance().getSoundManager());
           this.pressAction.accept(this, GLFW.GLFW_MOUSE_BUTTON_LEFT);
           return true;
        }
     } else {
        return false;
     }
  }
  
}