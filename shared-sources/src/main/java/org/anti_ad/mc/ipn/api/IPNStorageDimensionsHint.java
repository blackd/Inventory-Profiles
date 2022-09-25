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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation informs Inventory Profiles Next about the
 * dimensions of the storage. This information is used by IPN
 * for its column and row sorting functionality.
 * <p>By default, IPN can detect all vanilla storage dimensions,
 * so if your custom storage screen inherits from some vanilla
 * storage screen and doesn't change the dimensions you don't need to
 * worry about compatibility.</p>
 * <p>
 * However even if your custom storage has the standard dimensions
 * but you don't inherit from vanilla screen it's best to add this
 * annotation.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IPNStorageDimensionsHint {
    /**
     * @return the horizontal length in number of slots. Do I need to say it starts from 0.
     */
    int x();

    /**
     * @return the horizontal length in number of slots. Do I need to say it starts from 0.
     */
    int y();

    /**
     * IPN supports sorting in columns and rows only if the storage slots have rectangular
     * shape. If you set this to false IPN will not try to do sorting in columns and rows.
     * Normal sorting will still work.
     * @return {@code true} if the shape of your storage is rectangular.
     */
    boolean isRectangular() default true;
}
