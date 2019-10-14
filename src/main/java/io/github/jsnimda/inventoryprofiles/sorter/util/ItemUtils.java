package io.github.jsnimda.inventoryprofiles.sorter.util;

import net.minecraft.item.Item;
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

}