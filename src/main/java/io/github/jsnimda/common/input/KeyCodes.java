package io.github.jsnimda.common.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

public class KeyCodes {

  public static final int
    KEY_UNKNOWN       = GLFW.GLFW_KEY_UNKNOWN,
    KEY_SPACE         = GLFW.GLFW_KEY_SPACE,
    KEY_APOSTROPHE    = GLFW.GLFW_KEY_APOSTROPHE,
    KEY_COMMA         = GLFW.GLFW_KEY_COMMA,
    KEY_MINUS         = GLFW.GLFW_KEY_MINUS,
    KEY_PERIOD        = GLFW.GLFW_KEY_PERIOD,
    KEY_SLASH         = GLFW.GLFW_KEY_SLASH,
    KEY_0             = GLFW.GLFW_KEY_0,
    KEY_1             = GLFW.GLFW_KEY_1,
    KEY_2             = GLFW.GLFW_KEY_2,
    KEY_3             = GLFW.GLFW_KEY_3,
    KEY_4             = GLFW.GLFW_KEY_4,
    KEY_5             = GLFW.GLFW_KEY_5,
    KEY_6             = GLFW.GLFW_KEY_6,
    KEY_7             = GLFW.GLFW_KEY_7,
    KEY_8             = GLFW.GLFW_KEY_8,
    KEY_9             = GLFW.GLFW_KEY_9,
    KEY_SEMICOLON     = GLFW.GLFW_KEY_SEMICOLON,
    KEY_EQUAL         = GLFW.GLFW_KEY_EQUAL,
    KEY_A             = GLFW.GLFW_KEY_A,
    KEY_B             = GLFW.GLFW_KEY_B,
    KEY_C             = GLFW.GLFW_KEY_C,
    KEY_D             = GLFW.GLFW_KEY_D,
    KEY_E             = GLFW.GLFW_KEY_E,
    KEY_F             = GLFW.GLFW_KEY_F,
    KEY_G             = GLFW.GLFW_KEY_G,
    KEY_H             = GLFW.GLFW_KEY_H,
    KEY_I             = GLFW.GLFW_KEY_I,
    KEY_J             = GLFW.GLFW_KEY_J,
    KEY_K             = GLFW.GLFW_KEY_K,
    KEY_L             = GLFW.GLFW_KEY_L,
    KEY_M             = GLFW.GLFW_KEY_M,
    KEY_N             = GLFW.GLFW_KEY_N,
    KEY_O             = GLFW.GLFW_KEY_O,
    KEY_P             = GLFW.GLFW_KEY_P,
    KEY_Q             = GLFW.GLFW_KEY_Q,
    KEY_R             = GLFW.GLFW_KEY_R,
    KEY_S             = GLFW.GLFW_KEY_S,
    KEY_T             = GLFW.GLFW_KEY_T,
    KEY_U             = GLFW.GLFW_KEY_U,
    KEY_V             = GLFW.GLFW_KEY_V,
    KEY_W             = GLFW.GLFW_KEY_W,
    KEY_X             = GLFW.GLFW_KEY_X,
    KEY_Y             = GLFW.GLFW_KEY_Y,
    KEY_Z             = GLFW.GLFW_KEY_Z,
    KEY_LEFT_BRACKET  = GLFW.GLFW_KEY_LEFT_BRACKET,
    KEY_BACKSLASH     = GLFW.GLFW_KEY_BACKSLASH,
    KEY_RIGHT_BRACKET = GLFW.GLFW_KEY_RIGHT_BRACKET,
    KEY_GRAVE_ACCENT  = GLFW.GLFW_KEY_GRAVE_ACCENT,
    KEY_WORLD_1       = GLFW.GLFW_KEY_WORLD_1,
    KEY_WORLD_2       = GLFW.GLFW_KEY_WORLD_2,
    KEY_ESCAPE        = GLFW.GLFW_KEY_ESCAPE,
    KEY_ENTER         = GLFW.GLFW_KEY_ENTER,
    KEY_TAB           = GLFW.GLFW_KEY_TAB,
    KEY_BACKSPACE     = GLFW.GLFW_KEY_BACKSPACE,
    KEY_INSERT        = GLFW.GLFW_KEY_INSERT,
    KEY_DELETE        = GLFW.GLFW_KEY_DELETE,
    KEY_RIGHT         = GLFW.GLFW_KEY_RIGHT,
    KEY_LEFT          = GLFW.GLFW_KEY_LEFT,
    KEY_DOWN          = GLFW.GLFW_KEY_DOWN,
    KEY_UP            = GLFW.GLFW_KEY_UP,
    KEY_PAGE_UP       = GLFW.GLFW_KEY_PAGE_UP,
    KEY_PAGE_DOWN     = GLFW.GLFW_KEY_PAGE_DOWN,
    KEY_HOME          = GLFW.GLFW_KEY_HOME,
    KEY_END           = GLFW.GLFW_KEY_END,
    KEY_CAPS_LOCK     = GLFW.GLFW_KEY_CAPS_LOCK,
    KEY_SCROLL_LOCK   = GLFW.GLFW_KEY_SCROLL_LOCK,
    KEY_NUM_LOCK      = GLFW.GLFW_KEY_NUM_LOCK,
    KEY_PRINT_SCREEN  = GLFW.GLFW_KEY_PRINT_SCREEN,
    KEY_PAUSE         = GLFW.GLFW_KEY_PAUSE,
    KEY_F1            = GLFW.GLFW_KEY_F1,
    KEY_F2            = GLFW.GLFW_KEY_F2,
    KEY_F3            = GLFW.GLFW_KEY_F3,
    KEY_F4            = GLFW.GLFW_KEY_F4,
    KEY_F5            = GLFW.GLFW_KEY_F5,
    KEY_F6            = GLFW.GLFW_KEY_F6,
    KEY_F7            = GLFW.GLFW_KEY_F7,
    KEY_F8            = GLFW.GLFW_KEY_F8,
    KEY_F9            = GLFW.GLFW_KEY_F9,
    KEY_F10           = GLFW.GLFW_KEY_F10,
    KEY_F11           = GLFW.GLFW_KEY_F11,
    KEY_F12           = GLFW.GLFW_KEY_F12,
    KEY_F13           = GLFW.GLFW_KEY_F13,
    KEY_F14           = GLFW.GLFW_KEY_F14,
    KEY_F15           = GLFW.GLFW_KEY_F15,
    KEY_F16           = GLFW.GLFW_KEY_F16,
    KEY_F17           = GLFW.GLFW_KEY_F17,
    KEY_F18           = GLFW.GLFW_KEY_F18,
    KEY_F19           = GLFW.GLFW_KEY_F19,
    KEY_F20           = GLFW.GLFW_KEY_F20,
    KEY_F21           = GLFW.GLFW_KEY_F21,
    KEY_F22           = GLFW.GLFW_KEY_F22,
    KEY_F23           = GLFW.GLFW_KEY_F23,
    KEY_F24           = GLFW.GLFW_KEY_F24,
    KEY_F25           = GLFW.GLFW_KEY_F25,
    KEY_KP_0          = GLFW.GLFW_KEY_KP_0,
    KEY_KP_1          = GLFW.GLFW_KEY_KP_1,
    KEY_KP_2          = GLFW.GLFW_KEY_KP_2,
    KEY_KP_3          = GLFW.GLFW_KEY_KP_3,
    KEY_KP_4          = GLFW.GLFW_KEY_KP_4,
    KEY_KP_5          = GLFW.GLFW_KEY_KP_5,
    KEY_KP_6          = GLFW.GLFW_KEY_KP_6,
    KEY_KP_7          = GLFW.GLFW_KEY_KP_7,
    KEY_KP_8          = GLFW.GLFW_KEY_KP_8,
    KEY_KP_9          = GLFW.GLFW_KEY_KP_9,
    KEY_KP_DECIMAL    = GLFW.GLFW_KEY_KP_DECIMAL,
    KEY_KP_DIVIDE     = GLFW.GLFW_KEY_KP_DIVIDE,
    KEY_KP_MULTIPLY   = GLFW.GLFW_KEY_KP_MULTIPLY,
    KEY_KP_SUBTRACT   = GLFW.GLFW_KEY_KP_SUBTRACT,
    KEY_KP_ADD        = GLFW.GLFW_KEY_KP_ADD,
    KEY_KP_ENTER      = GLFW.GLFW_KEY_KP_ENTER,
    KEY_KP_EQUAL      = GLFW.GLFW_KEY_KP_EQUAL,
    KEY_LEFT_SHIFT    = GLFW.GLFW_KEY_LEFT_SHIFT,
    KEY_LEFT_CONTROL  = GLFW.GLFW_KEY_LEFT_CONTROL,
    KEY_LEFT_ALT      = GLFW.GLFW_KEY_LEFT_ALT,
    KEY_LEFT_SUPER    = GLFW.GLFW_KEY_LEFT_SUPER,
    KEY_RIGHT_SHIFT   = GLFW.GLFW_KEY_RIGHT_SHIFT,
    KEY_RIGHT_CONTROL = GLFW.GLFW_KEY_RIGHT_CONTROL,
    KEY_RIGHT_ALT     = GLFW.GLFW_KEY_RIGHT_ALT,
    KEY_RIGHT_SUPER   = GLFW.GLFW_KEY_RIGHT_SUPER,
    KEY_MENU          = GLFW.GLFW_KEY_MENU,
    KEY_LAST          = GLFW.GLFW_KEY_LAST;

  public static final int
    MOUSE_BUTTON_1      = GLFW.GLFW_MOUSE_BUTTON_1 - 100,
    MOUSE_BUTTON_2      = GLFW.GLFW_MOUSE_BUTTON_2 - 100,
    MOUSE_BUTTON_3      = GLFW.GLFW_MOUSE_BUTTON_3 - 100,
    MOUSE_BUTTON_4      = GLFW.GLFW_MOUSE_BUTTON_4 - 100,
    MOUSE_BUTTON_5      = GLFW.GLFW_MOUSE_BUTTON_5 - 100,
    MOUSE_BUTTON_6      = GLFW.GLFW_MOUSE_BUTTON_6 - 100,
    MOUSE_BUTTON_7      = GLFW.GLFW_MOUSE_BUTTON_7 - 100,
    MOUSE_BUTTON_8      = GLFW.GLFW_MOUSE_BUTTON_8 - 100;

  private static final Map<Integer, String> KEY_TO_NAME_MAP = new HashMap<>();
  private static final Map<String, Integer> NAME_TO_KEY_MAP = new HashMap<>();
  private static final Map<String, String> NAME_TO_DISPLAY_TEXT_MAP = new HashMap<>();

  private static void mapKeyName(String name, String displayText, int key) {
    if (!KEY_TO_NAME_MAP.containsKey(key)) {
      KEY_TO_NAME_MAP.put(key, name);
    }
    if (!NAME_TO_KEY_MAP.containsKey(name)) {
      NAME_TO_KEY_MAP.put(name, key);
    }
    if (!NAME_TO_DISPLAY_TEXT_MAP.containsKey(name)) {
      NAME_TO_DISPLAY_TEXT_MAP.put(name, (displayText == null || displayText.isEmpty()) ? name : displayText);
    }
  }

  public static String getKeyName(int key) {
    return KEY_TO_NAME_MAP.containsKey(key) ? KEY_TO_NAME_MAP.get(key) : "keycode " + key;
  }

  public static int getKeyFromName(String name) {
    return NAME_TO_KEY_MAP.containsKey(name) ? NAME_TO_KEY_MAP.get(name) : -1;
  }

  public static String getFriendlyName(String name) {
    return NAME_TO_DISPLAY_TEXT_MAP.containsKey(name) ? NAME_TO_DISPLAY_TEXT_MAP.get(name) : name;
  }

  public static String getFriendlyName(int key) {
    return getFriendlyName(getKeyName(key));
  }

  static {
    mapKeyName("UNKNOWN"       , null           , KEY_UNKNOWN);
    mapKeyName("SPACE"         , "Space"        , KEY_SPACE);
    mapKeyName("APOSTROPHE"    , "'"            , KEY_APOSTROPHE);
    mapKeyName("COMMA"         , ","            , KEY_COMMA);
    mapKeyName("MINUS"         , "-"            , KEY_MINUS);
    mapKeyName("PERIOD"        , "."            , KEY_PERIOD);
    mapKeyName("SLASH"         , "/"            , KEY_SLASH);
    mapKeyName("0"             , "0"            , KEY_0);
    mapKeyName("1"             , "1"            , KEY_1);
    mapKeyName("2"             , "2"            , KEY_2);
    mapKeyName("3"             , "3"            , KEY_3);
    mapKeyName("4"             , "4"            , KEY_4);
    mapKeyName("5"             , "5"            , KEY_5);
    mapKeyName("6"             , "6"            , KEY_6);
    mapKeyName("7"             , "7"            , KEY_7);
    mapKeyName("8"             , "8"            , KEY_8);
    mapKeyName("9"             , "9"            , KEY_9);
    mapKeyName("SEMICOLON"     , ";"            , KEY_SEMICOLON);
    mapKeyName("EQUAL"         , "="            , KEY_EQUAL);
    mapKeyName("A"             , "a"            , KEY_A);
    mapKeyName("B"             , "b"            , KEY_B);
    mapKeyName("C"             , "c"            , KEY_C);
    mapKeyName("D"             , "d"            , KEY_D);
    mapKeyName("E"             , "e"            , KEY_E);
    mapKeyName("F"             , "f"            , KEY_F);
    mapKeyName("G"             , "g"            , KEY_G);
    mapKeyName("H"             , "h"            , KEY_H);
    mapKeyName("I"             , "i"            , KEY_I);
    mapKeyName("J"             , "j"            , KEY_J);
    mapKeyName("K"             , "k"            , KEY_K);
    mapKeyName("L"             , "l"            , KEY_L);
    mapKeyName("M"             , "m"            , KEY_M);
    mapKeyName("N"             , "n"            , KEY_N);
    mapKeyName("O"             , "o"            , KEY_O);
    mapKeyName("P"             , "p"            , KEY_P);
    mapKeyName("Q"             , "q"            , KEY_Q);
    mapKeyName("R"             , "r"            , KEY_R);
    mapKeyName("S"             , "s"            , KEY_S);
    mapKeyName("T"             , "t"            , KEY_T);
    mapKeyName("U"             , "u"            , KEY_U);
    mapKeyName("V"             , "v"            , KEY_V);
    mapKeyName("W"             , "w"            , KEY_W);
    mapKeyName("X"             , "x"            , KEY_X);
    mapKeyName("Y"             , "y"            , KEY_Y);
    mapKeyName("Z"             , "z"            , KEY_Z);
    mapKeyName("LEFT_BRACKET"  , "["            , KEY_LEFT_BRACKET);
    mapKeyName("BACKSLASH"     , "\\"           , KEY_BACKSLASH);
    mapKeyName("RIGHT_BRACKET" , "]"            , KEY_RIGHT_BRACKET);
    mapKeyName("GRAVE_ACCENT"  , "`"            , KEY_GRAVE_ACCENT);
    mapKeyName("WORLD_1"       , null           , KEY_WORLD_1);
    mapKeyName("WORLD_2"       , null           , KEY_WORLD_2);
    mapKeyName("ESCAPE"        , "Esc"          , KEY_ESCAPE);
    mapKeyName("ENTER"         , "Enter"        , KEY_ENTER);
    mapKeyName("TAB"           , "Tab"          , KEY_TAB);
    mapKeyName("BACKSPACE"     , "Backspace"    , KEY_BACKSPACE);
    mapKeyName("INSERT"        , "Insert"       , KEY_INSERT);
    mapKeyName("DELETE"        , "Delete"       , KEY_DELETE);
    mapKeyName("RIGHT"         , "Right"        , KEY_RIGHT);
    mapKeyName("LEFT"          , "Left"         , KEY_LEFT);
    mapKeyName("DOWN"          , "Down"         , KEY_DOWN);
    mapKeyName("UP"            , "Up"           , KEY_UP);
    mapKeyName("PAGE_UP"       , "Page Up"      , KEY_PAGE_UP);
    mapKeyName("PAGE_DOWN"     , "Page Down"    , KEY_PAGE_DOWN);
    mapKeyName("HOME"          , "Home"         , KEY_HOME);
    mapKeyName("END"           , "End"          , KEY_END);
    mapKeyName("CAPS_LOCK"     , "Caps Lock"    , KEY_CAPS_LOCK);
    mapKeyName("SCROLL_LOCK"   , "Scroll Lock"  , KEY_SCROLL_LOCK);
    mapKeyName("NUM_LOCK"      , "Num Lock"     , KEY_NUM_LOCK);
    mapKeyName("PRINT_SCREEN"  , "Print Screen" , KEY_PRINT_SCREEN);
    mapKeyName("PAUSE"         , "Pause"        , KEY_PAUSE);
    mapKeyName("F1"            , "F1"           , KEY_F1);
    mapKeyName("F2"            , "F2"           , KEY_F2);
    mapKeyName("F3"            , "F3"           , KEY_F3);
    mapKeyName("F4"            , "F4"           , KEY_F4);
    mapKeyName("F5"            , "F5"           , KEY_F5);
    mapKeyName("F6"            , "F6"           , KEY_F6);
    mapKeyName("F7"            , "F7"           , KEY_F7);
    mapKeyName("F8"            , "F8"           , KEY_F8);
    mapKeyName("F9"            , "F9"           , KEY_F9);
    mapKeyName("F10"           , "F10"          , KEY_F10);
    mapKeyName("F11"           , "F11"          , KEY_F11);
    mapKeyName("F12"           , "F12"          , KEY_F12);
    mapKeyName("F13"           , null           , KEY_F13);
    mapKeyName("F14"           , null           , KEY_F14);
    mapKeyName("F15"           , null           , KEY_F15);
    mapKeyName("F16"           , null           , KEY_F16);
    mapKeyName("F17"           , null           , KEY_F17);
    mapKeyName("F18"           , null           , KEY_F18);
    mapKeyName("F19"           , null           , KEY_F19);
    mapKeyName("F20"           , null           , KEY_F20);
    mapKeyName("F21"           , null           , KEY_F21);
    mapKeyName("F22"           , null           , KEY_F22);
    mapKeyName("F23"           , null           , KEY_F23);
    mapKeyName("F24"           , null           , KEY_F24);
    mapKeyName("F25"           , null           , KEY_F25);
    mapKeyName("KP_0"          , "Numpad 0"     , KEY_KP_0);
    mapKeyName("KP_1"          , "Numpad 1"     , KEY_KP_1);
    mapKeyName("KP_2"          , "Numpad 2"     , KEY_KP_2);
    mapKeyName("KP_3"          , "Numpad 3"     , KEY_KP_3);
    mapKeyName("KP_4"          , "Numpad 4"     , KEY_KP_4);
    mapKeyName("KP_5"          , "Numpad 5"     , KEY_KP_5);
    mapKeyName("KP_6"          , "Numpad 6"     , KEY_KP_6);
    mapKeyName("KP_7"          , "Numpad 7"     , KEY_KP_7);
    mapKeyName("KP_8"          , "Numpad 8"     , KEY_KP_8);
    mapKeyName("KP_9"          , "Numpad 9"     , KEY_KP_9);
    mapKeyName("KP_DECIMAL"    , "Numpad ."     , KEY_KP_DECIMAL);
    mapKeyName("KP_DIVIDE"     , "Numpad /"     , KEY_KP_DIVIDE);
    mapKeyName("KP_MULTIPLY"   , "Numpad *"     , KEY_KP_MULTIPLY);
    mapKeyName("KP_SUBTRACT"   , "Numpad -"     , KEY_KP_SUBTRACT);
    mapKeyName("KP_ADD"        , "Numpad +"     , KEY_KP_ADD);
    mapKeyName("KP_ENTER"      , "Numpad Enter" , KEY_KP_ENTER);
    mapKeyName("KP_EQUAL"      , null           , KEY_KP_EQUAL);
    mapKeyName("LEFT_SHIFT"    , "Left Shift"   , KEY_LEFT_SHIFT);
    mapKeyName("LEFT_CONTROL"  , "Left Ctrl"    , KEY_LEFT_CONTROL);
    mapKeyName("LEFT_ALT"      , "Left Alt"     , KEY_LEFT_ALT);
    mapKeyName("LEFT_SUPER"    , "Left Win"     , KEY_LEFT_SUPER);
    mapKeyName("RIGHT_SHIFT"   , "Right Shift"  , KEY_RIGHT_SHIFT);
    mapKeyName("RIGHT_CONTROL" , "Right Ctrl"   , KEY_RIGHT_CONTROL);
    mapKeyName("RIGHT_ALT"     , "Right Alt"    , KEY_RIGHT_ALT);
    mapKeyName("RIGHT_SUPER"   , "Right Win"    , KEY_RIGHT_SUPER);
    mapKeyName("MENU"          , "Menu"         , KEY_MENU);
    mapKeyName("LAST"          , null           , KEY_LAST);
    mapKeyName("BUTTON_1"      , "Left Button"  , MOUSE_BUTTON_1);
    mapKeyName("BUTTON_2"      , "Right Button" , MOUSE_BUTTON_2);
    mapKeyName("BUTTON_3"      , "Middle Button", MOUSE_BUTTON_3);
    mapKeyName("BUTTON_4"      , "Back Button"  , MOUSE_BUTTON_4);
    mapKeyName("BUTTON_5"      , "Forward Button", MOUSE_BUTTON_5);
    mapKeyName("BUTTON_6"      , null           , MOUSE_BUTTON_6);
    mapKeyName("BUTTON_7"      , null           , MOUSE_BUTTON_7);
    mapKeyName("BUTTON_8"      , null           , MOUSE_BUTTON_8);
  }

}