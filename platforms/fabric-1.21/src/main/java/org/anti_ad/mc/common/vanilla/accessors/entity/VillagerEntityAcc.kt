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
    inline get() = this.id

val VillagerEntity.`(villagerData)`
    inline get() = this.villagerData

val MerchantScreen.`(recipes)`
    get() = this.screenHandler.recipes

val MerchantScreen.`(indexStartOffset)`
    get() = indexStartOffset

var MerchantScreen.`(selectedIndex)`: Int
    get() = selectedIndex
    set(value) {
        selectedIndex = value
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
    get() = if (secondBuyItem.isPresent) secondBuyItem.get() else null

val TradeOffer.`(sellItem)`
    get() = sellItem

val TradeOffer.`(isDisabled)`
    get() = isDisabled

val VillagerProfession.`(id)`
    get() = id

fun StringNbtReader_parse(s: String): NbtElement? {
    return StringNbtReader.parse(s)
}
