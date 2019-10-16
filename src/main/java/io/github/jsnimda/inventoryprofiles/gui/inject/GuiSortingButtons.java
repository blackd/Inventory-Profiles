package io.github.jsnimda.inventoryprofiles.gui.inject;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import fi.dy.masa.malilib.gui.GuiBase;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort.GroupingType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerCategory;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerUtils.ContainerInfo;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.util.Identifier;

/**
 * SortButtonWidget
 */
public class GuiSortingButtons {

  public static final Identifier TEXTURE = new Identifier(ModInfo.MOD_ID, "textures/gui/gui_buttons.png");

  private static int base_x;
  private static int player_y;
  private static int chest_y;

  public static List<AbstractButtonWidget> gets(Screen screen, Container container, int left, int top, int containerWidth, int containerHeight) {
    List<AbstractButtonWidget> list = new ArrayList<>();
    base_x = left + containerWidth - 17 - 36;
    player_y = top + containerHeight - 95;
    chest_y = top + 5;
    ContainerCategory cate = ContainerCategory.of(container);
    if (!cate.isStorage()) {
      base_x += 12;
    }
    if (cate == ContainerCategory.PLAYER_CREATIVE) {
      base_x -= 18;
    }
    if (AdvancedOptions.INVENTORY_SHOW_SORT_BUTTONS.getBooleanValue()) {
      if (cate == ContainerCategory.SORTABLE_3x3
          || cate == ContainerCategory.SORTABLE_9xN
          || cate == ContainerCategory.SORTABLE_Nx3) {
        list.add(sortButton(true));
        list.add(sortColumnsButton(true));
        list.add(sortRowsButton(true));
      }
      if (cate == ContainerCategory.PLAYER_SURVIVAL || cate == ContainerCategory.PLAYER_CREATIVE) {
        list.add(sortButton(false));
        list.add(sortColumnsButton(false));
        list.add(sortRowsButton(false));
      }
    }
    if (AdvancedOptions.INVENTORY_SHOW_MOVE_ALL_BUTTONS.getBooleanValue() && cate.isStorage()) {
      list.add(moveAllButton(false));
      list.add(moveAllButton(true));
    }
    
    return list;
  }

  public static SortButtonWidget sortButton(boolean chestSide) { // chestSide or playerSide
    return new SortButtonWidget(base_x, chestSide ? chest_y : player_y, 1, 0, x->{
      ContainerInfo info = ContainerInfo.of(Current.container());
      VirtualSorterPort.doSort(!chestSide, info, SortingMethodProviders.DEFAULT, GroupingType.PRESERVED);
    }, "inventoryprofiles.config.comment.openConfigGui");
  }
  public static SortButtonWidget sortColumnsButton(boolean chestSide) { // chestSide or playerSide
    return new SortButtonWidget(base_x + 12, chestSide ? chest_y : player_y, 2, 0, x->{
      ContainerInfo info = ContainerInfo.of(Current.container());
      VirtualSorterPort.doSort(!chestSide, info, SortingMethodProviders.DEFAULT, GroupingType.COLUMNS);
    }, "");
  }
  public static SortButtonWidget sortRowsButton(boolean chestSide) { // chestSide or playerSide
    return new SortButtonWidget(base_x + 24, chestSide ? chest_y : player_y, 3, 0, x->{
      ContainerInfo info = ContainerInfo.of(Current.container());
      VirtualSorterPort.doSort(!chestSide, info, SortingMethodProviders.DEFAULT, GroupingType.ROWS);
    }, "");
  }

  public static SortButtonWidget moveAllButton(boolean chestSide) {
    return new SortButtonWidget(base_x + 36, chestSide ? chest_y : player_y, 
    chestSide ? 6 : 5, 0, x->{
      ContainerActions.moveAllAlike(chestSide, GuiBase.isShiftDown());
    }, "");
  }

  public static class SortButtonWidget extends TexturedButtonWidget {
    String tooltipText = "";
    public SortButtonWidget(int x, int y, int gx, int gy, PressAction pressAction, String tooltipText) {
      super(x, y, 10, 10, gx * 10, gy * 10, gy * 10 + 10, TEXTURE, pressAction);
      this.tooltipText = tooltipText;
    }

    @Override
    public void renderButton(int int_1, int int_2, float float_1) {
      super.renderButton(int_1, int_2, float_1);
      GlStateManager.pushMatrix();
      this.renderToolTip(int_1, int_2);
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
    }
    
    @Override
    public void renderToolTip(int x, int y) {
        if (AdvancedOptions.SHOW_INVENTORY_BUTTON_TOOLTIPS.getBooleanValue() && this.isHovered() && !tooltipText.isEmpty())
            Current.screen().renderTooltip(I18n.translate(tooltipText), x, y);
    }

  }
  
}