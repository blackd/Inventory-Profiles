package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.ipnext.debug.GenerateRuleListButtonInfoDelegate
import org.anti_ad.mc.ipnext.debug.GenerateTagVanillaTxtButtonInfoDelegate

fun configInitGlue() {
    GenerateRuleListButtonInfo.delegate = GenerateRuleListButtonInfoDelegate
    GenerateTagVanillaTxtButtonInfo.delegate = GenerateTagVanillaTxtButtonInfoDelegate
}