package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.jsnimda.inventoryprofiles.Log;

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
      ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    try {
      VirtualSlotsStats vs = new VirtualSlotsStats(items);
      List<VirtualItemStack> uni = vs.uniquified;
      List<VirtualItemStack> collapsed = vs.getInfos().entrySet().stream().map(x->new VirtualItemStack(x.getKey(), x.getValue().totalCount)).collect(Collectors.toList());
      List<VirtualItemStack> res = groups(uncollapse(sort(collapsed, sortingProvider)), groupingProvider, uni.size());
      //return targetsFirst ? diffTargetsFirst(uni, res) : diff(uni, res);
      
      List<VirtualItemStack> after = IntStream.range(0, uni.size())
        .mapToObj(x -> x < res.size() ? res.get(x) : null).collect(Collectors.toList());
      return DiffCalculator.calcDiff(
        uni.stream().map(x->x==null?VirtualItemStack.empty():x).collect(Collectors.toList()), 
        after.stream().map(x->x==null?VirtualItemStack.empty():x).collect(Collectors.toList()), 
        false);
    } catch (RuntimeException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public static List<VirtualItemType> sortTypes(List<VirtualItemType> types, ISortingMethodProvider provider) {
    return sort(
          types.stream().map(x -> new VirtualItemStack(x, 1)).collect(Collectors.toList())
          , provider
        )
        .stream().map(x -> x.itemType).collect(Collectors.toList());
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
        res.add(v.copy(del));
        c -= del;
      }
    }
    return res;
  }

  public static List<VirtualItemStack> groups(List<VirtualItemStack> items, IGroupingShapeProvider provider, int size) {
    return provider.group(items, size);
  }

  public static List<OldClick> diff(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems) {
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
    Map<VirtualItemType, VirtualItemStack> a = new VirtualSlotsStats(before).getInfosAs(x->new VirtualItemStack(x.type, x.totalCount));
    Map<VirtualItemType, VirtualItemStack> b = new VirtualSlotsStats(after).getInfosAs(x->new VirtualItemStack(x.type, x.totalCount));
    if (!a.equals(b)) {
      Log.error("[inventoryprofiles] before map:");
      a.forEach((key, value) -> Log.error(key + ":" + value));
      Log.error("[inventoryprofiles] after map:");
      b.forEach((key, value) -> Log.error(key + ":" + value));
      throw new RuntimeException("Not possible from before to after!");
    }
  }
  public static List<OldClick> diffTargetsFirst(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems) {
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
    List<OldClick> clicks = new ArrayList<>();
    public DiffSandbox(List<VirtualItemStack> before, List<VirtualItemStack> after) {
      sandboxItems = before.stream().map(x -> x == null ? null : x.copy()).collect(Collectors.toList());
      targets = after;
    }
    public void click(int index, int button) {
      clicks.add(new OldClick(index, button));
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
            sandboxCursor = v.copy(del);
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
            sandboxItems.set(index, sandboxCursor.copy(1));
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


  
}