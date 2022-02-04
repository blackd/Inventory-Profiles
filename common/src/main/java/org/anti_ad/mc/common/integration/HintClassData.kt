package org.anti_ad.mc.common.integration

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.anti_ad.mc.ipn.api.IPNButton

@Serializable
data class ButtonPositionHint(var horizontalOffset: Int = 0,
                              var top: Int = 0,
                              var bottom: Int = 0,
                              var hide: Boolean = false ) {
    @Transient
    var dirty: Boolean = false

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
            if (hints.dirty) {
                return true
            }
        }
        return false
    }

    fun hasInfo(): Boolean {
        return playerSideOnly || ignore || force || buttonHints.filterValues { v ->
            v.top + v.horizontalOffset + v.bottom != 0 && ! v.hide
        }.isNotEmpty()
    }

    fun copyOnlyChanged(): MutableMap<IPNButton, ButtonPositionHint> {
        return buttonHints.filter { (k, v) ->
            !v.hide && v.top + v.bottom + v.horizontalOffset > 0
        }.toMutableMap()
    }

    fun fillMissingHints() {
        IPNButton.values().forEach {
            buttonHints.putIfAbsent(it, ButtonPositionHint())
        }
    }

}
