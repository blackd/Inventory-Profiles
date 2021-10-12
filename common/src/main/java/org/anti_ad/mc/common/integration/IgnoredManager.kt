package org.anti_ad.mc.common.integration

import kotlinx.serialization.Serializable
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.ipn.api.IPNIgnore


object IgnoredManager {

    private val blackListed: MutableMap<Class<*>, Boolean> = mutableMapOf()

    fun getIgnoredClass(container: Class<*>): Class<*>? {
        Log.trace("Checking if class: '$container' is ignored")
        var sup: Class<*> = container
        while (sup != Object::class.java) {
            val isIt = blackListed[sup]
            if (isIt != null) {
                return if (isIt) {
                    Log.trace("Screen/Container: '${container.name}' will be ignored because (super)class: ${sup.name} was blacklisted")
                    sup
                } else {
                    Log.trace("Class: '$container' is NOT ignored")
                    null
                }
            }
            if (sup.isAnnotationPresent(IPNIgnore::class.java)) {
                Log.trace("Screen/Container: '${container.name}' will be ignored because (super)class: ${sup.name} is annotated with IPNIgnore.")
                blackListed[sup] = true
                return sup
            }
            sup = sup.superclass
        }
        blackListed[sup] = false
        Log.trace("Class: '$container' is NOT ignored")
        return null
    }

    fun addIgnore(cl: Class<*>) {
        blackListed[cl] = true
    }

}


