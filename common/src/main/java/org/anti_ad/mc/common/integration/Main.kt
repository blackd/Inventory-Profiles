package org.anti_ad.mc.common.integration

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.anti_ad.mc.ipn.api.IPNButton

fun main(args: Array<String>) {
    val l: MutableMap<String, HintClassData> = mutableMapOf()
    var v = HintClassData(true)
    l["de.maxhenkel.camera.gui.ImageScreen"] = v
    v =HintClassData(false, mapOf(IPNButton.MOVE_TO_CONTAINER to ButtonPositionHint(60)))
    l["ninjaphenix.expandedstorage.base.client.menu.PagedScreen"] = v
    val s = Json.encodeToString(l)
    println(s)
}