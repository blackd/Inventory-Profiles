package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * ItemUtils
 */
public class ItemUtils {

  @SuppressWarnings("deprecation")
  public static ResourceLocation getItemId(Item item) {
    return Registry.ITEM.getKey(item);
  }
  public static String getItemIdString(Item item) {
    return getItemId(item).toString();
  }

  public static ItemStack getItemStack(VirtualItemType type) {
    return getItemStack(type.item, type.tag);
  }
  public static ItemStack getItemStack(Item item, CompoundNBT tag) {
    ItemStack is = new ItemStack(item);
    is.setTag(tag);
    return is;
  }

  public static String getTranslatedName(VirtualItemType type) {
    return getTranslatedName(getItemStack(type));
  }
  public static String getTranslatedName(ItemStack item) {
    return item.getItem().getDisplayName(item).getString();
  }

  public static int getDamage(VirtualItemType type) {
    return getItemStack(type).getDamage();
  }

  public static boolean hasPotionName(VirtualItemType type) {
    return type.tag != null && type.tag.contains("Potion", 8);
  }
  public static String getPotionRegularName(VirtualItemType type) {
    return PotionUtils.getPotionTypeFromNBT(type.tag).getNamePrefixed("");
  }

  public static int compareEffects(List<EffectInstance> aList, List<EffectInstance> bList) {
    for (int i = 0; i < Math.max(aList.size(), bList.size()); i++) {
      int aHas = i < aList.size() ? 1 : 0;
      int bHas = i < bList.size() ? 1 : 0;
      if (aHas == 1 && bHas == 1) {
        EffectInstance aEff = aList.get(i);
        EffectInstance bEff = bList.get(i);
        int cmp = compareEffect(aEff, bEff);
        if (cmp != 0) return cmp;
      } else {
        return bHas - aHas; // has first
      }
    }
    return 0; // identicial
  }
  
  @SuppressWarnings("deprecation")
  public static int compareEffect(EffectInstance a, EffectInstance b) {
    String aStr = Registry.EFFECTS.getKey(a.getPotion()).toString();
    String bStr = Registry.EFFECTS.getKey(a.getPotion()).toString();
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