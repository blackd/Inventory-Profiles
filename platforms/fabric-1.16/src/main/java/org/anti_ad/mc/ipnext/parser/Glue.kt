package org.anti_ad.mc.ipnext.parser

import org.anti_ad.mc.ipnext.config.__glue_profileFilePath

fun parserInitGlue() {
    __glue_profileFilePath = { ProfilesLoader.file }
}