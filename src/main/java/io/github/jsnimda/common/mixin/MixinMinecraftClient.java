package io.github.jsnimda.common.mixin;

import io.github.jsnimda.common.event.GlobalInitHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

//  @Inject(method = "init()V", at = @At("RETURN"))
  @Inject(method = "run()V", at = @At("HEAD"))
  private void init(CallbackInfo ci) {
    GlobalInitHandler.INSTANCE.onInit();
  }

}
