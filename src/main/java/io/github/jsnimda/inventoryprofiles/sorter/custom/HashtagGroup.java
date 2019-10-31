package io.github.jsnimda.inventoryprofiles.sorter.custom;

import java.util.HashMap;
import java.util.Map;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

/**
 * HashtagGroup
 */
public class HashtagGroup {

  public static final Map<String, HashtagGroup> groups = new HashMap<>();

  private static void setup() {

  }

  public static boolean isIn(VirtualItemType type, String groupName) {
    // TODO is in user defined
    
    TagContainer<Item> container = ItemTags.getContainer();
    Tag<Item> tt = container.get(new Identifier("acacia_logs")); // TODO get tag without entering world (only in data/minecraft/tags/items?)
    
    return false;
  }

}