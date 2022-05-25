/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.extensions.createDirectories
import org.anti_ad.mc.common.extensions.div
import org.anti_ad.mc.common.extensions.pathFrom
import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.alias.Util
import org.anti_ad.mc.common.vanilla.glue.IVanillaUtil
import org.anti_ad.mc.common.vanilla.glue.__glue_vanillaUtil
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenHeight
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.concurrent.thread

fun initVanillaUtil() {
    __glue_vanillaUtil = VanillaUtil
}


private object VanillaUtil : IVanillaUtil {
    override fun isOnClientThread(): Boolean = // Thread.currentThread() == this.getThread()
        Vanilla.mc().isOnThread // isOnThread()

    // ============
    // info
    // ============
    override fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

    override fun languageCode(): String = Vanilla.languageManager().language.code

    override fun shiftDown() = Screen.hasShiftDown()
    override fun ctrlDown() = Screen.hasControlDown()
    override fun altDown() = Screen.hasAltDown()

    // Mouse.onCursorPos() / GameRenderer.render()
    override fun mouseX(): Int = mouseXDouble().toInt()
    override fun mouseY(): Int = mouseYDouble().toInt()
    override fun mouseXRaw(): Double = Vanilla.mouse().x
    override fun mouseYRaw(): Double = Vanilla.mouse().y
    override fun mouseXDouble(): Double = mouseScaleX(mouseXRaw())
    override fun mouseYDouble(): Double = mouseScaleY(mouseYRaw())
    override fun mouseScaleX(amount: Double): Double = amount * glue_rScreenWidth / Vanilla.window().width
    override fun mouseScaleY(amount: Double): Double = amount * glue_rScreenHeight / Vanilla.window().height

    // this.client.getLastFrameDuration()
    override fun lastFrameDuration(): Float = Vanilla.mc().lastFrameDuration

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


    private fun runDirectory(): Path = Vanilla.runDirectoryFile().toPath().normalize()
    override fun configDirectory(): Path = runDirectory() / "config"
    override fun configDirectory(modName: String): Path = (configDirectory() / modName).apply { createDirectories() }

    override fun getResourceAsString(identifier: String): String? = tryCatch {
        Vanilla.resourceManager().getResource(Identifier(identifier)).inputStream?.reader()?.readText()
    }

    override fun loggingString(path: Path): String = // return ".minecraft/config/file.txt" etc
        (if (path.isAbsolute) path pathFrom (runDirectory() / "..") else path).toString()


    override fun open(file: File) {
        // ResourcePackOptionsScreen.init()
        //todo check with every version if this is safe to do!!!!!
        thread { ->
            Util.getOperatingSystem().open(file)
        }
    }

    override fun open(url: URL) {
        // ResourcePackOptionsScreen.init()
        thread { ->
            Util.getOperatingSystem().open(url)
        }
    }


    override fun isValidScreen(ctx: KeybindSettings.Context) = ctx.isValid(Vanilla.screen())

    override fun chat(message: Any) = Vanilla.chatHud().addMessage(if (message is Text) message else LiteralText(message.toString()))
}

private fun KeybindSettings.Context.isValid(s: Screen?) = when (this) {
    KeybindSettings.Context.INGAME -> s == null
    KeybindSettings.Context.GUI -> s != null
    KeybindSettings.Context.ANY -> true
}

fun showSubTitle(text: Text?) {
    Vanilla.inGameHud().setDefaultTitleFade()
    Vanilla.inGameHud().setSubtitle(text)
    Vanilla.inGameHud().setTitle(Text.of(" "))
}
