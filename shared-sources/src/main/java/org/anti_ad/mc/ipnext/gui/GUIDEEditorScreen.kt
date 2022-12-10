/*
 * Inventory Profiles Next
 *
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

package org.anti_ad.mc.ipnext.gui

import org.anti_ad.mc.common.config.IConfigOption
import org.anti_ad.mc.common.config.options.ConfigBoolean
import org.anti_ad.mc.common.gui.screen.BaseOverlay
import org.anti_ad.mc.common.gui.layout.Overflow
import org.anti_ad.mc.common.gui.layout.fillParent
import org.anti_ad.mc.common.gui.layout.setBottomLeft
import org.anti_ad.mc.common.gui.layout.setTopRight
import org.anti_ad.mc.common.gui.widgets.ConfigListWidget
import org.anti_ad.mc.ipnext.gui.widgets.Hintable
import org.anti_ad.mc.common.gui.widgets.HudLabeledText
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.input.KeyCodes.KEY_A
import org.anti_ad.mc.common.input.KeyCodes.KEY_D
import org.anti_ad.mc.common.input.KeyCodes.KEY_DOWN
import org.anti_ad.mc.common.input.KeyCodes.KEY_LEFT
import org.anti_ad.mc.common.input.KeyCodes.KEY_RIGHT
import org.anti_ad.mc.common.input.KeyCodes.KEY_S
import org.anti_ad.mc.common.input.KeyCodes.KEY_SPACE
import org.anti_ad.mc.common.input.KeyCodes.KEY_UP
import org.anti_ad.mc.common.input.KeyCodes.KEY_W
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.MainKeybind
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.alias.vanillaContainers
import org.anti_ad.mc.common.vanilla.alias.vanillaScreens
import org.anti_ad.mc.common.vanilla.render.COLOR_WHITE
import org.anti_ad.mc.common.vanilla.render.rScreenHeight
import org.anti_ad.mc.common.vanilla.render.rScreenWidth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawHorizontalLine
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rDrawVerticalLine
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes


class GUIDEEditorScreen(private val target: Screen,
                        private val container: Container,
                        targets: MutableList<InsertableWidget>): BaseOverlay() {

    companion object {

        private const val TRANSLATE_ROOT = "inventoryprofiles.gui.editor"
        const val NAME_ROOT = "$TRANSLATE_ROOT.name"
        const val DESCRIPTION_ROOT = "$TRANSLATE_ROOT.description"
        const val CATEGORY_ROOT = "$TRANSLATE_ROOT.category"

        private fun MutableList<Hintable>.whenAtLeastOneVisible(function: () -> Unit) {
            if (this.find { it.isVisible } != null) {
                function()
            }
        }

        private val Hintable?.isVisible: Boolean
            get() {
                if (this != null && this is Widget) return this.visible
                return false
            }

        val SHIFT = MainKeybind("LEFT_SHIFT", KeybindSettings.GUI_EXTRA)
    }

    private var selectedIndex: Int = 0
    private var activeHintable: Hintable? = null
    private var allHintable = mutableListOf<Hintable>()

    private var leftLongPress: Int = 0
    private var rightLongPress: Int = 0
    private var upLongPress: Int = 0
    private var downLongPress: Int = 0

    private val screenHints = HintsManagerNG.getHints(target.javaClass)
    private val containerHints = HintsManagerNG.getHints(container.javaClass)
    private val isVanillaContainer = vanillaContainers.contains(container.javaClass)
    private val isVanillaScreen = vanillaScreens.contains(target.javaClass)

    private val infoStrings = mutableListOf(("$NAME_ROOT.class.screen" to target.javaClass.name),
                                            ("$NAME_ROOT.class.container" to container.javaClass.name))

    private val helpStrings = mutableListOf(
        "help.select.prev",
        "help.select.next",
        "help.move.speed",
        "help.move.keys",
                                           )

    inner class EditorConfigBoolean(override var key: String,
                                    override var hidden: Boolean,
                                    default: Boolean,
                                    currentValue: Boolean,
                                    val valueSetEvent: (Boolean) -> Unit): ConfigBoolean(default) {

        override var value: Boolean = currentValue
            set(value) {
                field = value
                valueSetEvent(value)
            }

        init {
            value = currentValue
            importance = IConfigOption.Importance.NORMAL
        }
    }

    inner class Options {

        val screenIgnoreOption = EditorConfigBoolean("screen.ignore",
                                                     isVanillaScreen,
                                                     false,
                                                     screenHints.ignore) {
            if (screenHints.ignore != it) {
                screenHints.markAsDirty()
            }
            screenHints.ignore = it
        }

        val screenPlayerSideOption = EditorConfigBoolean("screen.player_side",
                                                         isVanillaScreen,
                                                         false,
                                                         screenHints.playerSideOnly) {
            if (screenHints.playerSideOnly != it) {
                screenHints.markAsDirty()
            }
            screenHints.playerSideOnly = it

        }

        val screenDisableSwipe = EditorConfigBoolean("screen.disable_fast_swipe",
                                                         isVanillaScreen,
                                                         false,
                                                         screenHints.disableFastSwipe) {
            if (screenHints.disableFastSwipe != it) {
                screenHints.markAsDirty()
            }
            screenHints.disableFastSwipe = it

        }

        val screenForceOption = EditorConfigBoolean("screen.force",
                                                    isVanillaScreen,
                                                    false,
                                                    screenHints.force) {
            if (screenHints.force != it) {
                screenHints.markAsDirty()
            }
            screenHints.force = it
        }

        val screenShowProfileSelectorOption = EditorConfigBoolean("screen.profile",
                                                                  isVanillaScreen,
                                                                  true,
                                                                  !screenHints.hintFor(IPNButton.PROFILE_SELECTOR).hide) {
            if (screenHints.hintFor(IPNButton.PROFILE_SELECTOR).hide == it) {
                screenHints.markAsDirty()
            }
            screenHints.hintFor(IPNButton.PROFILE_SELECTOR).hide = !it
        }

        val containerIgnoreOption = EditorConfigBoolean("screen.ignore",
                                                        isVanillaContainer,
                                                        false,
                                                        containerHints.ignore) {
            if (containerHints.ignore != it) {
                containerHints.markAsDirty()
            }
            containerHints.ignore = it
            ContainerTypes.deregister(container.javaClass)
        }

        val containerPlayerSideOption = EditorConfigBoolean("screen.player_side",
                                                            isVanillaContainer,
                                                            false,
                                                            containerHints.playerSideOnly) {
            if (containerHints.playerSideOnly != it) {
                containerHints.markAsDirty()
            }
            containerHints.playerSideOnly = it
            ContainerTypes.deregister(container.javaClass)
        }

        val containerForceOption = EditorConfigBoolean("screen.force",
                                                       isVanillaContainer,
                                                       false,
                                                       containerHints.force) {
            if (containerHints.force != it) {
                containerHints.markAsDirty()
            }
            containerHints.force = it
            ContainerTypes.deregister(container.javaClass)
        }

    }

    init {
        rootWidget.fillParent()

        targets.forEach {
            allHintable.addAll(it.hintableList)
        }
        selectWidgets()
        var nextTop = 5
        val translateOrElse: (String) -> String = { I18n.translateOrElse(it) { it } }

        infoStrings.forEach {
            addWidget(HudLabeledText(translateOrElse(it.first), it.second).apply {
                setTopRight(nextTop, 5)
                nextTop += 10
            })
        }

        var nextBottom = 5

        helpStrings.forEach {
            val label = translateOrElse("$NAME_ROOT.$it")
            val text = translateOrElse("$DESCRIPTION_ROOT.$it")
            addWidget(HudLabeledText(label, text).apply {
                setBottomLeft(nextBottom, 5)
                nextBottom += 10
            })
        }

        nextTop += 3


        ConfigListWidget({ I18n.translateOrElse("$NAME_ROOT.$it") { it } },
                         { I18n.translateOrEmpty("$DESCRIPTION_ROOT.$it") },
                         3).apply {
            height = rScreenHeight - nextTop - 5
            val targetBounds = (target as ContainerScreen<*>).`(containerBounds)`
            width = rScreenWidth - targetBounds.width - targetBounds.x - 3
            this@GUIDEEditorScreen.addWidget(this)
            overflow = Overflow.VISIBLE

            rootWidget.sizeChanged += {
                val bounds = target.`(containerBounds)`
                height = rScreenHeight - nextTop - 5
                width = rScreenWidth - bounds.width - bounds.x - 3
                setTopRight(nextTop, 0)
            }
            anchorHeader.visible = false
            container.scrollWheelAmount = 14
            renderBorder = false
            setTopRight(nextTop, 0)
            with(Options()) {
                val types = ContainerTypes.getTypes(this@GUIDEEditorScreen.container)
                addCategory(I18n.translateOrElse("$CATEGORY_ROOT.screen") { "Screen" })
                addConfigOption(screenIgnoreOption)
                addConfigOption(screenPlayerSideOption)
                addConfigOption(screenDisableSwipe)
                addConfigOption(screenForceOption)
                if (types.contains(ContainerType.PLAYER)) {
                    addConfigOption(screenShowProfileSelectorOption)
                }
                addCategory(I18n.translateOrElse("$CATEGORY_ROOT.container") { "Container" })
                addConfigOption(containerIgnoreOption)
                addConfigOption(containerPlayerSideOption)
                addConfigOption(containerForceOption)
            }
        }




        addWidget(object: Widget() {
            init {
                zIndex = 0
            }

            override fun render(mouseX: Int,
                                mouseY: Int,
                                partialTicks: Float) {
                fillParent()
                val color = COLOR_WHITE
                rDrawVerticalLine(mouseX,
                                  1,
                                  height - 2,
                                  color)
                rDrawHorizontalLine(1,
                                    width - 2,
                                    mouseY,
                                    color)
            }
        })

    }

    override fun onTick() {
        updateHints()
    }

    private fun updateHints(threshold: Int = 10) {
        if (leftLongPress in 1..9) {
            leftLongPress++
        }
        if (rightLongPress in 1..9) {
            rightLongPress++
        }
        if (upLongPress in 1..9) {
            upLongPress++
        }
        if (downLongPress in 1..9) {
            downLongPress++
        }
        val step = if (SHIFT.isPressing()) 10 else 1

        if (leftLongPress >= threshold) {
            activeHintable?.moveLeft(step)
        }
        if (rightLongPress >= threshold) {
            activeHintable?.moveRight(step)
        }
        if (upLongPress >= threshold) {
            activeHintable?.moveUp(step)
        }
        if (downLongPress >= threshold) {
            activeHintable?.moveDown(step)
        }
    }

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean {
        val res = super.keyReleased(keyCode, scanCode, modifiers)
        if (!res) {
            val step = if (SHIFT.isPressing()) 10 else 1
            when (keyCode) {

                KEY_SPACE        -> {
                    selectWidgets(!SHIFT.isPressing())
                }
                KEY_LEFT, KEY_A  -> {
                    activeHintable?.moveLeft(step)
                    leftLongPress = 0
                }
                KEY_RIGHT, KEY_D -> {
                    activeHintable?.moveRight(step)
                    rightLongPress = 0
                }
                KEY_UP, KEY_W    -> {
                    activeHintable?.moveUp(step)
                    upLongPress = 0
                }
                KEY_DOWN, KEY_S  -> {
                    activeHintable?.moveDown(step)
                    downLongPress = 0
                }
                else             -> {
                    return false
                }
            }
        }
        return res
    }

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean {
        val res = super.keyPressed(keyCode, scanCode, modifiers)
        if (!res) {
            when (keyCode) {
                KEY_LEFT, KEY_A  -> {
                    if (leftLongPress <= 0) leftLongPress = 1
                }
                KEY_RIGHT, KEY_D -> {
                    if (rightLongPress <= 0) rightLongPress = 1
                }
                KEY_UP, KEY_W    -> {
                    if (upLongPress <= 0) upLongPress = 1
                }
                KEY_DOWN, KEY_S  -> {
                    if (downLongPress <= 0) downLongPress = 1
                }
                else             -> {
                    return false
                }
            }
        }
        return res
    }

    private fun selectWidgets(forward: Boolean = true) {
        activeHintable?.underManagement = false
        allHintable.whenAtLeastOneVisible {
            if (!forward) {
                do {
                    selectedIndex--
                    if (selectedIndex <= 0) {
                        selectedIndex = allHintable.size - 1
                    }
                    activeHintable = allHintable[selectedIndex]
                } while (!activeHintable.isVisible)
            } else {
                do {
                    selectedIndex++
                    if (selectedIndex >= allHintable.size) {
                        selectedIndex = 0
                    }
                    activeHintable = allHintable[selectedIndex]
                } while (!activeHintable.isVisible)
            }
        }
        activeHintable?.underManagement = true
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks)

        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(rootWidget.absoluteBounds,
                         0xff00FF.opaque)
        }
    }

    override fun closeScreen() {
        if (screenHints.dirty() || containerHints.dirty() || screenHints.areButtonsMoved()) {
            var screenId = screenHints.readId()
            var containerId = containerHints.readId()
            if (screenId == null) {
                screenId = containerId ?: "player-defined"
            }

            if (containerId == null) {
                @Suppress("USELESS_ELVIS")
                containerId = screenId ?: "player-defined"
            }

            screenHints.changeId(screenId)
            containerHints.changeId(containerId)

            HintsManagerNG.saveDirty(screenHints, containerHints)
            ContainerTypes.reset()
        }
        super.closeScreen()
    }

}
