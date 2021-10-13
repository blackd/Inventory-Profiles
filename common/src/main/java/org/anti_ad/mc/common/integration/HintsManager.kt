package org.anti_ad.mc.common.integration

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipn.api.IPNGuiHint

object HintsManager {

    private val buttonHints = mapOf<IPNButton, MutableMap<Class<*>, ButtonPositionHint>>(IPNButton.MOVE_TO_CONTAINER to mutableMapOf(),
                                                                                         IPNButton.MOVE_TO_PLAYER to mutableMapOf(),
                                                                                         IPNButton.CONTINUOUS_CRAFTING to mutableMapOf(),
                                                                                         IPNButton.SORT to mutableMapOf(),
                                                                                         IPNButton.SORT_COLUMNS to mutableMapOf(),
                                                                                         IPNButton.SORT_ROWS to mutableMapOf(),
                                                                                         IPNButton.PROFILE_SELECTOR to mutableMapOf())

    val zeroZero = ButtonPositionHint()

    fun hintFor(button: IPNButton,
                javaClass: Class<*>): ButtonPositionHint {
        val coord = buttonHints[button]!![javaClass]

        return if (coord != null) {
            Log.trace("Founds hint for class: ${javaClass.name} + $coord")
            coord
        } else {
            var res = zeroZero
            javaClass.getAnnotationsByType(IPNGuiHint::class.java).forEach {
                val tmpTriple = if (it.horizontalOffset == 0 && it.top == 0 && it.bottom == 0) {
                    Log.warn("IPNGuiHint annotation of '${javaClass.name}' for button IPNButton.${button.name} has no meaningful value. Please report this to the mod author.")
                    zeroZero
                } else {
                    ButtonPositionHint(it.horizontalOffset, it.top, it.bottom, it.hide)
                }
                buttonHints[it.button]?.put(javaClass, tmpTriple)
                if (it.button == button) {
                    res = tmpTriple
                }
            }
            if (res == zeroZero) {
                buttonHints[button]?.put(javaClass, zeroZero)
            }
            res
        }
    }

    fun addHints(cl: Class<*>,
                 hint: HintClassData) {
        hint.buttonHints.keys.forEach {
            Log.trace("Processing hints for button: $it")
            val h = hint.buttonHints[it]!!
            Log.trace("Hint is: $h")
            if (cl.isAnnotationPresent(IPNGuiHint::class.java)) {
                if (hint.forceButtonHints) {
                    buttonHints[it]!![cl] =  ButtonPositionHint(h.right, h.top, h.bottom, h.hide)
                } else {
                    hintFor(it, cl)
                }
            } else {
                Log.trace("Adding hint '$h' for button: '$it' for class ${cl.name}")
                buttonHints[it]!![cl] = ButtonPositionHint(h.right, h.top, h.bottom, h.hide)
            }
        }
    }

}