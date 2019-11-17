package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.Collection;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
            if (!match(sel)) {
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
        int sel = IntStream.range(0, currents().size())
          .filter(x -> !matchType(x) && current(x).itemType.equals(type))
          .findFirst().orElse(index);
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
        throw new RuntimeException("shouldn't reach here");
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
      public SortedSet<Integer> unmatches = new TreeSet<>();
      public SortedSet<Integer> fullUnmatches = new TreeSet<>(); // handle this first
      public ScoreGroup(ItemTypeStats info) {
        info.indexes.forEach(x -> {
          if (!matchExact(x)) unmatches.add(x);
          if (!matchExact(x) && target(x).isFull()) fullUnmatches.add(x);
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
      // #region pick
      private List<Integer> candidates(Predicate<Integer> predicate) {
        return unmatches.stream().filter(predicate).collect(Collectors.toList());
      }
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
      private List<Integer> beforePickScore;
      private List<Integer> sortedBeforePickScore;
      public void pick() {
        // for each try left pick and right pick and see which gives decrease in min score
        updateUnmatches();
        if (unmatches.isEmpty()) {
          return;
        }
        if (!fullUnmatches.isEmpty()) {
          // handle this first
          List<Integer> cand = candidates(x -> currentIfMatchType(x).count > target(x).count);
          Integer sel = CodeUtils.selectFirst(cand,
            x -> new GradingResult(x).scoreIgnoreCursor(),
            (x, y) -> y.mappedValue - x.mappedValue
          ).value;
          if (currentIfMatchType(sel).count / 2 <= target(sel).count) // make current smaller than target
            sandbox.rightClick(sel);
          else
            sandbox.leftClick(sel);
          return;
        } // no full target

        beforePickScore = unmatches.stream().map(x -> new GradingResult(x).scoreIgnoreCursor()).collect(Collectors.toList());
        sortedBeforePickScore = beforePickScore.stream().sorted().collect(Collectors.toList());

        List<Distinct> distincts = getDistincts();
        List<TryPickResult> cand = distincts.stream().flatMap(x -> {
          if (currentIfMatchType(x.index).isEmpty()) return Stream.empty();
          if (currentIfMatchType(x.index).count > target(x.index).count) {
            if (getScoreObject(currentIfMatchType(x.index).count, target(x.index).count).shouldRightClick()) {
              return Stream.of(new TryPickResult(x.index, 1));
            } else {
              return Stream.of(new TryPickResult(x.index, 1), new TryPickResult(x.index, 0));
            }
          }
          return Stream.of(new TryPickResult(x.index, 1));
        }).collect(Collectors.toList());

        TryPickResult res = CodeUtils.selectFirst(cand, TryPickResult::compareTo);

        res.clicks.forEach(x -> {
          if (x.button == 0) {
            sandbox.leftClick(x.slotId);
          } else {
            sandbox.rightClick(x.slotId);
          }
        });
      }
      private class TryPickResult implements Comparable<TryPickResult> { 
        public int byIndex;
        public int byButton;
        public List<Click> clicks = new ArrayList<>();
        public int cursorCount;
        public List<Integer> afterPickScore;
        public int mostDecreased;
        public boolean shouldSkipThis;
        public TryPickResult(int index, int button) {
          byIndex = index;
          byButton = button;
          calc();
        }
        public void calc() {
          SortedSet<Integer> backupUnmatches = new TreeSet<>(unmatches);
          int clicksSize = sandbox.clicks.size();
          sandbox.save(backupUnmatches);
          if (byButton == 0)
            sandbox.leftClick(byIndex);
          else
            sandbox.rightClick(byIndex);
          // calc
          while (!cursor().isEmpty())
            handleCursor();
          clicks.addAll(sandbox.clicks.subList(clicksSize, sandbox.clicks.size()));
          cursorCount = cursor().count;

          afterPickScore = backupUnmatches.stream().map(x -> new GradingResult(x).scoreIgnoreCursor()).collect(Collectors.toList());

          mostDecreased = CodeUtils.selectFirst(
            IntStream.range(0, afterPickScore.size())
              .mapToObj(x -> beforePickScore.get(x) - afterPickScore.get(x))
              .collect(Collectors.toList())
            , (x, y) -> y - x
          );

          shouldSkipThis = getSortedScores().equals(sortedBeforePickScore);

          sandbox.restore();
          unmatches = backupUnmatches;
        }

        public int value() {
          return mostDecreased - (cursorCount + clicks.size());
        }

        private List<Integer> sortedScores = null;
        public List<Integer> getSortedScores() {
          if (sortedScores == null) {
            sortedScores = afterPickScore.stream().sorted().collect(Collectors.toList());
          }
          return sortedScores;
        }
        public int compare(List<Integer> a, List<Integer> b) {
          if (a.size() != b.size()) throw new RuntimeException("unsupported");
          for (int i = 0; i < a.size(); i++) {
            if (a.get(i) != b.get(i))
              return a.get(i) - b.get(i);
          }
          return 0;
        }

        @Override
        public int compareTo(TryPickResult o) {
          if (shouldSkipThis || o.shouldSkipThis) {
            if (shouldSkipThis && o.shouldSkipThis) {
              return 0;
            } else {
              return shouldSkipThis ? 1 : -1;
            }
          }
          if (o.value() != value()) {
            return o.value() - value();
          } else {
            // throw new RuntimeException("help!");
            int cmp = compare(getSortedScores(), o.getSortedScores());
            if (cmp != 0) {
              return cmp;
            } else {
              if (byIndex == o.byIndex) {
                return 0;
              } else {
                return Math.random() < 0.5 ? 1 : -1; // FIXME (?)
                // return 0;
              }
            }
          }
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
        return unmatches.stream().mapToInt(x -> new GradingResult(x).scoreIgnoreCursor());
      }
      // #endregion
      // ============
      // #region handleCursor
      private void removeAllMatches(Collection<Integer> indexes) {
        Iterator<Integer> it = indexes.iterator();
        while (it.hasNext()) {
          if (matchExact(it.next())) {
            it.remove();
          }
        }
      }
      public void updateUnmatches() {
        removeAllMatches(unmatches);
        removeAllMatches(fullUnmatches);
      }
      public List<GradingResult> handleCursorCandidates() {
        return unmatches.stream().filter(x -> !currentIfMatchType(x).isFull())
          .map(x -> new GradingResult(x).calc()).collect(Collectors.toList());
      }
      public void handleCursor() {
        updateUnmatches();
        if (unmatches.isEmpty()) {
          throw new AssertionError();
        }
        if (!fullUnmatches.isEmpty()) {
          sandbox.leftClick(fullUnmatches.first());
          return;
        } // no full target
        List<GradingResult> cand = handleCursorCandidates();
        GradingResult sel = CodeUtils.selectFirst(
          cand,
          GradingResult::compareTo
        );
        if (sel == null) {
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
      }
      public void rightClickOthers(int index) { // cursor should be empty after this is called
        // check if there are any non exceed target items other than me
        List<Integer> cand = candidates(x -> x != index && currentIfMatchType(x).count < target(x).count);
        if (cand.isEmpty()) {
          throw new RuntimeException("impossible");
        }
        int amount = cursor().count - (target(index).count - currentIfMatchType(index).count);
        // select closest
        cand.sort((a, b)->{
          int aClose = target(a).count - currentIfMatchType(a).count;
          int bClose = target(b).count - currentIfMatchType(b).count;
          return aClose - bClose;
        });
        int i = 0;
        while (amount > 0) {
          int sel = cand.get(i);
          if (!matchExact(sel)) {
            sandbox.rightClick(sel);
            --amount;
          } else {
            ++i;
          }
        }
        sandbox.leftClick(index);
      }
      // #endregion
      // ============
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
      // public GradingResult(int index, int maxCount, int currentCount, int targetCount, int cursorCount) {
      //   this.index = index;
      //   this.maxCount = maxCount;
      //   this.currentCount = currentCount;
      //   this.targetCount = targetCount;
      //   this.cursorCount = cursorCount;
      // }
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
      public int scoreIgnoreCursor() { // clicks needed in worst case
        return scoreIgnoreCursor(currentCount, targetCount);
      }
      public int scoreIgnoreCursor(int currentCount, int targetCount) {
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
        return scoreIgnoreCursor(afterCount, targetCount)
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
        return scoreIgnoreCursor() - score;
      }
      @Override
      public int compareTo(GradingResult o) {
        if (actionType == null) throw new RuntimeException("unsupported");
        if (actionType == ActionType.RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME || o.actionType == ActionType.RIGHT_CLICK_OTHERS_THEN_LEFT_CLICK_ME) {
          if (score != o.score)
            return score - o.score;
        }
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