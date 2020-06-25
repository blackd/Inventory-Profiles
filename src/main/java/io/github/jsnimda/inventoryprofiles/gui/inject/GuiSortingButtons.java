package io.github.jsnimda.inventoryprofiles.gui.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.GuiSettings;
import io.github.jsnimda.inventoryprofiles.gui.ToolTips;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort.GroupingType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerCategory;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

/**
 * SortButtonWidget
 */
public class GuiSortingButtons {

  public static final Identifier TEXTURE = new Identifier(ModInfo.MOD_ID, "textures/gui/gui_buttons.png");

  private Screen screen;
  private ScreenHandler container;
  private int left;
  private int top;
  private int containerWidth;
  private int containerHeight;
  
  public GuiSortingButtons(Screen screen, ScreenHandler container, int left, int top, int containerWidth,
      int containerHeight) {
    this.screen = screen;
    this.container = container;
    this.left = left;
    this.top = top;
    this.containerWidth = containerWidth;
    this.containerHeight = containerHeight;
  }

  List<AbstractButtonWidget> list = new ArrayList<>();
  private int right_base_x; // right most button x
  private int player_y; // player side button y
  private int chest_y; // chest side button y

  private ContainerCategory cate;

  public List<AbstractButtonWidget> gets() {
    right_base_x = left + containerWidth - 17;
    player_y = top + containerHeight - 95;
    chest_y = top + 5;
    cate = ContainerCategory.of(container);
    if (cate == ContainerCategory.PLAYER_CREATIVE) {
      right_base_x -= 18;
    }
    // if (AdvancedOptions.INVENTORY_SHOW_PROFILE_BUTTONS.getBooleanValue()) {
    //   if (cate == ContainerCategory.PLAYER_SURVIVAL || cate == ContainerCategory.PLAYER_CREATIVE) {
    //     // list.add(profileButton(1));
    //     // list.add(profileButton(2));
    //     // list.add(profileButton(3));
    //     // list.add(profileButton(4));
    //     // list.add(profileButton(5));
    //     // list.add(profileButton(6));
    //     // list.add(profileButton(7));
    //     // list.add(profileButton(8));
    //     // list.add(profileButton(9));
    //     // list.add(profileButton(10));
    //     // list.add(profileButton(11));
    //     // list.add(profileButton(12));
    //   }
    // }
    boolean addChestSide = cate == ContainerCategory.SORTABLE_3x3
        || cate == ContainerCategory.SORTABLE_9xN
        || cate == ContainerCategory.SORTABLE_Nx3
        || cate == ContainerCategory.UNKNOWN;
    boolean addNonChestSide = cate == ContainerCategory.PLAYER_SURVIVAL || cate == ContainerCategory.PLAYER_CREATIVE;
    boolean shouldAdd = addChestSide || addNonChestSide;
    int x0 = right_base_x;
    if (GuiSettings.SHOW_MOVE_ALL_BUTTON.getBooleanValue() && showMoveAllButton(cate)) {
      list.add(moveAllButton(false, x0));
      if (cate.isStorage()) {
        list.add(moveAllButton(true, x0));
      }
      if (cate != ContainerCategory.PLAYER_SURVIVAL) {
        x0 -= 12;
      }
    }
    if (shouldAdd) {
      if (GuiSettings.SHOW_SORT_IN_ROWS_BUTTON.getBooleanValue()) {
        if (addChestSide)    list.add(sortRowsButton(true, x0));
        if (addNonChestSide) list.add(sortRowsButton(false, x0));
        x0 -= 12;
      }
      if (GuiSettings.SHOW_SORT_IN_COLUMNS_BUTTON.getBooleanValue()) {
        if (addChestSide)    list.add(sortColumnsButton(true, x0));
        if (addNonChestSide) list.add(sortColumnsButton(false, x0));
        x0 -= 12;
      }
      if (GuiSettings.SHOW_SORT_BUTTON.getBooleanValue()) {
        if (addChestSide)    list.add(sortButton(true, x0));
        if (addNonChestSide) list.add(sortButton(false, x0));
        x0 -= 12;
      }
    }
    
    return list;
  }

  public static List<AbstractButtonWidget> gets(Screen screen, ScreenHandler container, int left, int top, int containerWidth, int containerHeight) {
    return new GuiSortingButtons(screen, container, left, top, containerWidth, containerHeight).gets();
  }

  private static boolean showMoveAllButton(ContainerCategory cate) {
    return cate.isStorage()
      || cate == ContainerCategory.CRAFTABLE_3x3
      || cate == ContainerCategory.PLAYER_SURVIVAL;
  }

  private SortButtonWidget sortButton(boolean chestSide, int buttonX) { // chestSide or playerSide
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y, 1, 0, x->{
      VirtualSorterPort.doSort(!chestSide, SortingMethodProviders.current(), GroupingType.PRESERVED);
    }, "inventoryprofiles.tooltip.sort_button");
  }
  private SortButtonWidget sortColumnsButton(boolean chestSide, int buttonX) { // chestSide or playerSide
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y, 2, 0, x->{
      VirtualSorterPort.doSort(!chestSide, SortingMethodProviders.current(), GroupingType.COLUMNS);
    }, "inventoryprofiles.tooltip.sort_columns_button");
  }
  private SortButtonWidget sortRowsButton(boolean chestSide, int buttonX) { // chestSide or playerSide
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y, 3, 0, x->{
      VirtualSorterPort.doSort(!chestSide, SortingMethodProviders.current(), GroupingType.ROWS);
    }, "inventoryprofiles.tooltip.sort_rows_button");
  }

  private SortButtonWidget moveAllButton(boolean chestSide, int buttonX) {
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y - (cate == ContainerCategory.PLAYER_SURVIVAL ? 12 : 0), 
    chestSide ? 6 : 5, 0, x->{
      ContainerActions.moveAllAlike(chestSide, Screen.hasShiftDown());
    }, "inventoryprofiles.tooltip.move_all_button");
  }

  private static boolean isRecipeBookOpen() {
    if (Current.screen() instanceof InventoryScreen || Current.screen() instanceof CraftingScreen) {
      return Current.recipeBook().isGuiOpen();
    }
    if (Current.screen() instanceof FurnaceScreen) {
      return Current.recipeBook().isFurnaceGuiOpen();
    }
    if (Current.screen() instanceof BlastFurnaceScreen) {
      return Current.recipeBook().isBlastFurnaceGuiOpen();
    }
    if (Current.screen() instanceof SmokerScreen) {
      return Current.recipeBook().isSmokerGuiOpen();
    }
    return false;
  }

  public static class SortButtonWidget extends TexturedButtonWidget {
    String tooltipText = "";

    int originalX;
    public SortButtonWidget(int x, int y, int gx, int gy, PressAction pressAction, String tooltipText) {
      super(x, y, 10, 10, gx * 10, gy * 10, gy * 10 + 10, TEXTURE, pressAction);
      originalX = x;
      this.tooltipText = tooltipText;
    }

    @Override
    public void renderButton(int int_1, int int_2, float float_1) {
      this.x = originalX;
      // check if creative and if non inventory tab
      if (Current.screen() instanceof CreativeInventoryScreen) {
        if (((CreativeInventoryScreen)Current.screen()).getSelectedTab() != ItemGroup.INVENTORY.getIndex()) {
          //
          // should i use this.x = -999 or this.active = false + return?
          //
          this.x = -20; // temporary solution
        }
      }
      // recipeBook position fix
      boolean isNarrow = Current.screen().width < 379; // hardcoded, maybe lookup to protected isNarrow field (?)
      if (isRecipeBookOpen() && !isNarrow) {
        this.x = this.originalX + 177 - 200 / 2; // from RecipeBookWidget.findLeftEdge
      }
      
      super.renderButton(int_1, int_2, float_1);
      this.renderToolTip(int_1, int_2);
    }
    
    @Override
    public void renderToolTip(int x, int y) {
      if (GuiSettings.SHOW_BUTTON_TOOLTIPS.getBooleanValue() && this.isHovered() && !tooltipText.isEmpty())
        ToolTips.add(I18n.translate(tooltipText), x, y);
    }

  }

  
}