package io.github.jsnimda.inventoryprofiles.config;

import java.util.ArrayList;
import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;

/**
 * ProfilesConfig
 */
public class ProfilesConfig {

  public enum ProfileId {
    PROFILE_I,
    PROFILE_II
  }
  public static ProfileId selectedProfile = ProfileId.PROFILE_II;

  public static final ProfilesConfig emptyProfiles;
  public static ProfilesConfig defaultProfiles;

  public static ProfilesConfig getDefaultProfiles() {
    return defaultProfiles;
  }

  public static ProfilesConfig parse(String str) {
    return emptyProfiles;
  }

  static {
    emptyProfiles = new ProfilesConfig();
  }

  public List<PreferenceEntry> profileI = new ArrayList<>();
  public List<PreferenceEntry> profileII = new ArrayList<>();

  public String errMsgs = "";

  public static class PreferenceEntry {
    public int slotId;
    public List<VirtualItemStack> items = new ArrayList<>();
    public PreferenceEntry(int slotId) {
      this.slotId = slotId;
    }
  }
}