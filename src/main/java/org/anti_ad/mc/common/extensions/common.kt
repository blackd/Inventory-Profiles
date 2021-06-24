package org.anti_ad.mc.common.extensions


// ============
// tryCatch()
// ============
// tryOrElse    handle error
// tryCatch     printStackTrace
// trySwallow   swallow error

// use this functions if the exceptions is unusual and not expected (typically IOException)

inline fun <R> trySwallow(tryToRun: () -> R): R? = tryOrElse({ null },
                                                             tryToRun)

inline fun <R> trySwallow(failureValue: R,
                          tryToRun: () -> R): R = tryOrElse({ failureValue },
                                                            tryToRun)

inline fun <R> tryOrPrint(printFailure: (String) -> Unit,
                          tryToRun: () -> R): R? =
    tryOrPrint(null,
               printFailure,
               tryToRun)

inline fun <R> tryOrPrint(failureValue: R,
                          printFailure: (String) -> Unit,
                          tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        printFailure(e.toString())
        failureValue
    }
}

//inline fun <R> tryCatchAlsoPrint(printFailure: (String) -> Unit, tryToRun: () -> R): R? =
//  tryCatchAlsoPrint(null, printFailure, tryToRun)
//
//inline fun <R> tryCatchAlsoPrint(failureValue: R, printFailure: (String) -> Unit, tryToRun: () -> R): R {
//  return try {
//    tryToRun()
//  } catch (e: Throwable) {
//    e.printStackTrace()
//    printFailure(e.toString())
//    failureValue
//  }
//}

inline fun <R> tryCatch(tryToRun: () -> R): R? = tryCatch({ null },
                                                          tryToRun)

inline fun <R> tryCatch(failureValue: R,
                        tryToRun: () -> R): R = tryCatch({ failureValue },
                                                         tryToRun)

inline fun <R> tryCatch(onFailure: (Throwable) -> R,
                        tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        e.printStackTrace()
        onFailure(e)
    }
}

inline fun <R> tryOrElse(onFailure: (Throwable) -> R,
                         tryToRun: () -> R): R {
    return try {
        tryToRun()
    } catch (e: Throwable) {
        onFailure(e)
    }
}

// ============
// Event
// ============

class Event<T> {
    private val handlers = mutableSetOf<((data: T) -> Unit)>()
    operator fun plusAssign(handler: (T) -> Unit) {
        handlers.add(handler)
    }

    operator fun minusAssign(handler: (T) -> Unit) {
        handlers.remove(handler)
    }

    operator fun invoke(data: T) {
        handlers.forEach { it(data) }
    }
}

class RoutedEvent<T> {
    private val handlers = mutableSetOf<((data: T, handled: Boolean) -> Boolean)>()
    operator fun plusAssign(handler: (T, handled: Boolean) -> Boolean) {
        handlers.add(handler)
    }

    operator fun minusAssign(handler: (T, handled: Boolean) -> Boolean) {
        handlers.remove(handler)
    }

    operator fun invoke(data: T,
                        handled: Boolean): Boolean {
        return handlers.map {
            it(data,
               handled)
        }.any { it }
    }
}
