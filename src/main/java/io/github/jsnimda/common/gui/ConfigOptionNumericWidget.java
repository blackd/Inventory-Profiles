package io.github.jsnimda.common.gui;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class ConfigOptionNumericWidget extends ConfigOptionWidgetBase<IConfigOptionPrimitiveNumeric<?>> {
  public static Identifier WIDGETS_TEXTURE = new Identifier("inventoryprofiles", "textures/gui/widgets.png");

  private SliderWidget slider = new SliderWidget(10, 10, 200, 20, VHLine.EMPTY_TEXT, 0.5) {

    @Override
    protected void updateMessage() {
    }
  
    @Override
    protected void applyValue() {
      double min = configOption.getMinValue().doubleValue();
      double max = configOption.getMaxValue().doubleValue();
      double val = (max - min) * value + min;
      configOption.setValue(val);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
      slider.x = ConfigOptionNumericWidget.this.x;
      slider.y = ConfigOptionNumericWidget.this.y;
      slider.setWidth(availableWidth - 16 - 2);
      slider.setMessage(new LiteralText(String.valueOf(configOption.getValue())));
      double min = configOption.getMinValue().doubleValue();
      double max = configOption.getMaxValue().doubleValue();
      double val = configOption.getValue().doubleValue();
      value = (val - min) / (max - min);
      super.render(matrices, mouseX, mouseY, partialTicks);
    }

  };
  private boolean useSlider = true;
  private TextFieldWidget textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 10, 10, 200, 18, VHLine.EMPTY_TEXT);

  private ButtonWidget toggleButton = new ButtonWidget(10, 10, 16, 16, VHLine.EMPTY_TEXT, x -> useSlider = !useSlider) {
    @Override
    public void renderButton(MatrixStack matrices, int i, int j, float f) { // ref: TexturedButtonWidget
       MinecraftClient minecraftClient = MinecraftClient.getInstance();
       minecraftClient.getTextureManager().bindTexture(WIDGETS_TEXTURE);
       GlStateManager.disableDepthTest();
       int textureX = this.isHovered() ? 32 : 16;
       int textureY = useSlider ? 16 : 0;
 
       drawTexture(matrices, this.x, this.y, textureX, textureY, this.width, this.height, 256, 256);
       GlStateManager.enableDepthTest();
    }
  };
  
  private static final Pattern PATTERN_INTEGER = Pattern.compile("-?[0-9]*");
  private static final Pattern PATTERN_DOUBLE = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?)?");

  private Pattern pattern;

  protected ConfigOptionNumericWidget(IConfigOptionPrimitiveNumeric<?> configOption) {
    super(configOption);
    pattern = configOption.getDefaultValue() instanceof Double ? PATTERN_DOUBLE : PATTERN_INTEGER;
    textField.setTextPredicate(x -> x.isEmpty() || pattern.matcher(x).matches()); // ref: malilib GuiTextFieldDouble
    textField.setChangedListener(x -> {
      if (textField.isActive()) { // method_20315
        // try set config value to text
        if (textField.getText().isEmpty()) {
          configOption.setValue(0);
        } else try {
          double d = Double.parseDouble(textField.getText());
          configOption.setValue(d);
        } catch (NumberFormatException e) {
        };
      }
    });
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    super.render(matrices, mouseX, mouseY, partialTicks);
    toggleButton.x = x + availableWidth - 16;
    toggleButton.y = y + (20 - 16) / 2;
    toggleButton.render(matrices, mouseX, mouseY, partialTicks);
    textField.x = x + 2;
    textField.y = y + 1;
    textField.setWidth(availableWidth - 16 - 2 - 4);
    if (!textField.isActive() && !useSlider) { // is editing // method_20315
      textField.setText(String.valueOf(configOption.getValue()));
    }
    (useSlider ? slider : textField).render(matrices, mouseX, mouseY, partialTicks);
  }

  @Override
  public List<? extends Element> children() {
    return useSlider ? Arrays.asList(resetButton, slider, toggleButton) : Arrays.asList(resetButton, textField, toggleButton);
  }

}