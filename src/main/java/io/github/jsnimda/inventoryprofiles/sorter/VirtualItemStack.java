package io.github.jsnimda.inventoryprofiles.sorter;

public final class VirtualItemStack {
  public VirtualItemType itemtype;
  public int count;

  public VirtualItemStack(VirtualItemType itemtype, int count) {
    this.itemtype = itemtype;
    this.count = count;
  }

  public int getMaxCount() {
    return itemtype.getMaxCount();
  }

  public boolean sameType(VirtualItemStack b) {
    return b != null && itemtype.equals(b.itemtype);
  }

  public VirtualItemStack copy() {
    return new VirtualItemStack(itemtype, count);
  }
  public VirtualItemStack copyWithCount(int count) {
    return new VirtualItemStack(itemtype, count);
  }

  @Override
  public String toString() {
    return this.count + "x " + this.itemtype;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + count;
    result = prime * result + ((itemtype == null) ? 0 : itemtype.hashCode());
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
    VirtualItemStack other = (VirtualItemStack) obj;
    if (count != other.count)
      return false;
    if (itemtype == null) {
      if (other.itemtype != null)
        return false;
    } else if (!itemtype.equals(other.itemtype))
      return false;
    return true;
  }


}