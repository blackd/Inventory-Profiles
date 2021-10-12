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
     * The continous crafting check box
     */
    CONTINUOUS_CRAFTING;
}
