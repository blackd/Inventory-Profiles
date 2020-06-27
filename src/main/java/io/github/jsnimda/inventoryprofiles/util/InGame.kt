package io.github.jsnimda.inventoryprofiles.util

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.`(itemStack)`
import net.minecraft.entity.EquipmentSlot

fun vSelectedSlot() =
  Vanilla.playerInventory().`(selectedSlot)`

//fun vMainHandItem(): ItemStack =
//  // clientPlayerEntity.getMainHandStack()
//  Vanilla.player().mainHandStack.`(itemStack)`
//
//fun vOffHandItem(): ItemStack =
//  Vanilla.player().offHandStack.`(itemStack)`
//
//fun vHeadItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.HEAD).`(itemStack)`
//
//fun vChestItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.CHEST).`(itemStack)`
//
//fun vLegsItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.LEGS).`(itemStack)`
//
//fun vFeetItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.FEET).`(itemStack)`

