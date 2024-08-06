
/*
 * MIT License
 *
 * Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 * Copyright (c) 2021-2022 Plamen K. Kosseff
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
    PROFILE_SELECTOR,
    SHOW_EDITOR,
    SETTINGS,
    VILLAGER_DO_GLOBAL_TRADES,
    VILLAGER_DO_GLOBAL_TRADES1,
    VILLAGER_DO_GLOBAL_TRADES2,
    VILLAGER_DO_LOCAL_TRADES,
    VILLAGER_DO_LOCAL_TRADES1,
    VILLAGER_DO_LOCAL_TRADES2,
    VILLAGER_GLOBAL_BOOKMARK,
    VILLAGER_LOCAL_BOOKMARK,
    /**
     * The sort button
     */
    SORT_PLAYER,

    /**
     * The sort in columns button
     */
    SORT_COLUMNS_PLAYER,
    /**
     * The sort in rows button
     */
    SORT_ROWS_PLAYER,
}
