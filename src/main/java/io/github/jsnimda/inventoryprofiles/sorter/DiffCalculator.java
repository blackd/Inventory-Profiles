package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Predicate;

import io.github.jsnimda.inventoryprofiles.Log;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.config.Configs.Tweaks;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats.ItemTypeStats;
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
    fromItems = VirtualSlotsStats.uniquify(fromItems);
    toItems = VirtualSlotsStats.uniquify(toItems);
    return new CalcDiffInstance(fromItems, toItems, allowDrop).calc();
  }

  private static void checkPossible(VirtualSlotsStats aStats, VirtualSlotsStats bStats, boolean allowDrop) {
    Map<VirtualItemType, Integer> a = aStats.getInfosAsMap(x->x.totalCount);
    Map<VirtualItemType, Integer> b = bStats.getInfosAsMap(x->x.totalCount);
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
    public final VirtualItemStacksSandbox sandbox;
    private VirtualSlotsStats fromStats;
    private VirtualSlotsStats targetStats;

    public CalcDiffInstance(List<VirtualItemStack> fromItems, List<VirtualItemStack> toItems, boolean allowDrop) {
      this.fromItems = fromItems;
      this.toItems = toItems;
      this.allowDrop = allowDrop;
      sandbox = new VirtualItemStacksSandbox(this.fromItems);
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
    private boolean matchExact(int index) {
      return DiffCalculator.matchExact(current(index), target(index));
    }
    private boolean matchType(int index) {
      return DiffCalculator.matchType(current(index), target(index));
    }
    // #endregion
    // ============

    public List<Click> calc() {
      try{
        checkPossible(fromStats, targetStats, allowDrop);
        long st = System.nanoTime();
        if (!allowDrop) {
          noDropInit();
          doStageANoDrop();
          doStageBNoDrop();
        } else {
          // TODO impl allowDrop
        }
        long ed = System.nanoTime();
        if (AdvancedOptions.DEBUG_LOGS.getBooleanValue()) {
          Log.info("[inventoryprofiles] Execute calcDiff() in " + (ed - st)/(double)1000000 + " ms");
        }
      } catch (Throwable e) {
        if (AdvancedOptions.DEBUG_LOGS.getBooleanValue()) {
          e.printStackTrace();
          return sandbox.clicks;
        } else {
          throw e;
        }
      };
      return sandbox.clicks;
    }

    private boolean allowRightClick = false;

    private void doStageANoDrop() {
      allowRightClick = false;
      doStage();
    }

    private void doStageBNoDrop() {
      allowRightClick = true;
      doStage();
    }

    private void doStage() {
      boolean matchAny = true;
      while (matchAny) {
        matchAny = false;
        int i = 0;
        while (i < targets().size() || !cursor().isEmpty()) {
          if (cursor().isEmpty()) {
            int sel = i++;
            if (allowRightClick ? !matchExact(sel) : !matchType(sel)) {
              matchAny = true;
              findPick(sel);
            }
          } else {
            handleCursor();
          }
        }
      }
    }

    private void findPick(int index) {
      if (target(index).isEmpty()) {
        sandbox.leftClick(index);
        return;
      }
      VirtualItemType type = target(index).itemType;
      if (!allowRightClick) { // stage A
        int sel = IntStream.range(0, targets().size())
          .filter(x -> !matchType(x) && current(x).itemType.equals(type))
          .findFirst().getAsInt();
        sandbox.leftClick(sel);
      } else { // stage B
        scoreGroups.get(type).pick();
      }
    }

    private void handleCursor() {
      VirtualItemType type = cursor().itemType;
      scoreGroups.get(type).handleCursor();
    }

    private Map<VirtualItemType, ScoreGroup> scoreGroups = new HashMap<>();
    private void noDropInit() {
      targetStats.getInfos().values().forEach(x -> {
        scoreGroups.put(x.type, new ScoreGroup(x));
      });
    }

    private class ScoreGroup {
      public VirtualItemType type;
      public ItemTypeStats info;
      public Set<Integer> unmatches = new HashSet<>();
      public ScoreGroup(ItemTypeStats info) {
        this.type = info.type;
        this.info = info;
        info.fromIndexes.forEach(x -> {
          if (!matchExact(x)) unmatches.add(x);
        });
      }
      public void pick() {
        // for each try left pick and right pick and see which gives decrease in min score
        updateUnmatches();
        int min = minScore();
        int sum = sumScore();
        Set<Integer> backupUnmatches = new HashSet<>(unmatches);
        List<TryPickResult> cand = backupUnmatches.stream().flatMap(x -> {
          if (currentIfMatchType(x).isEmpty()) return Stream.empty();
          TryPickResult left = new TryPickResult(x, 0);
          TryPickResult right = new TryPickResult(x, 1);
          return Stream.of(left, right);
        }).collect(Collectors.toList());
        TryPickResult res = CodeUtils.selectFirst(cand, TryPickResult::compareTo);
        // if (res.resMin > min)
        //   throw new RuntimeException("wat?!");
        // if (res.resMin == min && res.resSum > sum)
        //   throw new RuntimeException("wat?!");
        if (res.resByButton == 0) {
          sandbox.leftClick(res.resByIndex);
        } else {
          sandbox.rightClick(res.resByIndex);
        }
      }
      private class TryPickResult implements Comparable<TryPickResult> { 
        public int resMin;
        public int resSum;
        public int resByIndex;
        public int resByButton;
        public TryPickResult(int index, int button) {
          Set<Integer> backupUnmatches = new HashSet<>(unmatches);
          resByIndex = index;
          resByButton = button;
          sandbox.save();
          if (button == 0)
            sandbox.leftClick(index);
          else
            sandbox.rightClick(index);
          resMin = minScore();
          resSum = sumScore();
          handleCursor();
          Click lastClick = sandbox.clicks.get(sandbox.clicks.size()-1);
          if (lastClick.button == 0 && lastClick.slotId == resByIndex) {
            resMin = Integer.MAX_VALUE;
            resSum = Integer.MAX_VALUE;
          }
          sandbox.restore();
          unmatches = backupUnmatches;
        }

        @Override
        public int compareTo(TryPickResult o) {
          int cmp = resMin - o.resMin;
          return cmp == 0 ? resSum - o.resSum : cmp;
        }
      }
      public int minScore() {
        return unmatches.stream().mapToInt(x -> cursor().isEmpty()
          ? new GradingResult(x).scoreWithoutCursor() : new GradingResult(x).calc().score)
          .min().getAsInt();
      }
      public int sumScore() {
        int sum = unmatches.stream().map(x -> new GradingResult(x)).mapToInt(x -> x.scoreWithoutCursor()).sum();
        if (cursor().isEmpty()) {
          return sum;
        }
        // compare scoreWithoutCursor and score, get decreased the most
        int decreasedTheMost = unmatches.stream().map(x -> new GradingResult(x).calc())
          .mapToInt(x -> x.scoreWithoutCursor() - x.score).max().getAsInt();
        return sum - decreasedTheMost;
      }
      public void updateUnmatches() {
        Iterator<Integer> it = unmatches.iterator();
        while (it.hasNext()) {
          if (matchExact(it.next())) {
            it.remove();
          }
        }
      }
      public void handleCursor() {
        updateUnmatches();
        GradingResult sel = CodeUtils.selectFirst(
          unmatches.stream().map(x -> new GradingResult(x).calc())
            .filter(x -> x.canHandleCursor()).collect(Collectors.toList()),
          GradingResult::compareTo
        );
        if (sel == null)
          throw new RuntimeException("not found");
        switch(sel.actionType) {
        case LEFT_CLICK_ME:
          sandbox.leftClick(sel.index);
          return;
        case RIGHT_CLICK_ME:
          sandbox.rightClick(sel.index);
          return;
        case RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME:
          rightClickOthers(sel.index);
          break;
        default:
          throw new AssertionError();
        }
      }
      public void rightClickOthers(int index) {
        // check if there are any non exceed target items other than me
        List<Integer> cand = unmatches.stream()
          .filter(x -> x != index && currentIfMatchType(x).count < target(x).count)
          .collect(Collectors.toList());
        if (cand.isEmpty()) {
          throw new RuntimeException("impossible");
        }
        int sel = CodeUtils.selectFirst(cand,
          x -> new GradingResult(currentIfMatchType(x).count, target(x).count, 1).scoreAfterLeftClickMe(),
          (x, y) -> x.mappedValue - y.mappedValue
        ).value;
        sandbox.rightClick(sel);
      }
    }

    private class GradingResult implements Comparable<GradingResult> {
      public int index;
      public int maxCount;
      public int currentCount;
      public int targetCount;
      public int cursorCount;
      public ActionType actionType = null;
      public int score = Integer.MAX_VALUE;
      public GradingResult(int index) {
        this.index = index;
        maxCount = target(index).getMaxCount();
        currentCount = currentIfMatchType(index).count;
        targetCount = target(index).count;
        if (!cursor().isEmpty() && !cursor().sameType(target(index)))
          throw new AssertionError();
        cursorCount = cursor().count;
      }
      public GradingResult(int currentCount, int targetCount, int cursorCount) {
        this.index = -1;
        this.currentCount = currentCount;
        this.targetCount = targetCount;
        this.cursorCount = cursorCount;
      }
      public boolean canHandleCursor() {
        return actionType != ActionType.SOLVED && actionType != ActionType.UNTOUCH;
      }
      public GradingResult calc() {
        if (targetCount == currentCount) {
          actionType = ActionType.SOLVED;
          score = 0;
        } else if (cursorCount == 0) {
          actionType = ActionType.UNTOUCH;
          score = scoreWithoutCursor();
        } else {
          // for same score, perfer untouch -> r then l -> right -> left
          if (currentCount < targetCount && allowRightClick) {
            if (cursorCount > targetCount - currentCount) {
              if (scoreAfterRightClickOthersThenLeftClickMe() < score) {
                actionType = ActionType.RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME;
                score = scoreAfterRightClickOthersThenLeftClickMe();
              }
            }
            if (scoreAfterRightClickMe() < score) {
              actionType = ActionType.RIGHT_CLICK_ME;
              score = scoreAfterRightClickMe();
            }
          }
          if (scoreAfterLeftClickMe() < score) {
            actionType = ActionType.LEFT_CLICK_ME;
            score = scoreAfterLeftClickMe();
          }
        }
        if (actionType == null) {
          actionType = ActionType.UNTOUCH;
          score = scoreWithoutCursor();
        }
        return this;
      }
      public int scoreWithoutCursor() { // clicks needed in worst case
        if (currentCount > targetCount) {
          return lookupScore(currentCount, targetCount);
        } else {
          return targetCount - currentCount;
        }
      }
      public int scoreAfterLeftClickMe() {
        if (cursorCount == 0) 
          throw new RuntimeException("this shouldn't be called");
        int afterCount = Math.min(cursorCount + currentCount, maxCount);
        int afterLeftover = currentCount + cursorCount - afterCount;
        return new GradingResult(afterCount, targetCount, afterLeftover).scoreWithoutCursor()
          + (afterLeftover > 0 ? 2 : 1);
      }
      public int scoreAfterRightClickMe() {
        if (currentCount >= targetCount || cursorCount == 0)
          throw new RuntimeException("this shouldn't be called");
        if (cursorCount <= targetCount - currentCount) {
          return targetCount - currentCount;
        } else {
          return targetCount - currentCount + 1;
        }
      }
      public int scoreAfterRightClickOthersThenLeftClickMe() {
        if (currentCount >= targetCount || cursorCount == 0
            || cursorCount <= targetCount - currentCount)
          throw new RuntimeException("this shouldn't be called");
        return cursorCount - (targetCount - currentCount) + 1;
      }

      @Override
      public int compareTo(GradingResult o) {
        int aDecrease = scoreWithoutCursor() - score;
        int bDecrease = o.scoreWithoutCursor() - o.score;
        if (aDecrease != bDecrease)
          return bDecrease - aDecrease;
        if (score != o.score)
          return score - o.score;
        int cmp = o.targetCount - targetCount; // higher first
        return cmp == 0 ? index - o.index : cmp;
      }

    }

    private enum ActionType {
      SOLVED,
      UNTOUCH,
      LEFT_CLICK_ME,
      RIGHT_CLICK_ME,
      RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME
    }

  }

  // ============
  // core logics
  private static boolean matchExact(VirtualItemStack a, VirtualItemStack b) {
    return a.equals(b);
  }
  private static boolean matchType(VirtualItemStack a, VirtualItemStack b) { // b is target
    if (a.isEmpty()) return true;
    if (b.isEmpty()) return false;
    return a.sameType(b);
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

  private static int lookupScore(int fromCount, int targetCount) {
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