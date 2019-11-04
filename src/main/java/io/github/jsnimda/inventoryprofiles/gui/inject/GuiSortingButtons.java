package io.github.jsnimda.inventoryprofiles.gui.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.dy.masa.malilib.gui.GuiBase;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.gui.ToolTips;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorterPort.GroupingType;
import io.github.jsnimda.inventoryprofiles.sorter.predefined.SortingMethodProviders;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerCategory;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.FurnaceScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.screen.inventory.SmokerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

/**
 * SortButtonWidget
 */
public class GuiSortingButtons {

  public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.MOD_ID, "textures/gui/gui_buttons.png");
  public static final List<String> ROMAN_NUMBER = Arrays.asList("0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII");

  private static int left_base_x;
  private static int right_base_x;
  private static int player_y;
  private static int chest_y;
  private static ContainerCategory cate;

  public static List<Widget> gets(Screen screen, Container container, int left, int top, int containerWidth, int containerHeight) {
    List<Widget> list = new ArrayList<>();
    left_base_x = left - 10;
    right_base_x = left + containerWidth - 17 - 36;
    player_y = top + containerHeight - 95;
    chest_y = top + 5;
    cate = ContainerCategory.of(container);
    if (!showMoveAllButton(cate) || cate == ContainerCategory.PLAYER_SURVIVAL) { // move all button
      right_base_x += 12;
    }
    if (cate == ContainerCategory.PLAYER_CREATIVE) {
      right_base_x -= 18;
    }
    if (AdvancedOptions.INVENTORY_SHOW_PROFILE_BUTTONS.getBooleanValue()) {
      if (cate == ContainerCategory.PLAYER_SURVIVAL || cate == ContainerCategory.PLAYER_CREATIVE) {
        // list.add(profileButton(1));
        // list.add(profileButton(2));
        // list.add(profileButton(3));
        // list.add(profileButton(4));
        // list.add(profileButton(5));
        // list.add(profileButton(6));
        // list.add(profileButton(7));
        // list.add(profileButton(8));
        // list.add(profileButton(9));
        // list.add(profileButton(10));
        // list.add(profileButton(11));
        // list.add(profileButton(12));
      }
    }
    if (AdvancedOptions.INVENTORY_SHOW_SORT_BUTTONS.getBooleanValue()) {
      if (cate == ContainerCategory.SORTABLE_3x3
          || cate == ContainerCategory.SORTABLE_9xN
          || cate == ContainerCategory.SORTABLE_Nx3
          || cate == ContainerCategory.UNKNOWN) {
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
    if (AdvancedOptions.INVENTORY_SHOW_MOVE_ALL_BUTTONS.getBooleanValue() && showMoveAllButton(cate)) {
      list.add(moveAllButton(false));
      if (cate.isStorage()) {
        list.add(moveAllButton(true));
      }
    }
    
    return list;
  }

  public static boolean showMoveAllButton(ContainerCategory cate) {
    return cate.isStorage()
      || cate == ContainerCategory.CRAFTABLE_3x3
      || cate == ContainerCategory.PLAYER_SURVIVAL;
  }

  public static SortButtonWidget profileButton(int profileId){
    return new SortButtonWidget(left_base_x, player_y + 2 + 10*profileId, 6+profileId, 0, x->{

    }, "inventoryprofiles.tooltip.profile_" + ROMAN_NUMBER.get(profileId) + "_button");
  }
  public static SortButtonWidget sortButton(boolean chestSide) { // chestSide or playerSide
    return new SortButtonWidget(right_base_x, chestSide ? chest_y : player_y, 1, 0, x->{
      VirtualSorterPort.doSort(!chestSide, SortingMethodProviders.current(), GroupingType.PRESERVED);
    }, "inventoryprofiles.tooltip.sort_button");
  }
  public static SortButtonWidget sortColumnsButton(boolean chestSide) { // chestSide or playerSide
    return new SortButtonWidget(right_base_x + 12, chestSide ? chest_y : player_y, 2, 0, x->{
      VirtualSorterPort.doSort(!chestSide, SortingMethodProviders.current(), GroupingType.COLUMNS);
    }, "inventoryprofiles.tooltip.sort_columns_button");
  }
  public static SortButtonWidget sortRowsButton(boolean chestSide) { // chestSide or playerSide
    return new SortButtonWidget(right_base_x + 24, chestSide ? chest_y : player_y, 3, 0, x->{
      VirtualSorterPort.doSort(!chestSide, SortingMethodProviders.current(), GroupingType.ROWS);
    }, "inventoryprofiles.tooltip.sort_rows_button");
  }

  public static SortButtonWidget moveAllButton(boolean chestSide) {
    return new SortButtonWidget(right_base_x + 36 - (cate == ContainerCategory.PLAYER_SURVIVAL ? 12 : 0), chestSide ? chest_y : player_y - (cate == ContainerCategory.PLAYER_SURVIVAL ? 12 : 0), 
    chestSide ? 6 : 5, 0, x->{
      ContainerActions.moveAllAlike(chestSide, GuiBase.isShiftDown());
    }, "inventoryprofiles.tooltip.move_all_button");
  }

  public static boolean isRecipeBookOpen() {
    if (Current.screen() instanceof InventoryScreen || Current.screen() instanceof CraftingScreen) {
      return Current.recipeBook().isGuiOpen();
    }
    if (Current.screen() instanceof FurnaceScreen) {
      return Current.recipeBook().isFurnaceGuiOpen();
    }
    if (Current.screen() instanceof BlastFurnaceScreen) {
      return Current.recipeBook().func_216758_e(); //func_216758_e isBlastFurnaceGuiOpen
    }
    if (Current.screen() instanceof SmokerScreen) {
      return Current.recipeBook().func_216759_g(); //func_216759_g isSmokerGuiOpen
    }
    return false;
  }

  public static class SortButtonWidget extends ImageButton {
    String tooltipText = "";

    int originalX;
    public SortButtonWidget(int x, int y, int gx, int gy, IPressable pressAction, String tooltipText) {
      super(x, y, 10, 10, gx * 10, gy * 10, gy * 10 + 10, TEXTURE, pressAction);
      originalX = x;
      this.tooltipText = tooltipText;
    }

    @Override
    public void renderButton(int int_1, int int_2, float float_1) {
      this.x = originalX;
      // check if creative and if non inventory tab
      if (Current.screen() instanceof CreativeScreen) {
        if (((CreativeScreen)Current.screen()).getSelectedTabIndex() != ItemGroup.INVENTORY.getIndex()) {
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
      if (AdvancedOptions.SHOW_INVENTORY_BUTTON_TOOLTIPS.getBooleanValue() && this.isHovered() && !tooltipText.isEmpty())
        ToolTips.add(I18n.format(tooltipText), x, y);
    }

  }
  
}