package org.anti_ad.mc.common.vanilla.accessors.entity

import net.minecraft.client.gui.screen.inventory.MerchantScreen as VanillaMerchantScreen
import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.ItemStack
import org.anti_ad.mc.common.vanilla.alias.MerchantScreen
import org.anti_ad.mc.common.vanilla.alias.NbtElement
import org.anti_ad.mc.common.vanilla.alias.StringNbtReader
import org.anti_ad.mc.common.vanilla.alias.entity.VillagerEntity
import org.anti_ad.mc.common.vanilla.alias.village.VillagerProfession
import org.anti_ad.mc.common.vanilla.alias.village.TradeOffer

typealias MerchantScreen_WidgetButtonPage = VanillaMerchantScreen.TradeButton

val VillagerEntity.`(profession)`
    inline get() = this.villagerData.profession

val VillagerProfession.`(professionId)`
    inline get() = this.toString()

val VillagerEntity.`(villagerData)`
    inline get() = this.villagerData

val MerchantScreen.`(recipes)`
    get() = this.container.offers

val MerchantScreen.`(indexStartOffset)`: Int
    get() {
        return this.field_214139_n
    }

val MerchantScreen_WidgetButtonPage.index
    get() = this.func_212937_a()

val MerchantScreen.`(offers)`
    get() = field_214138_m

val ClickableWidget.`(isHovered)`
    get() = isHovered

val TradeOffer.`(originalFirstBuyItem)`
    get() = buyingStackFirst

val TradeOffer.`(secondBuyItem)`: ItemStack?
    get() = buyingStackSecond

val TradeOffer.`(sellItem)`
    get() = sellingStack

val VillagerProfession.`(id)`
    get() = toString()

fun StringNbtReader_parse(s: String): NbtElement? {
    return StringNbtReader.getTagFromJson(s)
}
