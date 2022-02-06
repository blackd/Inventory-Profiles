package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.widget.fillParent
import org.anti_ad.mc.common.gui.widget.setTopLeft
import org.anti_ad.mc.common.gui.widgets.Hintable
import org.anti_ad.mc.common.integration.ButtonPositionHint
import org.anti_ad.mc.common.integration.HintClassData
import org.anti_ad.mc.common.integration.HintsManagerNG
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaScreenUtil
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.gui.GUIDEEditorScreen
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.ProfileButtonWidget

class EditorWidget(override val screen: ContainerScreen<*>,
                   private val hintsData: HintClassData = HintsManagerNG.getHints(screen.javaClass)): InsertableWidget(), Hintable {

    val targets = mutableListOf<InsertableWidget>()
    override var hints: ButtonPositionHint = hintsData.hintFor(IPNButton.SHOW_EDITOR)

    override var underManagement: Boolean = false

    override var hintManagementRenderer = Hintable.HintManagementRenderer(this)

    override val container = Vanilla.container()

    override fun postBackgroundRender(mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {

        rStandardGlState()
        rClearDepth()
        fillParent()

        //overflow = Overflow.VISIBLE
        absoluteBounds = Rectangle( 0,
                                    0,
                                    containerWidth,
                                    containerHeight)
        init()
        rehint()
        visible = GuiSettings.ENABLE_INVENTORY_EDITOR_BUTTON.value
        super.render(mouseX,
                     mouseY,
                     partialTicks)

        hintManagementRenderer.renderUnderManagement()

        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(absoluteBounds,
                         0xffff00.opaque)
        }
    }

    override fun postForegroundRender(mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float) {

    }



    var rehint = {}

    private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        InitWidgets().also { rehint = it::reHint }
        fillParent()

        //overflow = Overflow.VISIBLE
        absoluteBounds = Rectangle( 0,
                                    0,
                                    containerWidth,
                                    containerHeight)

    }

    inner class InitWidgets { // todo cleanup code


        private val showHideButton = ProfileButtonWidget { -> showEditorScreen() }.apply {
            tx = 160
            ty = 40
            hints = this@EditorWidget.hints
            this@EditorWidget.addChild(this)
            visible = GuiSettings.ENABLE_INVENTORY_EDITOR_BUTTON.value
            tooltipText = I18n.translate("inventoryprofiles.tooltip.editor_toggle")
            zIndex = 0
            hintableList.add(this)
        }

        fun reHint() {
            if (hints.top < -10) hints.top = -10
            if (hints.top > containerHeight - 20) hints.top = containerHeight - 20

            if (hints.horizontalOffset < -10) hints.horizontalOffset = -10
            if (hints.horizontalOffset > containerWidth - 20) hints.horizontalOffset = containerWidth - 20

            showHideButton.setTopLeft(10 + hints.top, 10 + hints.horizontalOffset)
        }

        init {
            reHint()
        }

    }

    fun showEditorScreen() {
        GUIDEEditorScreen(this.screen,
                          this.container,
                          targets).let {
            VanillaScreenUtil.openDistinctScreenQuiet(it)
        }
    }

    fun addHintable(insertable: InsertableWidget) = targets.add(insertable)

}