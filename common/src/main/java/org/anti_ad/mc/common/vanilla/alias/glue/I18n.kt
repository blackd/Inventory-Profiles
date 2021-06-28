package org.anti_ad.mc.common.vanilla.alias.glue

import org.anti_ad.mc.common.Log

var __glue_I18n_translate: (String, objects: Array<out Any?>) -> String = {string: String, objects: Array<out Any?> ->
    Log.error("__glue_I18n_translate not initialized!")
    string
}

object I18n {
    fun translate(string: String,
                  vararg objects: Any?): String = __glue_I18n_translate(string,
                                                                        objects)

    fun translateOrNull(string: String,
                        vararg objects: Any?): String? =
        translate(string,
                  *objects).takeIf { it != string }

    fun translateOrEmpty(string: String,
                         vararg objects: Any?): String = translateOrNull(string,
                                                                         *objects) ?: ""

    inline fun translateOrElse(string: String,
                               vararg objects: Any?,
                               elseValue: () -> String): String =
        translateOrNull(string,
                        *objects) ?: elseValue()
}