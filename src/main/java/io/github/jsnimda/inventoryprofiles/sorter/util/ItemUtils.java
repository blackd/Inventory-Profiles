package io.github.jsnimda.inventoryprofiles.sorter.util;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * ItemUtils
 */
public class ItemUtils {

  public static Identifier getItemId(Item item) {
    return Registry.ITEM.getId(item);
  }
  public static String getItemIdString(Item item) {
    return getItemId(item).toString();
  }

  public static ItemStack getItemStack(VirtualItemType type) {
    return getItemStack(type.item, type.tag);
  }
  public static ItemStack getItemStack(Item item, CompoundTag tag) {
    ItemStack is = new ItemStack(item);
    is.setTag(tag);
    return is;
  }

  public static String getTranslatedName(VirtualItemType type) {
    return getTranslatedName(getItemStack(type));
  }
  public static String getTranslatedName(ItemStack item) {
    return item.getItem().getName(item).getString();
  }

  public static boolean hasPotionName(VirtualItemType type) {
    return type.tag != null && type.tag.containsKey("Potion", 8);
  }
  public static String getPotionRegularName(VirtualItemType type) {
    return PotionUtil.getPotion(getItemStack(type)).getName("");
  }

  public static boolean hasCustomPotionEffects(VirtualItemType type) {
    return type.tag != null && type.tag.containsKey("CustomPotionEffects", 9);
  }

  public static int getDamage(VirtualItemType type) {
    return getItemStack(type).getDamage();
  }

  public static CompoundTag getSubTag(CompoundTag tag, String key) {
    return tag != null && tag.containsKey(key, 10) ? tag.getCompound(key) : null;
  }

}