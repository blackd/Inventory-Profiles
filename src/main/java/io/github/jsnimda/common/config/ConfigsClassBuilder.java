package io.github.jsnimda.common.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.CaseFormat;

import io.github.jsnimda.common.config.options.ConfigBoolean;
import io.github.jsnimda.common.config.options.ConfigEnum;
import io.github.jsnimda.common.config.options.ConfigHotkey;
import io.github.jsnimda.common.config.options.ConfigHotkeyedBoolean;
import io.github.jsnimda.common.config.options.ConfigInteger;

public class ConfigsClassBuilder {

  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Category {
    public String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  public static @interface ConfigOptionsClass {
  }

  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Exclude {
  }

  public static ConfigBoolean bool(boolean defaultValue) {
    return new ConfigBoolean(defaultValue);
  }

  public static ConfigInteger integer(int defaultValue, int minValue, int maxValue) {
    return new ConfigInteger(defaultValue, minValue, maxValue);
  }

  public static <T extends Enum<T>> ConfigEnum<T> enumList(T defaultValue) {
    return new ConfigEnum<T>(defaultValue);
  }

  public static ConfigHotkey hotkey(String defaultValue) {
    return null;
  }

  public static ConfigHotkeyedBoolean hotkeyedBool(boolean defaultValue) {
    return null;
  }

  public static CategorizedConfigOptions load(Class<?> configOptionsClass) {
    CategorizedConfigOptions cates = new CategorizedConfigOptions();
    cates.setKey(configOptionsClass.getSimpleName());
    Field[] fields = configOptionsClass.getDeclaredFields(); // assumed in declaration order
    for (Field field : fields) {
      int mod = field.getModifiers();
      if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)) {
        try {
          Object fieldValue = field.get(null);
          if (fieldValue instanceof IConfigOption && field.getAnnotation(Exclude.class) == null) {
            Category categoryAnnotation = field.getAnnotation(Category.class);
            if (categoryAnnotation != null) {
              cates.addCategory(categoryAnnotation.value());
            }

            IConfigOption configOption = (IConfigOption) fieldValue;
            configOption.setKey(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
            cates.addConfigOption(configOption);
          }
        } catch (IllegalAccessException e) {
          // TODO Auto-generated catch block
          e.printStackTrace(); // TODO catch this
        }
      }
    }
    return cates;
  }

  public static CategorizedConfigOptions loadWithNested(Class<?> configOptionsOuterClass, boolean isDeep) {
    CategorizedConfigOptions cates = new CategorizedConfigOptions();
    Class<?>[] classes = configOptionsOuterClass.getDeclaredClasses();
    for (Class<?> clazz : classes) {
      int mod = clazz.getModifiers();
      if (Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
        if (clazz.getAnnotation(ConfigOptionsClass.class) != null) {
          cates.addConfigOption(isDeep ? loadWithNested(clazz, true) : load(clazz));
        }
      }
    }
    return cates;
  }

}