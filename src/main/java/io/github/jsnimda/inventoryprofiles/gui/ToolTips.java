package io.github.jsnimda.inventoryprofiles.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.util.Current;

/**
 * Tooltips
 */
public class ToolTips {

  public static final List<ToolTip> current = new ArrayList<>();
  
  public static class ToolTip {
    public List<String> strings;
    public int x;
    public int y;
    public ToolTip(String string, int x, int y) {
      this.strings = Arrays.asList(string.split("\n"));
      this.x = x;
      this.y = y;
    }
    public ToolTip(List<String> strings, int x, int y) {
      this.strings = strings;
      this.x = x;
      this.y = y;
    }
    public void render() {
      Current.screen().renderTooltip(strings, x, y);
    }
  }

  public static void add(String string, int x, int y) {
    current.add(new ToolTip(string, x, y));
  }
  public static void add(List<String> strings, int x, int y) {
    current.add(new ToolTip(strings, x, y));
  }

  public static void renderAll() {
    for (ToolTip t : current) {
      t.render();
    }
    current.clear();
  }

}