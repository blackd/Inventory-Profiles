package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import io.github.jsnimda.inventoryprofiles.sorter.util.ItemUtils;

/**
 * ItemTypeComparators
 */
public class ItemTypeComparators {

  public enum StringCompareMethod {
    IGNORE_CASE,
    LOCALE,
    UNICODE
  }
  public static int compareString(String a, String b, StringCompareMethod m) {
    if (m == StringCompareMethod.IGNORE_CASE)
      return a.compareToIgnoreCase(b);
    if (m == StringCompareMethod.UNICODE)
      return a.compareTo(b);
    if (m == StringCompareMethod.LOCALE) {
      String langTag = Current.languageCode().replace('_','-');
      Collator c = Collator.getInstance(Locale.forLanguageTag(langTag));
      return c.compare(a, b);
    }
    throw new AssertionError("Unreachable");
  }

  public static int custom_name(VirtualItemType a, VirtualItemType b, StringCompareMethod m) {
    int aHas = ItemUtils.getItemStack(a).hasCustomName() ? 1 : 0;
    int bHas = ItemUtils.getItemStack(b).hasCustomName() ? 1 : 0;
    if (aHas == 1 && bHas == 1) {
      String aStr = ItemUtils.getItemStack(a).getName().getString();
      String bStr = ItemUtils.getItemStack(b).getName().getString();
      return compareString(aStr, bStr, m);
    } else {
      return bHas - aHas;
    }
  }
  public static int translated_name(VirtualItemType a, VirtualItemType b, StringCompareMethod m) {
    String aStr = ItemUtils.getTranslatedName(a);
    String bStr = ItemUtils.getTranslatedName(b);
    return compareString(aStr, bStr, m);
  }

  public static class BuiltIn {
    public static int item_id(VirtualItemType a, VirtualItemType b) {
      String itemIdA = ItemUtils.getItemIdString(a.item);
      String itemIdB = ItemUtils.getItemIdString(b.item);
      return itemIdA.compareTo(itemIdB);
    }
    public static int has_custom_name(VirtualItemType a, VirtualItemType b) { // has first
      int aHas = ItemUtils.getItemStack(a).hasCustomName() ? 1 : 0;
      int bHas = ItemUtils.getItemStack(b).hasCustomName() ? 1 : 0;
      return bHas - aHas;
    }
    public static int custom_name_ignore_case(VirtualItemType a, VirtualItemType b) {
      return custom_name(a, b, StringCompareMethod.IGNORE_CASE);
    }
    public static int custom_name_locale(VirtualItemType a, VirtualItemType b) {
      return custom_name(a, b, StringCompareMethod.LOCALE);
    }
    public static int custom_name_unicode(VirtualItemType a, VirtualItemType b) {
      return custom_name(a, b, StringCompareMethod.UNICODE);
    }
    public static int translation_key(VirtualItemType a, VirtualItemType b) {
      String aKey = ItemUtils.getItemStack(a).getTranslationKey();
      String bKey = ItemUtils.getItemStack(b).getTranslationKey();
      return aKey.compareTo(bKey);
    }
    public static int translated_name_ignore_case(VirtualItemType a, VirtualItemType b) {
      return translated_name(a, b, StringCompareMethod.IGNORE_CASE);
    }
    public static int translated_name_locale(VirtualItemType a, VirtualItemType b) {
      return translated_name(a, b, StringCompareMethod.LOCALE);
    }
    public static int translated_name_unicode(VirtualItemType a, VirtualItemType b) {
      return translated_name(a, b, StringCompareMethod.UNICODE);
    }
    public static int creative_menu(VirtualItemType a, VirtualItemType b) {
      // TODO done this
      return 0;
    }
    public static int enchantments(VirtualItemType a, VirtualItemType b) {
      // TODO done this
      return 0;
    }
    public static int damage(VirtualItemType a, VirtualItemType b) {
      int aDmg = ItemUtils.getDamage(a);
      int bDmg = ItemUtils.getDamage(b);
      return aDmg - bDmg;
    }
    public static int potion_name(VirtualItemType a, VirtualItemType b) {
      int aHas = ItemUtils.hasPotionName(a) ? 1 : 0;
      int bHas = ItemUtils.hasPotionName(b) ? 1 : 0;
      if (aHas == 1 && bHas == 1) {
        // TODO done this
      } else {
        return bHas - aHas;
      }
      return 0;
    }
    public static int nbt(VirtualItemType a, VirtualItemType b) {
      // TODO done this
      return 0;
    }




    public static Map<String, Comparator<VirtualItemType>> methods = new HashMap<>();

  }
  
}