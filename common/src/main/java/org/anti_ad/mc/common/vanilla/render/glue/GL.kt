package org.anti_ad.mc.common.vanilla.render.glue

import org.anti_ad.mc.common.Log

var __glue_rStandardGlState: () -> Unit = {
    Log.error("__glue_rStandardGlState is not initialized!")
}

val rStandardGlState
    get() = __glue_rStandardGlState

var __glue_rClearDepth: () -> Unit = {
    Log.error("__glue_rClearDepth is not initialized!")
}

val rClearDepth
    get() = __glue_rClearDepth