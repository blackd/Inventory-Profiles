package io.github.jsnimda.inventoryprofiles.sorter.util;

import net.minecraft.container.Container;

/**
 * CurrentState
 * <p>
 * Getting current non-vanilla objects.
 */
public class CurrentState {
  private static ContainerInfoCache containerInfoCache = new ContainerInfoCache();
  private static ContainerInfoCache playerContainerInfoCache = new ContainerInfoCache();

  private static class ContainerInfoCache {
    public Container cachedContainer = null;
    public ContainerInfo cachedContainerInfo = null;

    public ContainerInfo get(Container container) {
      if (cachedContainer != container || cachedContainerInfo == null) {
        cachedContainerInfo = ContainerInfo.of(cachedContainer = container);
      }
      return cachedContainerInfo;
    }
  }

  public static ContainerInfo containerInfo() {
    return containerInfoCache.get(Current.container());
  }

  public static ContainerInfo playerContainerInfo() {
    return playerContainerInfoCache.get(Current.playerContainer());
  }

}