package io.github.jsnimda.common.util

// use this function if the exceptions is unusual and not expected (typically IOException)
inline fun <T> wrapError(tryToRun: () -> T): T? = wrapError(null as T?, tryToRun)
inline fun <T> wrapError(failureValue: T, tryToRun: () -> T): T = try {
  tryToRun()
} catch (e: Throwable) {
  e.printStackTrace()
  failureValue
}

inline fun <T> wrapError(onFailure: (Throwable) -> Unit, tryToRun: () -> T): T? = wrapError(null, onFailure, tryToRun)
inline fun <T> wrapError(failureValue: T, onFailure: (Throwable) -> Unit, tryToRun: () -> T): T = try {
  tryToRun()
} catch (e: Throwable) {
  onFailure(e)
  failureValue
}
