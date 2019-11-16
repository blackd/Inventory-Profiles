package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import io.github.jsnimda.inventoryprofiles.sorter.IGroupingShapeProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;

/**
 * GroupingShapeProviders
 */
public class GroupingShapeProviders {

  public static final IGroupingShapeProvider PRESERVED;
  public static final IGroupingShapeProvider RANDOM;
  public static final IGroupingShapeProvider COLUMNS;
  public static final IGroupingShapeProvider ROWS;
  public static final IGroupingShapeProvider TRANSPOSE;

  static {
    PRESERVED = items -> items;
    RANDOM = (items) -> {
      List<VirtualItemStack> copy = new ArrayList<>(items);
      Collections.shuffle(copy);
      return copy;
    };
    COLUMNS = columnsProvider(9);
    ROWS = rowsProvider(9);
    TRANSPOSE = transposeProvider(9);
  }

  /**
   * width is the target container columns count
   */
  public static IGroupingShapeProvider rowsProvider(int width) {
    return (items) -> 
      transposeProvider(width).group(columnsProvider(items.size() / width, true).group(items));
  }
  /**
   * width is the target container columns count.
   * <p>
   * treat input [items] is inside a container that has a [the target container rows count] of columns,
   * then do the transpose like matrix
   */
  public static IGroupingShapeProvider transposeProvider(int width) {
    return (items) -> transpose(items, items.size() / width, width);
  }
  private static List<VirtualItemStack> transpose(List<VirtualItemStack> grouped, int groupedWidth, int groupedHeight) {
    if (groupedWidth * groupedHeight != grouped.size())
      throw new RuntimeException("Container is not rectangular!");
    List<VirtualItemStack> result =  new ArrayList<>(Collections.nCopies(groupedWidth * groupedHeight, null));
    int resultWidth = groupedHeight;
    //int resultHeight = groupedWidth;
    for (int y = 0; y < groupedHeight; y++) {
      for (int x = 0; x < groupedWidth; x++) {
        int ind = y * groupedWidth + x;
        int ind2 = x * resultWidth + y;
        if (ind < grouped.size()) {
          result.set(ind2, grouped.get(ind));
        }
      }
    }
    return result;
  };

  public static IGroupingShapeProvider columnsProvider(int width) {
    return columnsProvider(width, false);
  }
  public static IGroupingShapeProvider columnsProvider(int width, boolean isTransposed) { // only works for sorted items
    return (items) -> {
      VirtualSlotsStats stats = new VirtualSlotsStats(items);
      if (items.size() == 0) return items;
      int height = stats.size / width; // assumed rectangular
      if (width * height != stats.size)
        throw new RuntimeException("Container is not rectangular!");

      List<Integer> itemsIdentity = stats.getInfosAsList(x->x.stackCount);
      int minRows = itemsIdentity.size();
      if (minRows == 0) return items;

      ColumnsCandidate bestCc = null;

      int neededColumnsCount = 1;
      while (neededColumnsCount <= width) {
        if (minRows > height * neededColumnsCount) {
          neededColumnsCount++;
          continue;
        }
        List<Integer> widths = CodeUtils.distribute(width, neededColumnsCount);
        ColumnsCandidate cc = new ColumnsCandidate(itemsIdentity, widths, height);
        if (cc.succeeded) {
          if (cc.brokenGroups == 0) {
            return cc.apply(items, isTransposed);
          }
          if (bestCc == null || bestCc.brokenGroups > cc.brokenGroups) {
            bestCc = cc;
          }
        }
        ++neededColumnsCount;
      }
      if (bestCc != null) return bestCc.apply(items, isTransposed);

      return PRESERVED.group(items);
    };
  }
  private static class ColumnsCandidate {
    public int brokenGroups = 0;
    public final boolean succeeded;
    private final List<Integer> itemsIdentity;
    private final List<Integer> widths;
    private final int width;
    private final int height;
    private final int columnsCount;
    private final List<Integer> spaceWidths;
    private boolean[] occupied;
    private int cursor = 0;
    private int loopCount = 0;
    private List<List<Integer>> rowsForStacks = new ArrayList<>();
      // ^ should have the same size as itemsIdentity
    public ColumnsCandidate(List<Integer> itemsIdentity, List<Integer> widths, int height) {
      this.itemsIdentity = itemsIdentity;
      this.widths = widths;
      width = widths.stream().mapToInt(x->x).sum();
      this.height = height;
      columnsCount = widths.size();
      occupied = new boolean[columnsCount * height];
      List<Integer> spaceWidths = new ArrayList<>();
      for (int i = 0; i < occupied.length; i++) {
        spaceWidths.add(widths.get(i / height));
      }
      this.spaceWidths = spaceWidths;
      for (int stacks : itemsIdentity) {
        if (!addStack(stacks)) {
          succeeded = false;
          return;
        }
      }
      assert itemsIdentity.size() == rowsForStacks.size();
      succeeded = true;
    }
    private boolean addStack(int stacks) {
      if (!findCursor()) return false;
      List<Integer> rows = new ArrayList<>();
      rowsForStacks.add(rows);
      boolean justFillFlag = false;
      if (loopCount == 0) {
        // first loop, try not to break groups apart
        int cursorY = cursor % height;
        int vStacks = stacks;
        int spaceHeight = 0;
        for ( ; cursor + spaceHeight < occupied.length && vStacks > 0; spaceHeight++) {
          vStacks -= spaceWidths.get(cursor + spaceHeight);
        }
        if (vStacks <= 0) {
          if (spaceHeight > height || cursorY + spaceHeight <= height) {
            for (int i = 0; i < spaceHeight; i++) {
              rows.add(cursor);
              occupied[cursor] = true;
              cursor++;
            }
            return true;
          } else {
            cursor += height - cursorY;
            justFillFlag = true;
          }
        } else {
          justFillFlag = true;
        }
      } 
      if (loopCount > 0 || justFillFlag) {
        int old_cursor = cursor;
        while (stacks > 0) {
          if (!findCursor()) return false;
          stacks -= spaceWidths.get(cursor);
          rows.add(cursor);
          occupied[cursor] = true;
          if (!isNear(old_cursor, cursor)) brokenGroups++;
          old_cursor = cursor;
        }
      }
      return true;
    }
    private boolean isNear(int cursor1, int cursor2) {
      if (cursor1 == cursor2) return true;
      RowInfo info1 = new RowInfo(cursor1);
      RowInfo info2 = new RowInfo(cursor2);
      if (info1.columnIndex == info2.columnIndex) {
        return Math.abs(info1.y - info2.y) <= 1;
      } else if (info1.y == info2.y) {
        return Math.abs(info1.columnIndex - info2.columnIndex) <= 1;
      }
      return false;
    }
    private boolean findCursor() {
      if (cursor >= occupied.length) {
        if (loopCount > 0) return false;
        loopCount++;
        cursor = 0;
      }
      while (occupied[cursor]) {
        cursor++;
        if (cursor >= occupied.length) {
          if (loopCount > 0) return false;
          loopCount++;
          cursor = 0;
        }
      }
      return true;
    }

    private class RowInfo {
      public int x_base;
      public int y;
      public int columnIndex;
      public int spaceWidth;
      
      public RowInfo(int row) {
        y = row % height;
        columnIndex = row / height;
        spaceWidth = widths.get(columnIndex); // = spaceWidths.get(row)
        x_base = IntStream.range(0, columnIndex).map(x->widths.get(x)).sum();
      }
    }

    public List<VirtualItemStack> apply(List<VirtualItemStack> items, boolean isTransposed) {
      List<VirtualItemStack> result =  new ArrayList<>(Collections.nCopies(width * height, VirtualItemStack.empty()));
      int itemsIndex = 0;
      for (int i = 0; i < itemsIdentity.size(); i++) {
        int stacks = itemsIdentity.get(i);
        List<Integer> rows = rowsForStacks.get(i);
        Collections.sort(rows);
        List<Integer> slots = slotIndexs(rows, isTransposed);
        for (int k = 0; k < stacks; k++) {
          VirtualItemStack v = items.get(itemsIndex + k);
          result.set(slots.get(k), v);
        }
        itemsIndex += stacks;
      }
      return result;
    }
    private List<Integer> slotIndexs(List<Integer> rows, boolean isTransposed) {
      List<Integer> res = new ArrayList<>();
      for (int row : rows) {
        RowInfo info = new RowInfo(row);
        for (int rowX = 0; rowX < info.spaceWidth; rowX++) {
          int x = info.x_base + rowX;
          res.add(getIndexByXY(x, info.y));
        }
      }
      if (!isTransposed) {
        Collections.sort(res);
      } else {
        res.sort(new Comparator<Integer>() {

          @Override
          public int compare(Integer o1, Integer o2) {
            int x1 = o1 % width;
            int y1 = o1 / width;
            int x2 = o2 % width;
            int y2 = o2 / width;
            return (x1*height+y1)-(x2*height+y2);
          }

        });
      }
      return res;
    }
    private int getIndexByXY(int x, int y) {
      return y * width + x;
    }
  }

}