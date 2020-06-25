package io.github.jsnimda.inventoryprofiles.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.jsnimda.common.gui.Tooltips;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;

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
    public void render(MatrixStack matrices) {
      Current.screen().renderTooltip(matrices, stringListToRenderables(strings), x, y);
    }
  }

  /**
   * @deprecated Ideally, {@link ToolTips.ToolTip ToolTip} should use {@link net.minecraft.text.Text Text} or {@link StringRenderable}.
   */
  @Deprecated
  private static List<StringRenderable> stringListToRenderables(List<String> strings) {
    return strings.stream().map(StringRenderable::plain).collect(Collectors.toList());
  }

  public static void add(String string, int x, int y) {
    current.add(new ToolTip(string, x, y));
  }
  public static void add(List<String> strings, int x, int y) {
    current.add(new ToolTip(strings, x, y));
  }

  public static void renderAll(MatrixStack matrices) {
    for (ToolTip t : current) {
      t.render(matrices);
    }
    current.clear();
  }

}