package io.github.jsnimda.inventoryprofiles.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

/**
 * MixinClientPlayerEntity
 */
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

  @Final @Shadow
  protected MinecraftClient client;

  public MixinClientPlayerEntity(ClientWorld clientWorld_1, GameProfile gameProfile_1) {
    super(clientWorld_1, gameProfile_1);
    // Auto-generated constructor stub
    // client = null; // ...what?
  }

  @Inject(at = @At("HEAD"), method = "closeContainer()V")
  public void closeContainer(CallbackInfo info) {
    // if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()) {
    //   SorterEventPort.handleCloseContainer(this.client);
    // }
    // TODO remove this class, code moved to MixinGuiCloseC2SPacket
  }

}