package org.anti_ad.mc.common.vanilla.accessors.entity


import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.MerchantScreen
import org.anti_ad.mc.common.vanilla.alias.NbtElement
import org.anti_ad.mc.common.vanilla.alias.StringNbtReader
import org.anti_ad.mc.common.vanilla.alias.entity.VillagerEntity
import org.anti_ad.mc.common.vanilla.alias.village.TradeOffer
import org.anti_ad.mc.common.vanilla.alias.village.VillagerProfession

val VillagerEntity.`(profession)`
    inline get() = this.villagerData.profession

val VillagerProfession.`(professionId)`
    inline get() = this.toString()

val VillagerEntity.`(villagerData)`
    inline get() = this.villagerData

val MerchantScreen.`(recipes)`
    get() = this.screenHandler.recipes

val MerchantScreen.`(indexStartOffset)`
    get() = indexStartOffset

var MerchantScreen.`(selectedIndex)`: Int
    get() = this.selectedIndex
    set(value) {
        this.selectedIndex = value
    }

fun MerchantScreen.`(syncRecipeIndex)`() {
    this.syncRecipeIndex()
}

val MerchantScreen.`(offers)`
    get() = offers

val ClickableWidget.`(isHovered)`
    get() = isHovered

val TradeOffer.`(originalFirstBuyItem)`
    get() = originalFirstBuyItem

val TradeOffer.`(secondBuyItem)`
    get() = secondBuyItem

val TradeOffer.`(sellItem)`
    get() = sellItem

val VillagerProfession.`(id)`
    get() = toString()

fun StringNbtReader_parse(s: String): NbtElement? {
    return StringNbtReader.parse(s)
}
