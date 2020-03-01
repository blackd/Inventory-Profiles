package io.github.jsnimda.common.config

import com.google.gson.JsonElement

interface IConfigElement {
  fun toJsonElement(): JsonElement
  fun fromJsonElement(element: JsonElement)
}