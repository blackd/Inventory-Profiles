package io.github.jsnimda.common.annotation

import kotlin.annotation.AnnotationTarget.*

/**
 * Indicates this function may throw some unexpected errors (usually by logical error or user input).
 * To safely call the function and avoid crash, the caller function should surround this function by try catch and marked with [ThrowsCaught]
 */
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, CONSTRUCTOR)
annotation class MayThrow

@Retention(AnnotationRetention.SOURCE)
@Target(
  FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, CONSTRUCTOR,
  EXPRESSION,
  LOCAL_VARIABLE,
)
annotation class ThrowsCaught

@Retention(AnnotationRetention.SOURCE)
@Target(
  FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, CONSTRUCTOR,
  EXPRESSION,
  LOCAL_VARIABLE,
)
annotation class WontThrow
