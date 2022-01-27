package org.anti_ad.mc.common.integration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json as HiddenJson
import kotlinx.serialization.json.decodeFromStream
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.exists
import org.anti_ad.mc.common.extensions.name
import org.anti_ad.mc.ipn.api.IPNButton
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream


val BUTTON_NO_HINTS = ButtonPositionHint()

@Serializable
data class ButtonPositionHint(val horizontalOffset: Int = 0,
                              val top: Int = 0,
                              val bottom: Int = 0,
                              val hide: Boolean = false ) {

}

@Serializable
data class HintClassData(val ignore: Boolean = false,
                         val playerSideOnly: Boolean = false,
                         val buttonHints: Map<IPNButton, ButtonPositionHint> = emptyMap(),
                         val force: Boolean = false) {

    private var id: String? = null

    fun changeId(newId: String) {
        id = newId
    }

    fun readId() = id

    fun hintFor(button: IPNButton) = buttonHints[button] ?: BUTTON_NO_HINTS

}
