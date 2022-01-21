package org.anti_ad.mc.common.integration

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipn.api.IPNGuiHint
import org.anti_ad.mc.ipn.api.IPNIgnore

object HintsManager {

    private val upgradeGuiHintsIfAnnotationPresent = mutableMapOf<String, Boolean>()
    private val buttonHints = mapOf<IPNButton, MutableMap<String, ButtonPositionHint>>(IPNButton.MOVE_TO_CONTAINER to mutableMapOf(),
                                                                                       IPNButton.MOVE_TO_PLAYER to mutableMapOf(),
                                                                                       IPNButton.CONTINUOUS_CRAFTING to mutableMapOf(),
                                                                                       IPNButton.SORT to mutableMapOf(),
                                                                                       IPNButton.SORT_COLUMNS to mutableMapOf(),
                                                                                       IPNButton.SORT_ROWS to mutableMapOf(),
                                                                                       IPNButton.PROFILE_SELECTOR to mutableMapOf())
    private val playerSideOnlyClasses = mutableMapOf<String, String>()

    private val blackListed: MutableMap<String, Boolean> = mutableMapOf()

    val zeroZero = ButtonPositionHint()

    fun hintFor(button: IPNButton,
                javaClass: Class<*>): ButtonPositionHint {
        var coord = buttonHints[button]!![javaClass.name]

        if (upgradeGuiHintsIfAnnotationPresent[javaClass.name] != null) {
            upgradeGuiHintsIfAnnotationPresent.remove(javaClass.name)
            if (javaClass.isAnnotationPresent(IPNGuiHint::class.java)) {
                coord = null
            }
        }

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
                    val invert = if (button === IPNButton.PROFILE_SELECTOR) -1 else 1
                    ButtonPositionHint(it.horizontalOffset * invert, it.top, it.bottom * invert, it.hide)
                }
                buttonHints[it.button]?.put(javaClass.name, tmpTriple)
                if (it.button == button) {
                    res = tmpTriple
                }
            }
            if (res == zeroZero) {
                buttonHints[button]?.put(javaClass.name, zeroZero)
            }
            res
        }
    }

    fun addHints(className: String,
                 hint: HintClassData) {
        hint.buttonHints.keys.forEach {
            Log.trace("Processing hints for button: $it")
            val h = hint.buttonHints[it]!!
            Log.trace("Hint is: $h")
            Log.trace("Adding hint '$h' for button: '$it' for class $className")
            buttonHints[it]!![className] = ButtonPositionHint(h.horizontalOffset, h.top, h.bottom, h.hide)
            if (!hint.force) {
                upgradeGuiHintsIfAnnotationPresent[className] = true
            }
        }
    }

    fun isForcePlayerSide(javaClass: Class<*>?): Boolean {
        return playerSideOnlyClasses[javaClass?.name] != null
    }

    fun addPlayerSideOnly(className: String) {
        playerSideOnlyClasses[className] = className
    }


    fun getIgnoredClass(container: Class<*>): Class<*>? {
        var sup: Class<*> = container
        while (sup != Object::class.java) {
            val isIt = blackListed[sup.name]
            if (isIt != null) {
                return if (isIt) {
                    //Log.trace("Screen/Container: '${container.name}' will be ignored because (super)class: ${sup.name} was blacklisted")
                    sup
                } else {
                    //Log.trace("Class: '$container' is NOT ignored")
                    null
                }
            }
            if (sup.isAnnotationPresent(IPNIgnore::class.java)) {
                //Log.trace("Screen/Container: '${container.name}' will be ignored because (super)class: ${sup.name} is annotated with IPNIgnore.")
                blackListed[sup.name] = true
                return sup
            }
            sup = sup.superclass
        }
        blackListed[sup.name] = false
        //Log.trace("Class: '$container' is NOT ignored")
        return null
    }

    fun isAccepted(target: Any?): Boolean {
        return target != null && getIgnoredClass(target.javaClass) == null
    }

    fun addIgnore(className: String, force: Boolean = false) {
        blackListed[className] = true
    }
}