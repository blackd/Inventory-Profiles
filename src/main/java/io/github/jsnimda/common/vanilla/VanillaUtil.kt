package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.util.*
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.alias.Util
import io.github.jsnimda.common.vanilla.render.rScreenHeight
import io.github.jsnimda.common.vanilla.render.rScreenWidth
import java.io.File
import java.nio.file.Path

val Path.loggingPath
  get() = VanillaUtil.loggingString(this)

object VanillaUtil {
  fun isOnClientThread(): Boolean = // Thread.currentThread() == this.getThread()
    Vanilla.mc().isSameThread // isOnExecutionThread // isOnThread()

  // ============
  // info
  // ============
  fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

  fun languageCode(): String = Vanilla.languageManager().selected.code

  fun shiftDown() = Screen.hasShiftDown()
  fun ctrlDown() = Screen.hasControlDown()
  fun altDown() = Screen.hasAltDown()
//  fun shiftDown() = Screen.func_231173_s_() // line 391
//  fun ctrlDown() = Screen.func_231172_r_() // line 383
//  fun altDown() = Screen.func_231174_t_() // line 395

  // Mouse.onCursorPos() / GameRenderer.render()
  fun mouseX(): Int = mouseXDouble().toInt()
  fun mouseY(): Int = mouseYDouble().toInt()
  fun mouseXRaw(): Double = Vanilla.mouse().xpos()
  fun mouseYRaw(): Double = Vanilla.mouse().ypos()
  fun mouseXDouble(): Double = mouseScaleX(mouseXRaw())
  fun mouseYDouble(): Double = mouseScaleY(mouseYRaw())
  fun mouseScaleX(amount: Double): Double = amount * rScreenWidth / Vanilla.window().width
  fun mouseScaleY(amount: Double): Double = amount * rScreenHeight / Vanilla.window().height

  // this.client.getLastFrameDuration()
  fun lastFrameDuration(): Float = Vanilla.mc().frameTime // for render

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
  fun closeScreen() = Vanilla.mc().setScreen(null)
  fun openScreen(screen: Screen) = Vanilla.mc().setScreen(screen)
  fun openScreenNullable(screen: Screen?) = Vanilla.mc().setScreen(screen)
  fun openDistinctScreen(screen: Screen) { // do nothing if screen is same type as current
    if (Vanilla.screen()?.javaClass != screen.javaClass) openScreen(screen)
  }

  fun openDistinctScreenQuiet(screen: Screen) { // don't trigger Screen.remove()
    if (Vanilla.screen()?.javaClass != screen.javaClass) {
      Vanilla.mc().screen = null
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
    Util.getPlatform().openFile(file)
  }
}