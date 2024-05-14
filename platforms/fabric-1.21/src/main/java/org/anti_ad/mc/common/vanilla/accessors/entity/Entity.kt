package org.anti_ad.mc.common.vanilla.accessors.entity

import org.anti_ad.mc.common.vanilla.alias.entity.Entity

val Entity.`(uuid)`
    get() = uuid

val Entity.`(uuidString)`: String
    get() = uuidAsString

val Entity.`(pos)`
    get() = pos

val Entity.`(blockPos)`
    get() = blockPos
