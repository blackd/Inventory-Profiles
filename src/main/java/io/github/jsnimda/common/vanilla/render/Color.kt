package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.util.land
import io.github.jsnimda.common.util.lnot
import io.github.jsnimda.common.util.lor

const val COLOR_WHITE = 0xFFFFFFFF.toInt()
const val COLOR_BLACK = 0xFF000000.toInt()


const val COLOR_HUD_TEXT_BG = 0x90505050.toInt()
const val COLOR_HUD_TEXT = 0xE0E0E0

// as kotlin color regarded as long
val Long.color
  get() = this.toInt()

// get color channel
val Int.alpha
  get() = (this shr 24) land 0xff
val Int.red
  get() = (this shr 16) land 0xff
val Int.green
  get() = (this shr 8) land 0xff
val Int.blue
  get() = (this shr 0) land 0xff

val Int.opaque
  get() = (0xff shl 24) lor this
val Int.transparent
  get() = 0xffffff land this

// as component of argb int
fun Int.asAlpha() = (this land 0xff) shl 24
fun Int.asRed() = (this land 0xff) shl 16
fun Int.asGreen() = (this land 0xff) shl 8
fun Int.asBlue() = (this land 0xff) shl 0

fun Int.dropAlpha() = this.transparent
fun Int.dropRed() = 0xff0000.lnot() land this
fun Int.dropGreen() = 0xff00.lnot() land this
fun Int.dropBlue() = 0xff.lnot() land this

// usage: 0xAA aRgb 0xRRGGBB
infix fun Int.argb(rgb: Int) = rgb.dropAlpha() lor this.asAlpha()

// usage 0xAA ar 0xRR g 0xGG b 0xBB
infix fun Int.r(r: Int) = this.asAlpha() lor r.asRed()
infix fun Int.g(g: Int) = this lor g.asGreen()
infix fun Int.b(b: Int) = this lor b.asBlue()

fun color(r: Int, g: Int, b: Int) = 255.r(r).g(g).b(b)
fun color(a: Int, r: Int, g: Int, b: Int) = a.r(r).g(g).b(b) // funny syntax a r r g g b b
fun color(a: Int, rgb: Int) = a argb rgb

