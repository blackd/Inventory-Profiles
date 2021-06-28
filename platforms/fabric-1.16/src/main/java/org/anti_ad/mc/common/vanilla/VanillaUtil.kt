package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.extensions.*
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Util
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenHeight
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth

import java.io.File
import java.nio.file.Path

val Path.loggingPath
    get() = VanillaUtil.loggingString(this)

object VanillaUtil {
    fun isOnClientThread(): Boolean = // Thread.currentThread() == this.getThread()
        Vanilla.mc().isOnThread // isOnThread()

    // ============
    // info
    // ============
    fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

    fun languageCode(): String = Vanilla.languageManager().language.code

    fun shiftDown() = Screen.hasShiftDown()
    fun ctrlDown() = Screen.hasControlDown()
    fun altDown() = Screen.hasAltDown()

    // Mouse.onCursorPos() / GameRenderer.render()
    fun mouseX(): Int = mouseXDouble().toInt()
    fun mouseY(): Int = mouseYDouble().toInt()
    fun mouseXRaw(): Double = Vanilla.mouse().x
    fun mouseYRaw(): Double = Vanilla.mouse().y
    fun mouseXDouble(): Double = mouseScaleX(mouseXRaw())
    fun mouseYDouble(): Double = mouseScaleY(mouseYRaw())
    fun mouseScaleX(amount: Double): Double = amount * glue_rScreenWidth / Vanilla.window().width
    fun mouseScaleY(amount: Double): Double = amount * glue_rScreenHeight / Vanilla.window().height

    // this.client.getLastFrameDuration()
    fun lastFrameDuration(): Float = Vanilla.mc().lastFrameDuration

//  var lastMouseX: Int = -1
//    private set
//  var lastMouseY: Int = -1
//    private set
//  var mouseX: Int = -1
//    private set
//  var mouseY: Int = -1
//    private set
//
//  fun updateMouse() {
//    lastMouseX = mouseX
//    lastMouseY = mouseY
//    mouseX = mouseX()
//    mouseY = mouseY()
//  }

    // ============
    // do actions
    // ============
    fun closeScreen() = Vanilla.mc().openScreen(null)
    fun openScreen(screen: Screen) = Vanilla.mc().openScreen(screen)
    fun openScreenNullable(screen: Screen?) = Vanilla.mc().openScreen(screen)
    fun openDistinctScreen(screen: Screen) { // do nothing if screen is same type as current
        if (Vanilla.screen()?.javaClass != screen.javaClass) openScreen(screen)
    }

    fun openDistinctScreenQuiet(screen: Screen) { // don't trigger Screen.remove()
        if (Vanilla.screen()?.javaClass != screen.javaClass) {
            Vanilla.mc().currentScreen = null
            openScreen(screen)
        }
    }

    private fun runDirectory(): Path = Vanilla.runDirectoryFile().toPath().normalize()
    fun configDirectory(): Path = runDirectory() / "config"
    fun configDirectory(modName: String): Path = (configDirectory() / modName).apply { createDirectories() }

    fun getResourceAsString(identifier: String): String? = tryCatch {
        Vanilla.resourceManager().getResource(Identifier(identifier)).inputStream?.readToString()
    }

    fun loggingString(path: Path): String = // return ".minecraft/config/file.txt" etc
        (if (path.isAbsolute) path pathFrom (runDirectory() / "..") else path).toString()

    fun open(file: File) {
        // ResourcePackOptionsScreen.init()
        Util.getOperatingSystem().open(file)
    }
}