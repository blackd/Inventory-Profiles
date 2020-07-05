package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.util.*
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.render.rScreenHeight
import io.github.jsnimda.common.vanilla.render.rScreenWidth
import net.minecraft.util.Util
import java.io.File
import java.nio.file.Path

val Path.loggingPath
  get() = VanillaUtil.loggingString(this)

object VanillaUtil {
  fun isOnClientThread(): Boolean = // Thread.currentThread() == this.getThread()
    Vanilla.mc().isOnExecutionThread // isOnThread()

  // ============
  // info
  // ============
  fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

  fun languageCode(): String = Vanilla.languageManager().currentLanguage.code

  fun shiftDown() = Screen.hasShiftDown()
  fun ctrlDown() = Screen.hasControlDown()
  fun altDown() = Screen.hasAltDown()

  // Mouse.onCursorPos() / GameRenderer.render()
  fun mouseX(): Int = (Vanilla.mouse().mouseX * rScreenWidth / Vanilla.window().width).toInt()
  fun mouseY(): Int = (Vanilla.mouse().mouseY * rScreenHeight / Vanilla.window().height).toInt()

  // this.client.getLastFrameDuration()
  fun lastFrameDuration(): Float = Vanilla.mc().tickLength // for render

  var lastMouseX: Int = -1
    private set
  var lastMouseY: Int = -1
    private set
  var mouseX: Int = -1
    private set
  var mouseY: Int = -1
    private set

  fun updateMouse() {
    lastMouseX = mouseX
    lastMouseY = mouseY
    mouseX = mouseX()
    mouseY = mouseY()
  }

  // ============
  // do actions
  // ============
  fun closeScreen() = Vanilla.mc().displayGuiScreen(null)
  fun openScreen(screen: Screen) = Vanilla.mc().displayGuiScreen(screen)
  fun openScreenNullable(screen: Screen?) = Vanilla.mc().displayGuiScreen(screen)
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
    Util.getOSType().openFile(file)
  }
}