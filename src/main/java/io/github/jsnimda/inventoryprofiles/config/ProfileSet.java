package io.github.jsnimda.inventoryprofiles.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.jsnimda.inventoryprofiles.sorter.util.Current;

/**
 * ProfileSet
 */
public class ProfileSet {

  // refer: ItemSlotArgumentType
  public static final HashMap<String, Integer> SLOT_MAP = new HashMap<>();

  static {
    SLOT_MAP.put("armor.head", 5);
    SLOT_MAP.put("armor.chest", 6);
    SLOT_MAP.put("armor.legs", 7);
    SLOT_MAP.put("armor.feet", 8);
    for(int i = 0; i < 27; i++) {
      SLOT_MAP.put("inventory." + i, 9 + i);
    }
    for(int i = 0; i < 9; i++) {
      SLOT_MAP.put("hotbar." + i, 36 + i);
    }
    SLOT_MAP.put("weapon.offhand", 45);
  }

  /**
   * @param slotName
   * @return slotId for playerContainer
   */
  public static int getSlotId(String slotName) {
    if ("weapon.mainhand".equals(slotName)) {
      return 36 + Current.selectedSlot();
    }
    return SLOT_MAP.get(slotName);
  }
  public static boolean isValidSlotName(String slotName) {
    return "weapon.mainhand".equals(slotName) || SLOT_MAP.containsKey(slotName);
  }

  // class instance
  public final String profileName;
  public final Map<String, String> options = new HashMap<>();
  public final List<Profile> profiles = new ArrayList<>();

  public void addOption(String key, String value) {
    options.put(key, value);
  }
  public Profile addProfile() {
    Profile p = new Profile();
    profiles.add(p);
    return p;
  }
  public ProfileSet(String profileName) {
    this.profileName = profileName;
  }

  public static class Profile {
    public final List<ProfilePreferenceEntry> preferences = new ArrayList<>();
    public void addPreference(String slotName, String items) {
      preferences.add(new ProfilePreferenceEntry(slotName, items));
    }
  }

  public static class ProfilePreferenceEntry {
    public final String slotName;
    public final String items;

    public ProfilePreferenceEntry(String slotName, String items) {
      this.slotName = slotName;
      this.items = items;
    }
    
  }

}