package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;

import io.github.jsnimda.inventoryprofiles.Log;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;

/**
 * DiffCalculator
 */
public class DiffCalculator {

  /* (non-Javadoc)
   * 
   * calc diff algorithm
   * -> (A) match types first
   * -> (B) then match count
   * 
   * for (B) stage:
   * -> if cursor has stack (B.i)
   *    -> find after left click, count is <= target count, (B.i.1)
   *       the closest the better, then priority to the lowest target count
   *    -> if not find:
   *       compare score (estimated click needed, the lower the better) for each unmatch count: (B.i.2)
   *       -> already > target count (B.i.2.1)
   *          if after left click score < before,
   *          record the decrease in score, the higher the better,
   *          and the after score, the lower the better,
   *          else ignore
   *          (should i also check after right click score < before?)
   *       -> after left click > target count (must = true) (B.i.2.2)
   *          compare after left click score and right click until reach score,
   *          if after left click exceed max count, the score + 1
   *          then select the lower,
   *          if left and right equal score, choose do left
   *       if decrease in score exists, find the highest decrease in score, // (B.i.2.1)
   *       else find the lowest after score // (B.i.2.2)
   *       also, for the same scores, priority to the lowest target count
   * -> if cursor no stack (empty cursor) (B.ii)
   *    -> compare all unmatch that current count > target count, (B.ii.1)
   *       find the lowest score (also, then priority to the lowest target count)
   *       if contains first operation "/2", right click, (B.ii.2)
   *       else left click
   * 
   * for (A) stage:
   * -> similar to if cursor has stack in stage (B), except no right click considered
   * 
   */

  public static List<Click> calcDiff(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems, boolean allowDrop) {
    if (fromItems.size() != toItems.size())
      throw new RuntimeException("sizes not match");
    return new CalcDiffInstance(fromItems, toItems, allowDrop).calc();
  }

  private static void checkPossible(VirtualSlotsStats aStats, VirtualSlotsStats bStats, boolean allowDrop) {
    Map<VirtualItemType, Integer> a = aStats.getInfosAs(x->x.totalCount);
    Map<VirtualItemType, Integer> b = bStats.getInfosAs(x->x.totalCount);
    if (allowDrop ? !isSuperset(a, b) : !a.equals(b)) {
      Log.error("[inventoryprofiles] before map:");
      a.forEach((key, value) -> Log.error(key + ":" + value));
      Log.error("[inventoryprofiles] after map:");
      b.forEach((key, value) -> Log.error(key + ":" + value));
      throw new RuntimeException("Not possible from before to after!");
    }
  }
  private static boolean isSuperset(Map<VirtualItemType, Integer> superset, Map<VirtualItemType, Integer> subset) {
    for(VirtualItemType key : subset.keySet()) {
      if (!superset.containsKey(key)) return false;
      if (subset.get(key) > superset.get(key)) return false;
    }
    return true;
  }

  // ============
  // calculator object
  private static class CalcDiffInstance {
    public final List<VirtualItemStack> fromItems;
    public final List<VirtualItemStack> toItems;
    public final boolean allowDrop;
    public final CalcDiffSandbox sandbox;
    private VirtualSlotsStats fromStats;
    private VirtualSlotsStats targetStats;

    public CalcDiffInstance(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems, boolean allowDrop) {
      this.fromItems = fromItems;
      this.toItems = toItems;
      this.allowDrop = allowDrop;
      sandbox = new CalcDiffSandbox(this.fromItems);
      fromStats = new VirtualSlotsStats(this.fromItems);
      targetStats = new VirtualSlotsStats(this.toItems);
    }
    // ============
    // #region properties
    private VirtualItemStack cursor() {
      return sandbox.cursor;
    }
    private List<VirtualItemStack> targets() {
      return toItems;
    }
    private VirtualItemStack target(int index) {
      return targets().get(index);
    }
    private List<VirtualItemStack> currents() {
      return sandbox.items;
    }
    private VirtualItemStack current(int index) {
      return currents().get(index);
    }
    private VirtualItemStack currentIfMatchType(int index) {
      if (matchType(index))
        return current(index);
      return new VirtualItemStack(target(index).itemType, 0);
    }
    private int score(int index) {
      return calcScore(currentIfMatchType(index).count, target(index).count);
    }
    private boolean shouldRightClick(int index) {
      return getScoreObject(currentIfMatchType(index).count, target(index).count).shouldRightClick();
    }
    private boolean matchExact(int index) {
      return DiffCalculator.matchExact(current(index), target(index));
    }
    private boolean matchType(int index) {
      return DiffCalculator.matchType(current(index), target(index));
    }
    // #endregion

    // ============
    // #region core
    public List<Click> calc() {
      checkPossible(fromStats, targetStats, allowDrop);
      if (!allowDrop) {
        doStageANoDrop();
        doStageBNoDrop();
      } else {
        // TODO impl allowDrop
      }
      return sandbox.clicks;
    }

    private void doStage(Function<Integer, Boolean> condition,
        Consumer<Integer> cursorNoStackAction,
        Runnable cursorHasStackAction) {
      boolean matchAny = true;
      while (matchAny) {
        matchAny = false;
        int i = 0;
        while (i < targets().size() || !cursor().isEmpty()) {
          if (cursor().isEmpty()) {
            Integer sel = i++;
            if (condition.apply(sel)) {
              matchAny = true;
              cursorNoStackAction.accept(sel);
            }
          } else { // cursor has stack
            cursorHasStackAction.run();
          }
        }
      }
    }
    private void doStageANoDrop() {
      doStage(sel->!matchType(sel), sel->sandbox.leftClick(sel), ()->B_i(false));
    }
    private void doStageBNoDrop() {
      doStage(sel->!matchExact(sel), sel->B_ii(target(sel).itemType), ()->B_i(true));
    }
    private void B_i(boolean allowRightClick) { // handle cursor
      GradingResult sel = CodeUtils.selectFirst(candidateIndexesCursor(),
        x -> new GradingResult(x, allowRightClick),
        (x, y) -> x.mappedValue.compareTo(y.mappedValue)
      ).mappedValue;
      doAction(sel);
    }
    private void B_ii(VirtualItemType type) {
      List<Integer> cand = candidateIndexesNoCursor(type);
      int sel = CodeUtils.selectFirst(cand, (x, y) -> score(x) - score(y));
      // if any target full stack exists, still unmatch,
      // and right click not enought to fill that, do left click
      List<Integer> fullCand = candidate(type, x -> !matchExact(x) && target(x).isFull());
      if (!fullCand.isEmpty()) {
        int fullSel = CodeUtils.selectFirst(fullCand, (x, y) -> {
          int xRoom = target(x).count - current(x).count;
          int yRoom = target(y).count - current(y).count;
          return yRoom - xRoom; // get the largest room
        });
        int room = target(fullSel).count - current(fullSel).count;
        int rightClickGet = current(sel).count - current(sel).count / 2;
        if (rightClickGet < room) {
          sandbox.leftClick(sel);
          return;
        }
      }
      if (shouldRightClick(sel)) {
        sandbox.rightClick(sel);
      } else {
        sandbox.leftClick(sel);
      }
    }
    private void doAction(GradingResult sel) {
      if (sel.button() == 0) {
        sandbox.leftClick(sel.index);
      } else { // == 1
        sandbox.rightClick(sel.index, sel.afterRightScore - 1);
      }
    }
    private List<Integer> candidateIndexesCursor() {
      return candidate(cursor().itemType, x -> !matchExact(x) && !currentIfMatchType(x).isFull());
    }
    private List<Integer> candidateIndexesNoCursor(VirtualItemType type) {
      return candidate(type, x -> !matchExact(x) && current(x).count > target(x).count);
    }
    private List<Integer> candidate(VirtualItemType type, Predicate<? super Integer> predicate) {
      return targetStats.getInfos().get(type).fromIndexes
        .stream().filter(predicate).collect(Collectors.toList());
    }

    // private void ensureCursorEmpty() {
    //   if (!cursor().isEmpty())
    //     throw new RuntimeException("cursor should be empty");
    // }

    // ============
    // #region GradingResult
    private class GradingResult implements Comparable<GradingResult> { // determine which slot to put when cursor has stack
      public int index;
      public boolean allowRightClick;
      public int targetCount;
      public int currentCount;
      public int cursorCount;
      public int afterCount; // after left click
      public int afterLeftover; // after left click
      public int currentScore;
      public int afterLeftScore;
      public int afterRightScore;
      public GradingResult(int index, boolean allowRightClick) { // cursor stack should same type to target
        this.index = index;
        this.allowRightClick = allowRightClick;
        targetCount = target(index).count;
        currentCount = currentIfMatchType(index).count;
        cursorCount = cursor().count;
        afterCount = currentIfMatchType(index).tryAdd(cursorCount);
        afterLeftover = currentCount + cursorCount - afterCount;
        calc();
      }
      private void calc() {
        currentScore = calcScore(currentCount, targetCount);
        afterLeftScore = calcScore(afterCount, targetCount) + (afterLeftover > 0 ? 2 : 1);
        afterRightScore = (targetCount - currentCount) + 1;
      }
      private boolean afterNotExceedTarget() { // (B.i.1)
        return afterCount <= targetCount;
      }
      private int close() { // (B.i.1)
        return targetCount - afterCount;
      }
      private boolean decreaseInAfterScore() { // (B.i.2.1)
        if (currentCount > targetCount) {
          return afterLeftScore < currentScore;
        }
        return false;
      }
      private int decrease() { // (B.i.2.1)
        return currentScore - afterLeftScore;
      }
      private int afterScore() { // (B.i.2.2)
        if (allowRightClick)
          return afterLeftScore <= afterRightScore ? afterLeftScore : afterRightScore;
        else
          return afterLeftScore;
      }
      private int button() { // 0 for left, 1 for right
        if (allowRightClick)
          if (afterNotExceedTarget() || decreaseInAfterScore())
            return 0;
          else
            return afterLeftScore <= afterRightScore ? 0 : 1;
        else
          return 0;
      }

      @Override
      public int compareTo(GradingResult o) {
        return compare(this, o);
      }

    }
    public static int compare(GradingResult a, GradingResult b) {
      int cmp = regularCompare(a, b);
      return cmp == 0 ? defaultCompare(a, b) : cmp;
    }
    public static int regularCompare(GradingResult a, GradingResult b) {
      int aInt = a.afterNotExceedTarget() ? 1 : 0;
      int bInt = b.afterNotExceedTarget() ? 1 : 0;
      if (aInt != bInt)
        return bInt - aInt; // not exceed first
      else if (aInt == 1) // both == 1
        return a.close() - b.close(); // closer first
      else { // both == 0
        aInt = a.decreaseInAfterScore() ? 1 : 0;
        bInt = b.decreaseInAfterScore() ? 1 : 0;
        if (aInt != bInt)
          return bInt - aInt; // has decrease first
        else if (aInt == 1) // both == 1
          return a.decrease() - b.decrease();
        else
          return a.afterScore() - b.afterScore();
      }
    }
    public static int defaultCompare(GradingResult a, GradingResult b) {
      int cmp = a.targetCount - b.targetCount;
      return cmp == 0 ? a.index - b.index : cmp;
    }
    // #endregion
    
    // #endregion
  }

  // ============
  // core logics
  private static boolean matchExact(VirtualItemStack a, VirtualItemStack b) {
    return a.equals(b);
  }
  private static boolean matchType(VirtualItemStack a, VirtualItemStack b) {
    // b is target
    if (a.isEmpty()) return true;
    if (b.isEmpty()) return false;
    return a.sameType(b);
  }

  // ============
  // sandbox

  private static final int INF_LOOP_MAX = 10000;

  private static class CalcDiffSandbox {
    public List<Click> clicks = new ArrayList<>();
    public List<VirtualItemStack> items;
    public VirtualItemStack cursor = VirtualItemStack.empty();
    public CalcDiffSandbox(List<VirtualItemStack> items) { // do copy
      this.items = items.stream().map(x->x.copy()).collect(Collectors.toList());
    }
    
    public void addClickLimited(Click c) {
      if (clicks.size() >= INF_LOOP_MAX) 
        throw new RuntimeException("Infinite loop detected");
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

  // ============
  // scores look up

  private static class ScoresGenerator {
    public static List<Score> scoresFor(int fromCount) { // ind 0 and fromCount is null
      List<Score> o = new ArrayList<>();
      for(int i = 0; i <= fromCount; i++) o.add(null);
      Queue<Operation> arr = new LinkedList<>();
      arr.add(minus1(fromCount, new ArrayList<>(), 0));
      arr.add(divide2(fromCount, new ArrayList<>(), 0));
      arr.add(plus1(0, new ArrayList<>(), 2));
      while(!arr.isEmpty()) {
        Operation oper = arr.poll();
        int k = oper.to;
        if (k <= 0 || k >= fromCount) continue;
        if (o.get(k) == null || oper.count < o.get(k).count) {
          o.set(k, initer(oper.count));
        }
        if (oper.count == o.get(k).count) {
          o.get(k).opss.add(oper.ops);
          arr.add(minus1(k, oper.ops, oper.count));
          arr.add(plus1(k, oper.ops, oper.count));
          arr.add(divide2(k, oper.ops, oper.count));
        }
      }
      return o;
    }
    private static Score initer(int count) {
      return new Score(count);
    }
    private static List<OperationType> concat(List<OperationType> ops, OperationType op) {
      List<OperationType> res = new ArrayList<>(ops);
      res.add(op);
      return res;
    }
    private static Operation minus1(int from, List<OperationType> ops, int count) {
      return new Operation(
        concat(ops, OperationType.MINUS_1),
        from - 1,
        count + ((!ops.isEmpty() && ops.get(ops.size()-1) == OperationType.MINUS_1)
          ? 1 : 3)
        );
    }
    private static Operation plus1(int from, List<OperationType> ops, int count) {
      return new Operation(
        concat(ops, OperationType.PLUS_1),
        from + 1,
        count + 1
        );
    }
    private static Operation divide2(int from, List<OperationType> ops, int count) {
      return new Operation(
        concat(ops, OperationType.DIVIDE_2),
        from / 2,
        count + 2
        );
    }
  }
  private static class Score {
    public int count;
    public List<List<OperationType>> opss = new ArrayList<>();
    public Score(int count) {
      this.count = count;
    }
    public boolean shouldRightClick() {
      return opss.stream().anyMatch(x -> !x.isEmpty()
          && x.get(0) == OperationType.DIVIDE_2);
    }
  }
  private static class Operation {
    public List<OperationType> ops;
    public int to;
    public int count;
    public Operation(List<OperationType> ops, int to, int count) {
      this.ops = ops;
      this.to = to;
      this.count = count;
    }
    
  }
  private enum OperationType {
    MINUS_1,
    PLUS_1,
    DIVIDE_2;
    @Override
    public String toString() {
      switch(this) {
        case MINUS_1: return "-1";
        case PLUS_1: return "+1";
        case DIVIDE_2: return "/2";
      }
      throw new AssertionError("Unreachable");
    }
  }

  private static HashMap<Integer, List<Score>> scoresLookupTable = new HashMap<>();

  private static int calcScore(int fromCount, int targetCount) {
    if (fromCount == targetCount) return 0;
    if (targetCount == 0) return 2;
    if (fromCount < targetCount) return Integer.MAX_VALUE; // never
    return getScoreObject(fromCount, targetCount).count;
  }
  private static Score getScoreObject(int fromCount, int targetCount) {
    if (!scoresLookupTable.containsKey(fromCount)) {
      scoresLookupTable.put(fromCount, ScoresGenerator.scoresFor(fromCount));
    }
    return scoresLookupTable.get(fromCount).get(targetCount);
  }

}