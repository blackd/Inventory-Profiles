/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.phys.Vec3;
import org.anti_ad.mc.ipnext.event.villagers.VillagerTradeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerboundInteractPacket.class)
public class MixinServerBoundInteractPacket {

    @Shadow @Final private int entityId;

    @Inject(method = "createInteractionPacket(Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;",
            at = @At("HEAD"))
    private static void createInteractionPacket(Entity entity,
                                                boolean p_179614_,
                                                InteractionHand p_179615_,
                                                Vec3 p_179616_,
                                                CallbackInfoReturnable<ServerboundInteractPacket> cir) {

        if (entity instanceof AbstractVillager) {
            VillagerTradeManager.INSTANCE.setCurrentVillager((AbstractVillager) entity);
        }
    }
}
