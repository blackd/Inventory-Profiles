package org.anti_ad.mc.ipn.api;

import java.lang.annotation.*;

/**
 * You SHOULD never use this annotation directly.
 * Use multiple IPNGuiHint annotations.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IPNGuiHints {
    IPNGuiHint[] value();
}
