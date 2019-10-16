package io.github.jsnimda.inventoryprofiles.gui.inject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.apache.commons.io.IOUtils;

import io.github.jsnimda.inventoryprofiles.ModInfo;
import io.github.jsnimda.inventoryprofiles.config.Configs.AdvancedOptions;
import io.github.jsnimda.inventoryprofiles.sorter.util.ContainerActions;
import io.github.jsnimda.inventoryprofiles.sorter.util.Current;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.container.SlotActionType;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;

/**
 * SortButtonWidget
 */
public class SortButtonWidget extends TexturedButtonWidget {

  public enum Type {
    MOVE_ALL_BOTTOM_TO_TOP, MOVE_ALL_TOP_TO_BOTTOM,
  }

  public static void onPress(ButtonWidget var1) {
    System.out.println(Current.cursorStack());
    ContainerActions.restockHotbar();
    /*
    StringReader rr = new StringReader("diamond_axe{                 Enchantments:[{id:\"minecraft:smite\",lvl:5}]} diamond_axe");
    System.out.println(rr.getCursor());
    System.out.println(rr.getRemaining());
    System.out.println(rr.getString());
    ItemStringReader item = new ItemStringReader(rr
        , true);
    try {
      item.consume();
      System.out.println(item.getItem());
      System.out.println(item.getTag());
      System.out.println(rr.getCursor());
      System.out.println(rr.getRemaining());
      System.out.println(rr.getString());
    } catch (CommandSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      System.out.println("equal? " + TagHelper.areTagsEqual(
        StringNbtReader.parse("{hi: [{bye: 1}]}"), 
        StringNbtReader.parse("{hi: [{}]}"), 
        true));
    } catch (CommandSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
  };

  public static final Identifier TEXTURE = new Identifier(ModInfo.MOD_ID, "textures/gui/gui_buttons.png");
  public static List<AbstractButtonWidget> getButtons(Screen screen, int left, int top, int containerWidth, int containerHeight) {
    List<AbstractButtonWidget> list = new ArrayList<>();
    int x = left + containerWidth - 17;
    int y = top + containerHeight - 95;
    if (AdvancedOptions.INVENTORY_SHOW_MOVE_ALL_BUTTONS.getBooleanValue()) {
      list.add(new SortButtonWidget(x, y, 5, 0));
    }
    
    return list;
  }

  public SortButtonWidget(int x, int y, int gx, int gy) {
    super(x, y, 10, 10, gx * 10, gy * 10, gy * 10 + 10, TEXTURE, SortButtonWidget::onPress);
  }

  
}