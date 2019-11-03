package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * SortingMethodOption
 */
public enum SortingMethodOption implements IConfigOptionListEntry {

  DEFAULT         ("default",         "inventoryprofiles.label.sorting_method.default"),
  ITEM_NAME       ("item_name",       "inventoryprofiles.label.sorting_method.item_name"),
  ITEM_ID         ("item_id",         "inventoryprofiles.label.sorting_method.item_id"),
  TRANSLATION_KEY ("translation_key", "inventoryprofiles.label.sorting_method.translation_key"),
  ;

  private final String configString;
  private final String unlocName;

  private SortingMethodOption(String configString, String unlocName)
  {
      this.configString = configString;
      this.unlocName = unlocName;
  }

  @Override
  public String getStringValue()
  {
      return this.configString;
  }

  @Override
  public String getDisplayName()
  {
      return StringUtils.translate(this.unlocName);
  }

  @Override
  public IConfigOptionListEntry cycle(boolean forward)
  {
      int id = this.ordinal();

      if (forward)
      {
          if (++id >= values().length)
          {
              id = 0;
          }
      }
      else
      {
          if (--id < 0)
          {
              id = values().length - 1;
          }
      }

      return values()[id % values().length];
  }

  @Override
  public SortingMethodOption fromString(String name)
  {
      return fromStringStatic(name);
  }

  public static SortingMethodOption fromStringStatic(String name)
  {
      for (SortingMethodOption mode : SortingMethodOption.values())
      {
          if (mode.configString.equalsIgnoreCase(name))
          {
              return mode;
          }
      }

      return SortingMethodOption.DEFAULT;
  }
}