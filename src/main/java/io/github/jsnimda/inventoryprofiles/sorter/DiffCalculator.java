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
import java.util.SortedSet;
import java.util.TreeSet;
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
    // #region control
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
    private boolean match(int index) {
      return allowRightClick ? matchExact(index) : matchType(index);
    }
    private void doStage() {
      SortedSet<Integer> indexes = new TreeSet<>();
      for (int i = 0; i < targets().size(); i++) {
        if (!match(i)) indexes.add(i);
      }
      while (!indexes.isEmpty()) {
        Iterator<Integer> it = indexes.iterator();
        while (it.hasNext() || !cursor().isEmpty()) {
          if (cursor().isEmpty()) {
            int sel = it.next();
            if (allowRightClick ? !matchExact(sel) : !matchType(sel)) {
              findPick(sel);
            }
          } else {
            handleCursor();
          }
        }
        it = indexes.iterator();
        while (it.hasNext()) {
          if (match(it.next())) it.remove();
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
        scoreGroups.get(type).handle();
      }
    }

    private void handleCursor() {
      VirtualItemType type = cursor().itemType;
      if (!allowRightClick) {
        scoreGroups.get(type).handleCursor();
      } else {
        scoreGroups.get(type).handle();
      }
    }
    // #endregion
    // ============

    private Map<VirtualItemType, ScoreGroup> scoreGroups = new HashMap<>();
    private void noDropInit() {
      targetStats.getInfos().values().forEach(x -> {
        scoreGroups.put(x.type, new ScoreGroup(x));
      });
    }

    private class ScoreGroup {
      public VirtualItemType type;
      public ItemTypeStats info;
      public SortedSet<Integer> unmatches = new TreeSet<>();
      public ScoreGroup(ItemTypeStats info) {
        this.type = info.type;
        this.info = info;
        info.fromIndexes.forEach(x -> {
          if (!matchExact(x)) unmatches.add(x);
        });
      }
      public void handle() {
        while (!unmatches.isEmpty() || !cursor().isEmpty()) {
          if (cursor().isEmpty()) {
            pick();
          } else {
            handleCursor();
          }
        }
      }
      // ============
      // pick
      private class Distinct {
        int index;
        int currentCount;
        int targetCount;
        public Distinct(int index) {
          this.index = index;
          this.currentCount = currentIfMatchType(index).count;
          this.targetCount = target(index).count;
        }

        @Override
        public int hashCode() {
          final int prime = 31;
          int result = 1;
          result = prime * result + getEnclosingInstance().hashCode();
          result = prime * result + currentCount;
          result = prime * result + targetCount;
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
          Distinct other = (Distinct) obj;
          if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
            return false;
          if (currentCount != other.currentCount)
            return false;
          if (targetCount != other.targetCount)
            return false;
          return true;
        }

        private ScoreGroup getEnclosingInstance() {
          return ScoreGroup.this;
        }
        
      }
      private List<Distinct> getDistincts() {
        List<Distinct> res = new ArrayList<>();
        Set<Distinct> existed = new HashSet<>();
        for (int sel : unmatches) {
          Distinct d = new Distinct(sel);
          if (!existed.contains(d)) {
            res.add(d);
            existed.add(d);
          }
        }
        return res;
      }
      public void pick() {
        clean = true;
        trying = true;
        // for each try left pick and right pick and see which gives decrease in min score
        updateUnmatches();
        if (unmatches.isEmpty()) {
          return;
        }
        int min = minScore();
        int sum = sumScore();
        SortedSet<Integer> backupUnmatches = new TreeSet<>(unmatches);
        List<TryPickResult> cand = backupUnmatches.stream().flatMap(x -> {
          if (currentIfMatchType(x).isEmpty()) return Stream.empty();
          return Stream.of(new TryPickResult(x, 0), new TryPickResult(x, 1));
        }).collect(Collectors.toList());
        TryPickResult res = CodeUtils.selectFirst(cand, TryPickResult::compareTo);
        if (res.shouldIgnoreThis
            || (res.min > min)
            || (res.min == min && res.mostDecreasedSelf <= 0 && res.sum > sum)
            ) {
          throw new RuntimeException("wat?!");
        }
        if (res.byButton == 0) {
          sandbox.leftClick(res.byIndex);
        } else {
          sandbox.rightClick(res.byIndex);
        }
        clean = true;
        trying = false;
      }
      private class TryPickResult implements Comparable<TryPickResult> { 
        public int min;
        public int mostDecreased;
        public int mostDecreasedIndex;
        public int mostDecreasedSelf;
        public int sum;
        public int byIndex;
        public int byButton;
        public int afterIndex = -1;
        public int afterButton = -1;
        public boolean shouldIgnoreThis;
        public TryPickResult(int index, int button) {
          byIndex = index;
          byButton = button;
          calc();
        }
        public void calc() {
          SortedSet<Integer> backupUnmatches = new TreeSet<>(unmatches);
          sandbox.save();
          if (byButton == 0)
            sandbox.leftClick(byIndex);
          else
            sandbox.rightClick(byIndex);
          // calc
          List<GradingResult> cand = handleCursorStreamPre().collect(Collectors.toList());
          boolean matchExactExist = false;
          if (cand.stream().anyMatch(x -> x.scoreWithoutCursor() == 0)) {
            matchExactExist = true;
            cand = cand.stream().filter(x -> x.scoreWithoutCursor() != 0).collect(Collectors.toList());
          }
          cand.forEach(x -> x.calc());
          if (matchExactExist) {
            min = 0;
          } else {
            min = cand.stream().mapToInt(x -> Math.min(x.score, x.scoreWithoutCursor())).min().getAsInt();
          }
          List<GradingResult> mdc = cand.stream().filter(x -> {
            return !(x.index == byIndex && (x.actionType == ActionType.LEFT_CLICK_ME
              || x.actionType == ActionType.RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME));
          }).collect(Collectors.toList());
          //mostDecreased = mdc.stream().mapToInt(x -> x.scoreWithoutCursor() - x.score).max().orElse();
          
          GradingResult g = CodeUtils.selectFirst(mdc,
            (x, y) -> (y.scoreWithoutCursor() - y.score) - (x.scoreWithoutCursor() - x.score)
          );
          if (g != null) {
            mostDecreased = g.scoreWithoutCursor() - g.score;
            mostDecreasedIndex = g.index;
          } else {
            mostDecreased = Integer.MIN_VALUE / 2;
            mostDecreasedIndex = -1;
          }
          mostDecreasedSelf = cand.stream()
            .mapToInt(x -> x.scoreWithoutCursor() - x.score).max().getAsInt();
          sum = scores().sum() - 1 - mostDecreased;

          clean = true;
          handleCursor();
          Click lastClick = sandbox.clicks.get(sandbox.clicks.size()-1);
          afterIndex = lastClick.slotId;
          afterButton = lastClick.button;
          if ((afterButton == 0 && afterIndex == byIndex)
              || (byButton == 0 && afterIndex != mostDecreasedIndex)) {
            shouldIgnoreThis = true;
          } else {
            shouldIgnoreThis = false;
          }
          sandbox.restore();
          unmatches = backupUnmatches;
        }

        @Override
        public int compareTo(TryPickResult o) {
          int aIgnore = shouldIgnoreThis ? 1 : 0;
          int bIgnore = o.shouldIgnoreThis ? 1 : 0;
          if (aIgnore == 1 || bIgnore == 1) {
            return aIgnore - bIgnore;
          }
          if (min != o.min)
            return min - o.min;
          int cmp = o.mostDecreased - mostDecreased;
          return cmp == 0 ? sum - o.sum : cmp;
        }
      }
      public int minScore() {
        if (!cursor().isEmpty()) throw new RuntimeException("unsupported");
        return scores().min().getAsInt();
      }
      public int sumScore() {
        if (!cursor().isEmpty()) throw new RuntimeException("unsupported");
        return scores().sum();
      }
      public IntStream scores() {
        return unmatches.stream().mapToInt(x -> new GradingResult(x).scoreWithoutCursor());
      }
      // ============
      // handleCursor
      public void updateUnmatches() {
        Iterator<Integer> it = unmatches.iterator();
        while (it.hasNext()) {
          if (matchExact(it.next())) {
            it.remove();
          }
        }
      }
      public Stream<GradingResult> handleCursorStreamPre() {
        return unmatches.stream().filter(x -> !currentIfMatchType(x).isFull())
          .map(x -> new GradingResult(x));
      }
      public List<GradingResult> handleCursorCandidates() {
        return handleCursorStreamPre().map(x -> x.calc()).collect(Collectors.toList());
      }
      public boolean clean = false;
      public boolean trying = false;
      public void handleCursor() {
        updateUnmatches();
        if (unmatches.isEmpty()) {
          throw new AssertionError();
        }
        List<GradingResult> cand = handleCursorCandidates();
        Click lastClick = sandbox.clicks.get(sandbox.clicks.size() - 1);
        if (clean == true) {
          cand = cand.stream().filter(
            x -> !(x.index == lastClick.slotId && x.actionType == ActionType.LEFT_CLICK_ME)
          ).collect(Collectors.toList());
        }
        GradingResult sel = CodeUtils.selectFirst(
          cand,
          GradingResult::compareTo
        );
        if (sel == null) {
          if (trying) {
            sandbox.leftClick(lastClick.slotId);
            return;
          }
          throw new RuntimeException("not found");
        }
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
        clean = false;
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
          x -> new GradingResult(
            x, target(x).getMaxCount(), currentIfMatchType(x).count, target(x).count, 1
          ).scoreAfterLeftClickMe(),
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
      // has cursor stack result
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
      public GradingResult(int index, int maxCount, int currentCount, int targetCount, int cursorCount) {
        this.index = index;
        this.maxCount = maxCount;
        this.currentCount = currentCount;
        this.targetCount = targetCount;
        this.cursorCount = cursorCount;
      }
      public GradingResult calc() {
        if (targetCount == currentCount) {
          throw new RuntimeException("unsupported");
        } else if (cursorCount == 0) {
          throw new RuntimeException("unsupported");
        } else if (currentCount >= maxCount) {
          throw new RuntimeException("unsupported");
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
          throw new AssertionError();
        }
        return this;
      }
      public int scoreWithoutCursor() { // clicks needed in worst case
        return scoreWithoutCursor(currentCount, targetCount);
      }
      public int scoreWithoutCursor(int currentCount, int targetCount) {
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
        return scoreWithoutCursor(afterCount, targetCount)
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

      public int decreased() {
        return scoreWithoutCursor() - score;
      }
      public boolean canMatchExact() {
        if (actionType == ActionType.RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME) return false;
        int afterCount = -1;
        if (actionType == ActionType.LEFT_CLICK_ME) {
          afterCount = Math.min(cursorCount + currentCount, maxCount);
        } else if (actionType == ActionType.RIGHT_CLICK_ME) {
          afterCount = Math.min(1 + currentCount, maxCount);
        }
        if (afterCount == targetCount) return true;
        return false;
      }
      @Override
      public int compareTo(GradingResult o) {
        if (actionType == null) throw new RuntimeException("unsupported");
        // int aCanMatch = canMatchExact() ? 1 : 0;
        // int bCanMatch = o.canMatchExact() ? 1 : 0;
        // if (aCanMatch != bCanMatch) {
        //   return bCanMatch - aCanMatch;
        // }
        // if (aCanMatch == 1 && score != o.score) {
        //   return score - o.score;
        // }
        if (decreased() != o.decreased())
          return o.decreased() - decreased();
        if (score != o.score)
          return score - o.score;
        int cmp = o.targetCount - targetCount; // higher first
        return cmp == 0 ? index - o.index : cmp;
      }

    }

    private enum ActionType {
      // SOLVED,
      // UNTOUCH,
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
    if (fromCount <= targetCount || targetCount <= 0) {
      throw new RuntimeException("unsupported");
    }
    return getScoreObject(fromCount, targetCount).count;
  }
  private static Score getScoreObject(int fromCount, int targetCount) {
    if (fromCount <= targetCount || targetCount <= 0) {
      throw new RuntimeException("unsupported");
    }
    if (!scoresLookupTable.containsKey(fromCount)) {
      scoresLookupTable.put(fromCount, ScoresGenerator.scoresFor(fromCount));
    }
    return scoresLookupTable.get(fromCount).get(targetCount);
  }

}