package io.github.jsnimda.common

fun <T: Enum<T>> T.next(): T {
  val values = declaringClass.enumConstants
  return values[(ordinal + 1) % values.size]
}

fun <T: Enum<T>> T.previous(): T {
  val values = declaringClass.enumConstants
  return values[(ordinal + values.size - 1) % values.size]
}