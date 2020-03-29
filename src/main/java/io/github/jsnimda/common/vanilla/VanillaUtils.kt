package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.util.createDirectories
import io.github.jsnimda.common.util.readToString
import io.github.jsnimda.common.util.wrapError
import java.nio.file.Path

object VanillaUtils {

  fun openScreenNullable(screen: Screen?) = Vanilla.mc().openScreen(screen)
  fun openScreen(screen: Screen) = Vanilla.mc().openScreen(screen)
  fun closeScreen() = Vanilla.mc().openScreen(null)

  fun runDirectory(): Path = Vanilla.mc().runDirectory.toPath() ?: error("mc.runDirectory is not initialized!")
  fun configDirectory(): Path = runDirectory().resolve("config")
  fun configDirectory(modName: String): Path = configDirectory().resolve(modName).apply { createDirectories() }

  fun getResourceAsString(identifier: String): String? = wrapError {
    Vanilla.resourceManager().getResource(Identifier(identifier)).inputStream?.readToString()
  }

  fun languageCode(): String = Vanilla.languageManager().language.code

}