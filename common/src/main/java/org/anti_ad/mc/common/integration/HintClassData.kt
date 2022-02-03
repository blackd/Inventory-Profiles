package org.anti_ad.mc.common.integration

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.anti_ad.mc.ipn.api.IPNButton

@Serializable
data class ButtonPositionHint(var horizontalOffset: Int = 0,
                              var top: Int = 0,
                              var bottom: Int = 0,
                              var hide: Boolean = false ) {

}

@Serializable
data class HintClassData(var ignore: Boolean = false,
                         var playerSideOnly: Boolean = false,
                         val buttonHints: MutableMap<IPNButton, ButtonPositionHint> = mutableMapOf(),
                         var force: Boolean = false) {

    @Transient
    private var id: String? = null

    @Transient
    private var dirty: Boolean = false

    fun changeId(newId: String) {
        id = newId
    }

    fun readId() = id

    fun dirty(): Boolean {
        return dirty
    }
    fun markAsDirty() {
        dirty = true
    }

    fun hintFor(button: IPNButton): ButtonPositionHint {
        return buttonHints[button] ?: ButtonPositionHint().also { buttonHints[button] = it }
    }

    fun areButtonsMoved(): Boolean {
        buttonHints.forEach { (_, hints) ->
            if (hints.top != 0 || hints.bottom != 0 || hints.horizontalOffset != 0) {
                return true
            }
        }
        return false
    }

}
