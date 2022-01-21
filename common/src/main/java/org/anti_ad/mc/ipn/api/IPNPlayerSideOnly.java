package org.anti_ad.mc.ipn.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Add this annotation if your Screen or ScreenHandler should only be considered for player
 * inventory operations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IPNPlayerSideOnly {

}
