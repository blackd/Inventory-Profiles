package org.anti_ad.mc.ipn.api;


public enum IPNButton {
    /**
     * this is the button that moves all from the inventory to the chest
     */
    MOVE_TO_CONTAINER,

    /**
     * this is the button that moves all from the chest to the inventory
     */
    MOVE_TO_PLAYER,

    /**
     * The sort button
     */
    SORT,

    /**
     * The sort in columns button
     */
    SORT_COLUMNS,
    /**
     * The sort in rows button
     */
    SORT_ROWS,

    /**
     * The continuous crafting check box
     */
    CONTINUOUS_CRAFTING,

    /**
     * The profile selector UI. It's only possible to move this as a whole.
     *
     * IPN Make reasonable efforts to show this part of its UI only when the player inventory is shown,
     * specifically only when the player presses the "E" key. However, depending on how the other mod
     * has implemented its UI IPN is not always able to detect that the shown UO is not the player inventory.
     * Please use the hide option only if your UI doesn't show the player inventory and use the position hints
     * in other cases.
     */
    PROFILE_SELECTOR;
}
