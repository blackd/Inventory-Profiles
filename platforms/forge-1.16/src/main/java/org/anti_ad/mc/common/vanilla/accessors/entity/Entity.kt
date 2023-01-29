package org.anti_ad.mc.common.vanilla.accessors.entity

import org.anti_ad.mc.common.vanilla.alias.entity.Entity

val Entity.`(uuid)`
    get() = uniqueID

val Entity.`(uuidString)`: String
    get() = cachedUniqueIdString

val Entity.`(pos)`
    get() = this.positionVec

val Entity.`(blockPos)`
    get() = position
