package io.github.jsnimda.inventoryprofiles.sorter.custom;

import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerInfo;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import io.github.jsnimda.inventoryprofiles.sorter.util.CurrentState;
import net.minecraft.container.PlayerContainer;

/**
 * ProfileProcessor
 */
public class ProfileProcessor {

  public static void setProfile(Profile profile) {
    if (!(Current.inGame() && Current.container() instanceof PlayerContainer)) return;
    new ProfileProcessor().process(profile);
  }

  private ContainerInfo info;
  private List<SlotGroup> slotGroups;
  public void process(Profile profile) {
    info = CurrentState.containerInfo();
    slotGroups = profile.getSlotGroups();
    for (SlotGroup slotGroup : slotGroups) {
      process(slotGroup);
    }
    last();
  }
  private void process(SlotGroup slotgroup) {

  }
  private void last() {
    
  }

}