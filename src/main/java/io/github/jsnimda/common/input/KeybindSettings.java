package io.github.jsnimda.common.input;

import static io.github.jsnimda.common.input.KeybindSettings.Context.*;
import static io.github.jsnimda.common.input.KeybindSettings.FlagConstants.*;
import static io.github.jsnimda.common.input.KeybindSettings.KeyAction.*;

import net.minecraft.client.resource.language.I18n;

public class KeybindSettings {

  public static final KeybindSettings INGAME_DEFAULT = new KeybindSettings(INGAME, PRESS, NO_EXTRA | IN_ORDER);
  public static final KeybindSettings GUI_DEFAULT    = new KeybindSettings(   GUI, PRESS, NO_EXTRA | IN_ORDER);
  public static final KeybindSettings ANY_DEFAULT    = new KeybindSettings(   ANY, PRESS, NO_EXTRA | IN_ORDER);

  public enum KeyAction {
    PRESS,
    RELEASE,
    BOTH;
    @Override
    public String toString() {
      return I18n.translate("inventoryprofiles.common.enum.key_action." + name().toLowerCase());
    }
  }
  public enum Context {
    INGAME,
    GUI,
    ANY;
    @Override
    public String toString() {
      return I18n.translate("inventoryprofiles.common.enum.context." + name().toLowerCase());
    }
  }

  public class FlagConstants {
    public static final long NO_EXTRA = 0;
    public static final long ALLOW_EXTRA = 1;
    public static final long NO_ORDER = 0;
    public static final long IN_ORDER = 1 << 1;
  }

  public final Context context;
  public final KeyAction activateOn;
  public final boolean allowExtraKeys;
  public final boolean orderSensitive;

  public KeybindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive) {
    this.context = context;
    this.activateOn = activateOn;
    this.allowExtraKeys = allowExtraKeys;
    this.orderSensitive = orderSensitive;
  }

  public KeybindSettings(Context context, KeyAction activateOn, long flags) {
    this.context = context;
    this.activateOn = activateOn;
    this.allowExtraKeys = (flags & ALLOW_EXTRA) != 0;
    this.orderSensitive = (flags & IN_ORDER) != 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((activateOn == null) ? 0 : activateOn.hashCode());
    result = prime * result + (allowExtraKeys ? 1231 : 1237);
    result = prime * result + ((context == null) ? 0 : context.hashCode());
    result = prime * result + (orderSensitive ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    KeybindSettings other = (KeybindSettings) obj;
    if (activateOn != other.activateOn)
      return false;
    if (allowExtraKeys != other.allowExtraKeys)
      return false;
    if (context != other.context)
      return false;
    if (orderSensitive != other.orderSensitive)
      return false;
    return true;
  }

}