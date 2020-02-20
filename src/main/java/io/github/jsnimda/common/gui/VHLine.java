package io.github.jsnimda.common.gui;

import net.minecraft.client.gui.DrawableHelper;

public class VHLine {

  // fix 1.14.4 DrawableHelper hLine/vLine offsetted by 1 px
  public static void h(int x1, int x2, int y, int color) { // x1 x2 inclusive
    if (x2 < x1) {
      int m = x1;
      x1 = x2;
      x2 = m;
    }
    DrawableHelper.fill(x1, y, x2 + 1, y + 1, color);
  }
  public static void v(int x, int y1, int y2, int color) { // y1 y2 inclusive
    if (y2 < y1) {
      int m = y1;
      y1 = y2;
      y2 = m;
    }
    DrawableHelper.fill(x, y1, x + 1, y2 + 1, color);
  }
  public static void outline(int x1, int y1, int x2, int y2, int color) {
    // same size with fill(...)
    --x2;
    --y2;
    h(x1, x2, y1, color);
    h(x1, x2, y2, color);
    v(x1, y1, y2, color);
    v(x2, y1, y2, color);
  }

  public static boolean contains(int x1, int y1, int x2, int y2, int x, int y) {
    return x >= x1 && x < x2 && y >= y1 && y < y2;
  }
  // public static boolean contains(double x1, double y1, double x2, double y2, double x, double y) {
  //   return x >= x1 && x < x2 && y >= y1 && y < y2;
  // }

}