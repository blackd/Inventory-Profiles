package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemType;
import net.minecraft.entity.effect.StatusEffectInstance;
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

  public static int getDamage(VirtualItemType type) {
    return getItemStack(type).getDamage();
  }

  public static boolean hasPotionName(VirtualItemType type) {
    return type.tag != null && type.tag.containsKey("Potion", 8);
  }
  public static String getPotionRegularName(VirtualItemType type) {
    return PotionUtil.getPotion(type.tag).getName("");
  }

  public static int compareEffects(List<StatusEffectInstance> aList, List<StatusEffectInstance> bList) {
    for (int i = 0; i < Math.max(aList.size(), bList.size()); i++) {
      int aHas = i < aList.size() ? 1 : 0;
      int bHas = i < bList.size() ? 1 : 0;
      if (aHas == 1 && bHas == 1) {
        StatusEffectInstance aEff = aList.get(i);
        StatusEffectInstance bEff = bList.get(i);
        int cmp = compareEffect(aEff, bEff);
        if (cmp != 0) return cmp;
      } else {
        return bHas - aHas; // has first
      }
    }
    return 0; // identicial
  }
  public static int compareEffect(StatusEffectInstance a, StatusEffectInstance b) {
    String aStr = Registry.STATUS_EFFECT.getId(a.getEffectType()).toString();
    String bStr = Registry.STATUS_EFFECT.getId(a.getEffectType()).toString();
    int cmp;
    cmp = aStr.compareTo(bStr);
    if (cmp != 0) return cmp;
    cmp = b.getAmplifier() - a.getAmplifier(); // stronger first
    if (cmp != 0) return cmp;
    cmp = b.getDuration() - a.getDuration(); // longer first
    return cmp;
  }

  // public static boolean hasCustomPotionEffects(VirtualItemType type) {
  //   return type.tag != null && type.tag.containsKey("CustomPotionEffects", 9);
  // }

  // public static CompoundTag getSubTag(CompoundTag tag, String key) {
  //   return tag != null && tag.containsKey(key, 10) ? tag.getCompound(key) : null;
  // }


}