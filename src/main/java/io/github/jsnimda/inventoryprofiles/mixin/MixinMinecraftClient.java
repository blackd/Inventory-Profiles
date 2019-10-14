package io.github.jsnimda.inventoryprofiles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import net.minecraft.client.MinecraftClient;

/**
 * MixinMinecraftClient
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

  @Shadow
  private int itemUseCooldown;

  @Inject(at = @At("HEAD"), method = "tick()V")
  public void tick(CallbackInfo info) {
    if (this.itemUseCooldown > 0 && Tweaks.DISABLE_ITEM_USE_COOLDOWN.getBooleanValue()) {
      this.itemUseCooldown = 0;
    }
  }
}
