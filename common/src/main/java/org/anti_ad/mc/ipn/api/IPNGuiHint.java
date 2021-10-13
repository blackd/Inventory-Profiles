package org.anti_ad.mc.ipn.api;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(IPNGuiHints.class)
public @interface IPNGuiHint {

    /**
     * @return The button this annotation is for
     */
    IPNButton button();

    /**
     * This value has meaning for {@link IPNButton}{@code .MOVE_TO_PLAYER}, {@link IPNButton}{@code .SORT}, {@link IPNButton}{@code .SORT_COLUMNS} and {@link IPNButton}{@code .SORT_ROWS}
     * when the open inventory is a chest, barrel or any other storage.
     * @return Vertical offset of the default position. Positive values move the button Down, negative Up
     */
    int top() default 0;

    /**
     * This value has meaning for all buttons when the open inventory only the players inventory as in no chest or other storage, or It's a crafting table.
     * @return Vertical offset of the default position. Positive values move the button Up, negative Down
     */
    int bottom() default 0;

    /**
     * @return The desired horizontal offset. Positive values move the button to the left, negative to the right
     */
    int horizontalOffset() default 0;

    /**
     * This is only meaningful for {@link IPNButton}{@code .PROFILE_SELECTOR}.
     * IPN Make reasonable efforts to show this part of its UI only when the player inventory is shown,
     * specifically only when the player presses the "E" key. However, depending on how the other mod
     * has implemented its UI IPN is not always able to detect that the shown UO is not the player inventory.
     * Please use the hide option only if your UI doesn't show the player inventory and use the position hints
     * in other cases.
     * @return Whether to hide the Profile UI or not.
     */
    boolean hide() default false;
}
