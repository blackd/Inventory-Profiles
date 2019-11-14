package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.jsnimda.inventoryprofiles.sorter.util.Converter;

class VirtualItemStacksSandbox {

  public static final int INF_LOOP_MAX = 1000;

  public List<Click> clicks = new ArrayList<>();
  public List<VirtualItemStack> items;
  public VirtualItemStack cursor = VirtualItemStack.empty();
  private StateSaver stateSaver = new StateSaver();
  public VirtualItemStacksSandbox(List<VirtualItemStack> items) { // do copy
    this.items = Converter.copy(items);
  }

  private class StateSaver {
    private Stack<Integer> clickSizes = new Stack<>();
    private Stack<List<VirtualItemStack>> itemss = new Stack<>();
    private Stack<VirtualItemStack> cursors = new Stack<>();
    public void save() {
      clickSizes.push(clicks.size());
      itemss.push(Converter.copy(items));
      cursors.push(cursor.copy());
    }
    public void restore() {
      int clickSize = clickSizes.pop();
      while (clicks.size() > clickSize) {
        clicks.remove(clicks.size() - 1);
      }
      items = itemss.pop();
      cursor = cursors.pop();
    }
    public void unsave() {
      clickSizes.pop();
      itemss.pop();
      cursors.pop();
    }
  }

  public void save() {
    stateSaver.save();
  }

  public void restore() {
    stateSaver.restore();
  }

  public void unsave() {
    stateSaver.unsave();
  }

  public void addClickLimited(Click c) {
    if (clicks.size() >= INF_LOOP_MAX) 
      throw new RuntimeException("Infinite loop detected. Click count > " + INF_LOOP_MAX);
    clicks.add(c);
  }
  public VirtualItemStack itemAt(int index) {
    return items.get(index);
  }
  public void leftClick(int index) {
    addClickLimited(Click.leftClick(index)); // needs remap
    if (cursor.isEmpty() || !cursor.capable(itemAt(index))) {
      cursor.swap(itemAt(index));
    } else { // same type, cursor fill -> slot
      cursor.transferTo(itemAt(index));
    }
  }
  public void rightClick(int index) {
    addClickLimited(Click.rightClick(index));
    if (cursor.isEmpty()) { // split half
      itemAt(index).splitHalfTo(cursor);
    } else if (!cursor.capable(itemAt(index))) { // swap
      cursor.swap(itemAt(index));
    } else { // cursor transfer one -> slot
      cursor.transferOneTo(itemAt(index));
    }
  }
  public void rightClick(int index, int times) { // right click n times
    for (int i = 0; i < times; i++) {
      rightClick(index);
    }
  }
  public void dropOne(int index) {
    addClickLimited(Click.dropOne(index));
    if (!itemAt(index).isEmpty()) {
      itemAt(index).count--;
      itemAt(index).updateEmpty(); // update is empty
    }
  }
  public void dropAll(int index) {
    addClickLimited(Click.dropAll(index));
    itemAt(index).setEmpty();
  }
  public void dropOneCursor() {
    addClickLimited(Click.dropOneCursor());
    if (!cursor.isEmpty()) {
      cursor.count--;
      cursor.updateEmpty(); // update is empty
    }
  }
  public void dropAllCursor() {
    addClickLimited(Click.dropAllCursor());
    cursor.setEmpty();
  }
}