package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.jsnimda.inventoryprofiles.Log;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

/**
 * VirtualSorter
 *    - this class don't deal with itemStack etc.
 * Procedure: List<VIS> with nullable elements ([input])
 *    -> collapse -> sort -> uncollapse -> groups -> diff -> [output]
 * Disclaimer: Currently item count that larger than a full stack is not supported,
 *    those items will be treated as a normal full stack of item (crash prevention(?))
 */
public class VirtualSorter {
  private VirtualSorter(){}

  public static List<Click> doSort(List<VirtualItemStack> items,
      ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider, boolean targetsFirst) {
    try {
      List<VirtualItemStack> uni = uniquify(items);
      List<VirtualItemStack> res = groups(uncollapse(sort(collapse(uni), sortingProvider)), groupingProvider, uni.size());
      return targetsFirst ? diffTargetsFirst(uni, res) : diff(uni, res);
    } catch (RuntimeException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  /**
   * Let all the same item tpyes to the same object reference. Run in O(n^2)
   * @param items
   * @return
   */
  public static List<VirtualItemStack> uniquify(List<VirtualItemStack> items) {
    List<VirtualItemStack> uni = new ArrayList<>();
    for (VirtualItemStack e : items) {
      if (e == null) {
        uni.add(null);
        continue;
      }
      VirtualItemStack n = e.copy();
      uni.add(n);
      for (VirtualItemStack v : uni) {
        if (n.sameType(v)) {
          n.itemtype = v.itemtype;
          break;
        }
      }
    }
    return uni;
  }

  /**
   * Null elements is allowed. Run in O(n^2)
   * @param items
   * @return
   */
  public static List<VirtualItemStack> collapse(List<VirtualItemStack> items) {
    CollapseResult r = new CollapseResult();
    for (VirtualItemStack e : items) {
      if (e != null) r.createOrAdd(e);
    }
    return r.collapsedItems;
  }
  private static class CollapseResult {
    public List<VirtualItemStack> collapsedItems = new ArrayList<>();
    public void createOrAdd(VirtualItemStack e) {
      for (VirtualItemStack v : collapsedItems) {
        if (v.sameType(e)) {
          v.count += Math.min(e.count, e.getMaxCount());
          return;
        }
      }
      // not found
      VirtualItemStack a = e.copy();
      a.count = Math.min(a.count, a.getMaxCount());
      collapsedItems.add(a);
    }
  }

  public static List<VirtualItemType> sortTypes(List<VirtualItemType> types, ISortingMethodProvider provider) {
    return sort(
          types.stream().map(x -> new VirtualItemStack(x, 1)).collect(Collectors.toList())
          , provider
        )
        .stream().map(x -> x.itemtype).collect(Collectors.toList());
  }
  public static List<VirtualItemStack> sort(List<VirtualItemStack> items, ISortingMethodProvider provider) {
    return provider.sort(items);
  }

  public static List<VirtualItemStack> uncollapse(List<VirtualItemStack> items) {
    List<VirtualItemStack> res = new ArrayList<>();
    for (VirtualItemStack v : items) {
      int c = v.count;
      while (c > 0) {
        int del = Math.min(c, v.getMaxCount());
        res.add(v.copyWithCount(del));
        c -= del;
      }
    }
    return res;
  }

  public static List<VirtualItemStack> groups(List<VirtualItemStack> items, IGroupingShapeProvider provider, int size) {
    return provider.group(items, size);
  }

  public static List<Click> diff(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems) {
    List<VirtualItemStack> before = fromItems;
    List<VirtualItemStack> after = IntStream.range(0, fromItems.size())
        .mapToObj(x -> x < toItems.size() ? toItems.get(x) : null).collect(Collectors.toList());
    assert before.size() == after.size();
    final int LEFT = 0;
    // final int RIGHT = 1;
    DiffSandbox sandbox = new DiffSandbox(before, after);
    boolean allCorrect = false;
    while (!allCorrect) {
      allCorrect = true;
      for (int i = 0; i < sandbox.size(); i++) {
        VirtualItemStack fromA = sandbox.get(i);
        if (fromA == null) continue;
        VirtualItemStack toB = after.get(i);
        if (fromA.sameType(toB)) {
          if (fromA.count > toB.count) {
            // sandbox.click(i, LEFT);
            // sandbox.clickNTimes(i, RIGHT, toB.count);
            // sandbox.handleCursor();
            // allCorrect = false;
            sandbox.click(i, LEFT);
            sandbox.handleCursor();
            allCorrect = false;
          } 
        } else {
          sandbox.click(i, LEFT);
          sandbox.handleCursor();
          allCorrect = false;
        }
      }
    }
    return sandbox.clicks;
  }
  public static void diffTargetsFirst_search(int i, DiffSandbox sandbox, VirtualItemStack targetA, List<VirtualItemStack> after){
    final int LEFT = 0;
    for (int k = i + 1; k < sandbox.size(); k++) {
      VirtualItemStack sandboxB = sandbox.get(k);
      if (sandboxB == null) continue;
      if (!targetA.sameType(sandboxB)) continue;
      VirtualItemStack targetB = after.get(k);
      if (!sandboxB.sameType(targetB)) { // ok to grab
        sandbox.click(k, LEFT);
        sandbox.handleCursor();
        return;
      } else { // need to check if target slot having smaller count
        if (targetB.count < sandboxB.count) {
          sandbox.click(k, LEFT);
          sandbox.handleCursor();
          return;
        }
      }
    }
  }
  
  private static void diffCheckPossible(List<VirtualItemStack> before, List<VirtualItemStack> after) {
    List<VirtualItemStack> beforeColl = collapse(before);
    List<VirtualItemStack> afterColl = collapse(after);
    for (VirtualItemStack afterV : afterColl) {
      Iterator<VirtualItemStack> i = beforeColl.iterator();
      while (i.hasNext()) {
        VirtualItemStack s = i.next(); // must be called before you can call i.remove()
        if (afterV.sameType(s)) {
          if (afterV.count == s.count) {
            i.remove();
            break;
          } else {
            Log.error("[inventoryprofiles] item mismatch! " + s + " <-> " + afterV + " (" + (s.count - afterV.count) +" missing)");
            throw new RuntimeException("Not possible from before to after!");
          }
        }
      }
    }
    if (!beforeColl.isEmpty()) {
      Log.error("[inventoryprofiles] beforeColl is not empty! " + beforeColl);
      throw new RuntimeException("Not possible from before to after!");
    }
  }
  public static List<Click> diffTargetsFirst(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems) {
    List<VirtualItemStack> before = fromItems;
    List<VirtualItemStack> after = IntStream.range(0, fromItems.size())
        .mapToObj(x -> x < toItems.size() ? toItems.get(x) : null).collect(Collectors.toList());
    assert before.size() == after.size();
    diffCheckPossible(before, after);
    final int LEFT = 0;
    final int RIGHT = 1;
    DiffSandbox sandbox = new DiffSandbox(before, after);
    for (int i = 0; i < after.size(); i++) {
      VirtualItemStack targetA = after.get(i);
      VirtualItemStack sanboxA = sandbox.get(i);
      if (targetA == null) {
        if (sanboxA != null) {
          sandbox.click(i, LEFT);
          sandbox.handleCursor();
        }
        continue;
      }
      if (!targetA.sameType(sanboxA)) {
        // search for targetA
        diffTargetsFirst_search(i, sandbox, targetA, after);
      } else {
        // same type
        if (sanboxA.count < targetA.count) {
          diffTargetsFirst_search(i, sandbox, targetA, after);
        } else if (sanboxA.count > targetA.count) {
          sandbox.click(i, LEFT);
          sandbox.clickNTimes(i, RIGHT, targetA.count);
          sandbox.handleCursor();
        }
      }
    }

    return sandbox.clicks;
  }
  

  private static class DiffSandbox {
    List<VirtualItemStack> sandboxItems;
    List<VirtualItemStack> targets;
    VirtualItemStack sandboxCursor = null;
    List<Click> clicks = new ArrayList<>();
    public DiffSandbox(List<VirtualItemStack> before, List<VirtualItemStack> after) {
      sandboxItems = before.stream().map(x -> x == null ? null : x.copy()).collect(Collectors.toList());
      targets = after;
    }
    public void click(int index, int button) {
      clicks.add(new Click(index, button));
      if (button == 0) { // left click
        if (sandboxCursor == null) {
          sandboxCursor = sandboxItems.get(index);
          sandboxItems.set(index, null);
        } else {
          VirtualItemStack v = sandboxItems.get(index);
          if (sandboxCursor.sameType(v)) {
            int room = v.getMaxCount() - v.count;
            int del = Math.min(sandboxCursor.count, room);
            v.count += del;
            sandboxCursor.count -= del;
          } else { // swap
            sandboxItems.set(index, sandboxCursor);
            sandboxCursor = v;
          }
        }
      } else if (button == 1) { // right click
        VirtualItemStack v = sandboxItems.get(index);
        if (sandboxCursor == null) { // split half, cursor round up, left round down
          if (v != null) {
            int del = v.count - v.count / 2;
            sandboxCursor = v.copyWithCount(del);
            v.count -= del;
          }
        } else {
          if (sandboxCursor.sameType(v)) {
            v.count++;
            sandboxCursor.count--;
          } else if (v != null) { // swap
            sandboxItems.set(index, sandboxCursor);
            sandboxCursor = v;
          } else {
            sandboxItems.set(index, sandboxCursor.copyWithCount(1));
            sandboxCursor.count--;
          }
        }
      }
      checkCursorZero();
      checkItemZero(index);
    }
    public void clickNTimes(int index, int button, int n) {
      for (int i = 0; i < n; i++) {
        click(index, button);
      }
    }
    public void checkCursorZero() {
      if (sandboxCursor != null && sandboxCursor.count <= 0) sandboxCursor = null;
    }
    public void checkItemZero(int index) {
      VirtualItemStack v = sandboxItems.get(index);
      if (v != null && v.count <= 0) sandboxItems.set(index, null);
    }
    public void handleCursor() {
      while (sandboxCursor != null) {
        handleOnce();
      }
    }
    private void handleOnce() {
      final int LEFT = 0;
      final int RIGHT = 1;
      for (int i = 0; i < targets.size() && sandboxCursor != null; i++) {
        VirtualItemStack targetItem = targets.get(i);
        if (sandboxCursor.sameType(targetItem)) {
          VirtualItemStack sandboxItem = sandboxItems.get(i);
          if (sandboxCursor.sameType(sandboxItem)) {
            // see should i left click once or right click many times
            if (sandboxItem.count == sandboxItem.getMaxCount()) {
              continue;
            }
            if (targetItem.count == targetItem.getMaxCount() 
                || sandboxCursor.count + sandboxItem.count <= targetItem.count) {
              click(i, LEFT);
            } else {
              clickNTimes(i, RIGHT, targetItem.count - sandboxItem.count);
            }
          } else if (sandboxItem == null) {
            if (sandboxCursor.count <= targetItem.count) {
              click(i, LEFT);
            } else {
              clickNTimes(i, RIGHT, targetItem.count);
            }
          } else {
            click(i, LEFT);
            return;
          }
        }
      }
    }
    public int size() {
      return sandboxItems.size();
    }
    public VirtualItemStack get(int index) {
      return sandboxItems.get(index);
    }
    
  }
  public static class Click {
    public int index;
    public int button;

    public Click(int index, int button) {
      this.index = index;
      this.button = button;
    }
    
  }


  public static boolean areItemTypesEqual(Item item1, CompoundTag tag1, Item item2, CompoundTag tag2) {
    if (item1 != item2) return false;
    if (tag1 == null && tag2 != null) {
       return false;
    } else {
       return tag1 == null || tag1.equals(tag2);
    }
  }

  public static class VirtualItemType {
    public final Item item;
    public final CompoundTag tag;

    public VirtualItemType(Item item, CompoundTag tag) {
      this.item = item;
      this.tag = tag;
    }

    public boolean sameAs(VirtualItemType b) {
      return this == b || b != null
          && areItemTypesEqual(item, tag, ((VirtualItemType)b).item, ((VirtualItemType)b).tag);
    }

    public static List<VirtualItemType> getListFrom(List<VirtualItemStack> items) {
      return items.stream().map(x -> x.itemtype).collect(Collectors.toList());
    }

    
    @Override
    public String toString() {
      return this.item + "" + (this.tag == null ? "" : this.tag);
    }

  }
  public static class VirtualItemStack {
    public VirtualItemType itemtype;
    public int count;

    public VirtualItemStack(VirtualItemType itemtype, int count) {
      this.itemtype = itemtype;
      this.count = count;
    }

    public int getMaxCount() {
      return itemtype.item.getMaxCount();
    }

    public boolean sameType(VirtualItemStack b) {
      return b != null && itemtype.sameAs(b.itemtype);
    }

    public VirtualItemStack copy() {
      return new VirtualItemStack(itemtype, count);
    }
    public VirtualItemStack copyWithCount(int count) {
      return new VirtualItemStack(itemtype, count);
    }

    @Override
    public String toString() {
      return this.count + " " + this.itemtype;
    }


  }
  
}