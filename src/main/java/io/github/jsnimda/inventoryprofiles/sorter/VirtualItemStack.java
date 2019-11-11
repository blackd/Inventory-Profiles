package io.github.jsnimda.inventoryprofiles.sorter;

public final class VirtualItemStack {
  public VirtualItemType itemType;
  public int count;

  public VirtualItemStack(VirtualItemType itemType, int count) {
    this.itemType = itemType;
    this.count = count;
  }

  public VirtualItemType getItemType() {
    return itemType;
  }
  public void setItemType() {

  }

  public int getMaxCount() {
    return itemType.getMaxCount();
  }

  public boolean sameType(VirtualItemStack b) { //TODO remove null
    return b != null && itemType.equals(b.itemType);
  }
  public boolean capable(VirtualItemStack b) { // return if two can join together
    return b == null || sameType(b) || isEmpty() || b.isEmpty(); //TODO remove null
  }

  public int add(int anotherCount) { // return leftover
    int j = cap(count + anotherCount) - count;
    count += j;
    return anotherCount - j;
  }
  public int tryAdd(int anotherCount) { // return count after add
    return cap(count + anotherCount);
  }

  public int cap(int count) {
    return Math.min(count, getMaxCount());
  }

  public VirtualItemStack cap() {
    count = cap(count);
    return this;
  }

  public boolean isFull() {
    return count >= getMaxCount();
  }

  public VirtualItemStack copy() {
    return new VirtualItemStack(itemType, count);
  }
  public VirtualItemStack copy(int count) {
    return new VirtualItemStack(itemType, count);
  }
  public VirtualItemStack copy(VirtualItemType itemtype) {
    return new VirtualItemStack(itemtype, count);
  }

  public void updateEmpty() {
    if (itemType.isEmpty() || count <= 0) {
      setEmpty();
    }
  }
  public boolean isEmpty() { // also update to air if empty
    if (itemType.isEmpty() || count <= 0) {
      setEmpty();
      return true;
    }
    return false;
  }
  public void setEmpty() {
    this.itemType = VirtualItemType.EMPTY;
    this.count = 0;
  }

  // ============
  // operations
  public void swap(VirtualItemStack another) {
    VirtualItemType ctype = itemType;
    itemType = another.itemType;
    another.itemType = ctype;
    int tmpcount = count;
    count = another.count;
    another.count = tmpcount;
  }

  public boolean preTransfer(VirtualItemStack another) {
    if (!this.capable(another)) return false;
    if (isEmpty()) return false;
    if (another.isFull()) return false;
    if (another.isEmpty()) {
      another.itemType = itemType;
      another.count = 0;
    }
    return true;
  }
  public boolean transferTo(VirtualItemStack another) { // return if any changes
    if (!preTransfer(another)) return false;
    count = another.add(count);
    updateEmpty(); // update if this empty
    return true;
  }
  public boolean transferOneTo(VirtualItemStack another) { // return if any changes
    if (!preTransfer(another)) return false;
    count--;
    another.count++;
    updateEmpty(); // update if this empty
    return true;
  }
  public int splitHalf() {
    int del = count - count / 2;
    count -= del;
    return del;
  }
  public boolean splitHalfTo(VirtualItemStack another) { // return if any changes
    if (!this.capable(another)) return false;
    if (isEmpty()) return false;
    if (!another.isEmpty()) return false;
    another.itemType = itemType;
    another.count = splitHalf();
    updateEmpty(); // update if this empty (split half when count is 1)
    return true;
  }

  // ============
  // statics
  public static VirtualItemStack empty() {
    return new VirtualItemStack(VirtualItemType.EMPTY, 0);
  }

  // ============
  // overrides

  @Override
  public String toString() {
    return this.count + "x " + this.itemType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + count;
    result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
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
    if (itemType == null) {
      if (other.itemType != null)
        return false;
    } else if (!itemType.equals(other.itemType))
      return false;
    return true;
  }


}