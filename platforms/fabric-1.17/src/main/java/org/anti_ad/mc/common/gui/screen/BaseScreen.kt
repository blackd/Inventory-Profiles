package org.anti_ad.mc.common.gui.screen

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.gui.widgets.RootWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.VanillaScreenUtil
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.rMatrixStack
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState

// ============
// vanillamapping code depends on mappings (package org.anti_ad.mc.common.gui.screen)
// ============

abstract class BaseScreen(text: Text) : Screen(text) {
    constructor() : this(LiteralText(""))

    var isClosing: Boolean = false

    var parent: Screen? = null
    val titleString: String
        get() = this.title.string // todo .asFormattedString()
    open val screenInfo
        get() = ScreenInfo.default

    open fun closeScreen() {
        this.isClosing = true
        VanillaScreenUtil.openScreenNullable(parent)
        this.isClosing = false
    }

    fun hasParent(screen: Screen): Boolean {
        val parents = mutableSetOf<BaseScreen>()
        var currentParent = this
        while (currentParent != screen) {
            parents.add(currentParent)
            currentParent = (currentParent.parent as? BaseScreen) ?: return (currentParent.parent == screen)
            if (currentParent in parents) { // loop
                return false
            }
        }
        return true
    }

    // ============
    // widget
    // ============
    val rootWidget = RootWidget()
    fun addWidget(widget: Widget) {
        rootWidget.addChild(widget)
    }

    fun dumpWidgetTree() {
        rootWidget.dumpWidgetTree()
    }

    fun clearWidgets() {
        rootWidget.clearChildren()
    }

    // ============
    // render
    // ============
    open fun renderWidgetPre(mouseX: Int,
                             mouseY: Int,
                             partialTicks: Float) {
        rStandardGlState()
        rClearDepth()
    }

    open fun render(mouseX: Int,
                    mouseY: Int,
                    partialTicks: Float) {
        renderWidgetPre(mouseX,
                        mouseY,
                        partialTicks)
        rootWidget.render(mouseX,
                          mouseY,
                          partialTicks)
    }

    override fun render(matrixStack: MatrixStack?,
                        mouseX: Int,
                        mouseY: Int,
                        delta: Float) {
        //rMatrixStack = matrixStack ?: MatrixStack().also { Log.debug("null matrixStack") }
        rMatrixStack = matrixStack ?: RenderSystem.getModelViewStack().also { Log.debug("null matrixStack") }
        render(mouseX,
               mouseY,
               delta)
    }

    // ============
    // vanilla overrides
    // ============
    final override fun isPauseScreen(): Boolean = screenInfo.isPauseScreen
    final override fun onClose() {
        if (!isClosing) {
            closeScreen()
        }
        isClosing = false
    }

    // ============
    // event delegates
    // ============
    override fun resize(minecraftClient: MinecraftClient,
                        width: Int,
                        height: Int) {
        super.resize(minecraftClient,
                     width,
                     height)
        rootWidget.size = Size(width,
                               height)
    }

    override fun mouseClicked(d: Double,
                              e: Double,
                              i: Int): Boolean =
        rootWidget.mouseClicked(d.toInt(),
                                e.toInt(),
                                i)

    override fun mouseReleased(d: Double,
                               e: Double,
                               i: Int): Boolean =
        rootWidget.mouseReleased(d.toInt(),
                                 e.toInt(),
                                 i)

    override fun mouseDragged(d: Double,
                              e: Double,
                              i: Int,
                              f: Double,
                              g: Double): Boolean =
        rootWidget.mouseDragged(d,
                                e,
                                i,
                                f,
                                g) // fixme fix dx dy decimal rounding off

    override fun mouseScrolled(d: Double,
                               e: Double,
                               f: Double): Boolean =
        rootWidget.mouseScrolled(d.toInt(),
                                 e.toInt(),
                                 f)

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean =
        super.keyPressed(keyCode,
                         scanCode,
                         modifiers) || rootWidget.keyPressed(keyCode,
                                                             scanCode,
                                                             modifiers)

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean =
        rootWidget.keyReleased(keyCode,
                               scanCode,
                               modifiers)

    override fun charTyped(charIn: Char,
                           modifiers: Int): Boolean =
        rootWidget.charTyped(charIn,
                             modifiers)
}