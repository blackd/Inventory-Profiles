package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.extensions.land
import org.anti_ad.mc.common.extensions.lnot
import org.anti_ad.mc.common.extensions.lor

// as kotlin regards (some) color as long
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

fun Int.alpha(value: Int) = this.dropAlpha() lor value.asAlpha()
fun Int.red(value: Int) = this.dropRed() lor value.asRed()
fun Int.green(value: Int) = this.dropGreen() lor value.asGreen()
fun Int.blue(value: Int) = this.dropBlue() lor value.asBlue()

// usage: 0xAA rgb 0xRRGGBB
infix fun Int.rgb(rgb: Int) = rgb.alpha(this)

// usage 0xAA r 0xRR g 0xGG b 0xBB
infix fun Int.r(r: Int) = 0.alpha(this).red(r)
infix fun Int.g(g: Int) = this.green(g)
infix fun Int.b(b: Int) = this.blue(b)

fun color(r: Int, g: Int, b: Int) = 255.r(r).g(g).b(b)
fun color(a: Int, r: Int, g: Int, b: Int) = a.r(r).g(g).b(b) // funny syntax a r r g g b b
fun color(a: Int, rgb: Int) = a.rgb(rgb)

// ============
// float
// ============

fun Int.alpha(value: Float) = this.alpha((value * 255 + 0.5).toInt())
fun Int.red(value: Float) = this.red((value * 255 + 0.5).toInt())
fun Int.green(value: Float) = this.green((value * 255 + 0.5).toInt())
fun Int.blue(value: Float) = this.blue((value * 255 + 0.5).toInt())
