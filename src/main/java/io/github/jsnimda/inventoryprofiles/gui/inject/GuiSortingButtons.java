package io.github.jsnimda.inventoryprofiles.gui.inject;

import io.github.jsnimda.common.gui.Tooltips;
import io.github.jsnimda.common.vanilla.Vanilla;
import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.GuiSettings;
import io.github.jsnimda.inventoryprofiles.inventory.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SortButtonWidget
 */
public class GuiSortingButtons {

  public static final Identifier TEXTURE = new Identifier(ModInfo.MOD_ID, "textures/gui/gui_buttons.png");

  private Screen screen;
  private Container container;
  private int left;
  private int top;
  private int containerWidth;
  private int containerHeight;

  public GuiSortingButtons(Screen screen, Container container, int left, int top, int containerWidth,
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

  private Set<ContainerType> types;

  public List<AbstractButtonWidget> gets() {
    right_base_x = left + containerWidth - 17;
    player_y = top + containerHeight - 95;
    chest_y = top + 5;
    types = ContainerTypes.INSTANCE.getTypes(container);
    if (types.contains(VanillaContainerType.CREATIVE)) {
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
    boolean addChestSide = types.contains(VanillaContainerType.ANY_ITEM_STORAGE);
    boolean addNonChestSide = types.contains(VanillaContainerType.PLAYER_INVENTORY);
    boolean shouldAdd = addChestSide || addNonChestSide;
    int x0 = right_base_x;
    if (GuiSettings.INSTANCE.getSHOW_MOVE_ALL_BUTTON().getBooleanValue() && showMoveAllButton(types)) {
      list.add(moveAllButton(false, x0));
      if (types.contains(VanillaContainerType.ANY_ITEM_STORAGE)||
          types.contains(VanillaContainerType.NO_SORTING_STORAGE)) {
        list.add(moveAllButton(true, x0));
      }
      if (!types.contains(VanillaContainerType.PLAYER)) {
        x0 -= 12;
      }
    }
    if (shouldAdd) {
      if (GuiSettings.INSTANCE.getSHOW_SORT_IN_ROWS_BUTTON().getBooleanValue()) {
        if (addChestSide)    list.add(sortRowsButton(true, x0));
        if (addNonChestSide) list.add(sortRowsButton(false, x0));
        x0 -= 12;
      }
      if (GuiSettings.INSTANCE.getSHOW_SORT_IN_COLUMNS_BUTTON().getBooleanValue()) {
        if (addChestSide)    list.add(sortColumnsButton(true, x0));
        if (addNonChestSide) list.add(sortColumnsButton(false, x0));
        x0 -= 12;
      }
      if (GuiSettings.INSTANCE.getSHOW_SORT_BUTTON().getBooleanValue()) {
        if (addChestSide)    list.add(sortButton(true, x0));
        if (addNonChestSide) list.add(sortButton(false, x0));
        x0 -= 12;
      }
    }

    return list;
  }

  public static List<AbstractButtonWidget> gets(Screen screen, Container container, int left, int top, int containerWidth, int containerHeight) {
    return new GuiSortingButtons(screen, container, left, top, containerWidth, containerHeight).gets();
  }

  private static boolean showMoveAllButton(Set<ContainerType> types) {
    return types.contains(VanillaContainerType.ANY_ITEM_STORAGE)||
        types.contains(VanillaContainerType.NO_SORTING_STORAGE)||
        types.contains(VanillaContainerType.CRAFTING);
  }

  private SortButtonWidget sortButton(boolean chestSide, int buttonX) { // chestSide or playerSide
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y, 1, 0, x->{
      try {
        GeneralInventoryActions.INSTANCE.doSort();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }, "inventoryprofiles.tooltip.sort_button");
  }
  private SortButtonWidget sortColumnsButton(boolean chestSide, int buttonX) { // chestSide or playerSide
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y, 2, 0, x->{
      try {
        GeneralInventoryActions.INSTANCE.doSortInColumns();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }, "inventoryprofiles.tooltip.sort_columns_button");
  }
  private SortButtonWidget sortRowsButton(boolean chestSide, int buttonX) { // chestSide or playerSide
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y, 3, 0, x->{
      try {
        GeneralInventoryActions.INSTANCE.doSortInRows();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }, "inventoryprofiles.tooltip.sort_rows_button");
  }

  private SortButtonWidget moveAllButton(boolean chestSide, int buttonX) {
    return new SortButtonWidget(buttonX, chestSide ? chest_y : player_y - (types.contains(VanillaContainerType.PLAYER) ? 12 : 0),
    chestSide ? 6 : 5, 0, x->{
      try {
        GeneralInventoryActions.INSTANCE.doMoveMatch(chestSide);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }, "inventoryprofiles.tooltip.move_all_button");
  }

  private static boolean isRecipeBookOpen() {
    if (Vanilla.INSTANCE.screen() instanceof InventoryScreen || Vanilla.INSTANCE.screen() instanceof CraftingTableScreen) {
      return Vanilla.INSTANCE.recipeBook().isGuiOpen();
    }
    if (Vanilla.INSTANCE.screen() instanceof FurnaceScreen) {
      return Vanilla.INSTANCE.recipeBook().isFurnaceGuiOpen();
    }
    if (Vanilla.INSTANCE.screen() instanceof BlastFurnaceScreen) {
      return Vanilla.INSTANCE.recipeBook().isBlastFurnaceGuiOpen();
    }
    if (Vanilla.INSTANCE.screen() instanceof SmokerScreen) {
      return Vanilla.INSTANCE.recipeBook().isSmokerGuiOpen();
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
      if (Vanilla.INSTANCE.screen() instanceof CreativeInventoryScreen) {
        if (((CreativeInventoryScreen)Vanilla.INSTANCE.screen()).method_2469() != ItemGroup.INVENTORY.getIndex()) {
          //
          // should i use this.x = -999 or this.active = false + return?
          //
          this.x = -20; // temporary solution
        }
      }
      // recipeBook position fix
      boolean isNarrow = Vanilla.INSTANCE.screen().width < 379; // hardcoded, maybe lookup to protected isNarrow field (?)
      if (isRecipeBookOpen() && !isNarrow) {
        this.x = this.originalX + 177 - 200 / 2; // from RecipeBookWidget.findLeftEdge
      }

      super.renderButton(int_1, int_2, float_1);
      this.renderToolTip(int_1, int_2);
    }

    @Override
    public void renderToolTip(int x, int y) {
      if (GuiSettings.INSTANCE.getSHOW_BUTTON_TOOLTIPS().getBooleanValue() && this.isHovered() && !tooltipText.isEmpty())
        Tooltips.INSTANCE.addTooltip(I18n.translate(tooltipText), x, y);
    }

  }


}