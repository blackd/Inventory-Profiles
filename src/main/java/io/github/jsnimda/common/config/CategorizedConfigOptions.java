package io.github.jsnimda.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class CategorizedConfigOptions extends ConfigOptionBase implements IConfigElementResettableMultiple {

  private List<Pair<String, List<IConfigOption>>> categories = new ArrayList<>();
  private List<IConfigOption> currentCategory = null;
  private Map<String, IConfigOption> configOptionsMap = new HashMap<>();

  public void addCategory(String categoryName) {
    currentCategory = new ArrayList<>();
    categories.add(Pair.of(categoryName, currentCategory));
  }

  public void addConfigOption(IConfigOption configOption) {
    if (currentCategory == null) {
      addCategory("");
    }
    currentCategory.add(configOption);
    configOptionsMap.put(configOption.getKey(), configOption);
  }

  public List<Pair<String, List<IConfigOption>>> getCategories() {
    return categories;
  }

  @Override
  public Map<String, ? extends IConfigOption> getConfigOptionsMap() {
    return configOptionsMap;
  }

}