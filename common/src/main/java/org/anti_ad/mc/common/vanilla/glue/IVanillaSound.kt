package org.anti_ad.mc.common.vanilla.glue


var __glue_vanillaSound: IVanillaSound? = null

val VanillaSound: IVanillaSound
    get() {
        return __glue_vanillaSound ?: DummyVanillaSound
    }

private object DummyVanillaSound: IVanillaSound {
    override fun playClick() {
        TODO("Not yet implemented")
    }
}

interface IVanillaSound {
    fun playClick()
}
