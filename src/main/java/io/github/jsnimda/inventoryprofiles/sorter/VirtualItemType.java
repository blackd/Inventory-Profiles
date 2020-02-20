package io.github.jsnimda.inventoryprofiles.sorter;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;

public final class VirtualItemType {
  public static final VirtualItemType EMPTY = new VirtualItemType(Items.AIR, null);

  public final Item item;
  @Nullable
  public final CompoundNBT tag;
  private ItemStack dummyItemStack = null;

  public VirtualItemType(Item item, CompoundNBT tag) {
    this.item = item;
    this.tag = tag;
  }

  public int getMaxCount() {
    return getDummyItemStack().getMaxStackSize(); // forge compatible instead of item.getMaxCount()
  }

  public ItemStack getDummyItemStack() {
    if (dummyItemStack == null) {
      dummyItemStack = new ItemStack(item);
      dummyItemStack.setTag(tag);
    }
    return dummyItemStack;
  }

  public boolean isEmpty() {
    return this == EMPTY || this.item == Items.AIR;
  }

  // ============
  // statics

  public static boolean areItemTypesEqual(Item item1, CompoundNBT tag1, Item item2, CompoundNBT tag2) {
    if (item1 != item2) return false;
    if (tag1 == null && tag2 != null) {
       return false;
    } else {
       return tag1 == null || tag1.equals(tag2);
    }
  }

  // ============
  // overrides

  @Override
  public String toString() {
    return this.item + "" + (this.tag == null ? "" : this.tag);
  }

  @Override
  public int hashCode() {
    if (isEmpty() && this != EMPTY) return EMPTY.hashCode();
    final int prime = 31;
    int result = 1;
    result = prime * result + ((item == null) ? 0 : item.hashCode());
    result = prime * result + ((tag == null) ? 0 : tag.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VirtualItemType other = (VirtualItemType) obj;
    if (isEmpty() && other.isEmpty())
      return true;
    if (item == null) {
      if (other.item != null)
        return false;
    } else if (!item.equals(other.item))
      return false;
    if (tag == null) {
      if (other.tag != null)
        return false;
    } else if (!tag.equals(other.tag))
      return false;
    return true;
  }

}