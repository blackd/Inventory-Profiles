package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.widget.Axis
import org.anti_ad.mc.common.gui.widget.BiFlex
import org.anti_ad.mc.common.gui.widget.setBottomLeft
import org.anti_ad.mc.common.gui.widget.setBottomRight
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.Hintable
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.integration.ButtonPositionHint
import org.anti_ad.mc.common.integration.HintClassData
import org.anti_ad.mc.common.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.event.ProfileSwitchHandler
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.ProfileButtonWidget
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes

class ProfilesUICollectionWidget(override val screen: ContainerScreen<*>,
                                 private val hintsData: HintClassData = HintsManagerNG.getHints(screen.javaClass)): InsertableWidget(), Hintable {

    override var hints: ButtonPositionHint = hintsData.hintFor(IPNButton.PROFILE_SELECTOR)
    override var hintManagementRenderer = Hintable.HintManagementRenderer(this)
    override var underManagement: Boolean = false
    override val container = Vanilla.container()
    private val types = ContainerTypes.getTypes(container)

    private var initialized = false

    init {
        visible = types.contains(ContainerType.PLAYER)
    }

    override fun postBackgroundRender(mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {

        rStandardGlState()
        rClearDepth()
        //overflow = Overflow.VISIBLE
        val parentBounds = screen.`(containerBounds)`
        absoluteBounds = parentBounds.copy(y = parentBounds.bottom + 3 + hints.bottom,
                                           x = parentBounds.x + hints.horizontalOffset,
                                           height = 20)
        init()
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(absoluteBounds.inflated(1),
                         0xffff00.opaque)
        }

        hintManagementRenderer.renderUnderManagement()

    }

    override fun postForegroundRender(mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float) {

    }

    override fun moveUp(step: Int) {
        hints.bottom = hints.bottom - step
    }

    override fun moveDown(step: Int) {
        hints.bottom = hints.bottom + step
    }

    private fun getCurrentProfileName(): String {
        return ProfileSwitchHandler.activeProfileName ?: "§cNONE§r"
    }

    fun init() {

        if (initialized) return

        initialized = true
        InitWidgets()
        hintableList.add(this);
    }

    inner class InitWidgets { // todo cleanup code

        private val nextProfileButton = ProfileButtonWidget { -> ProfileSwitchHandler.nextProfile(true) }.apply {
            tx = 50
            ty = 20
            this@ProfilesUICollectionWidget.addChild(this)
            visible = types.contains(ContainerType.PLAYER)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.next_profile_button")
        }

        private val prevProfileButton = ProfileButtonWidget { -> ProfileSwitchHandler.prevProfile(true) }.apply {
            tx = 60
            ty = 20
            this@ProfilesUICollectionWidget.addChild(this)
            visible = types.contains(ContainerType.PLAYER)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.prev_profile_button")
        }

        private val profileButton = ActiveProfileButtonWidget { ProfileSwitchHandler.applyCurrent(true) }.apply {

            parent = this@ProfilesUICollectionWidget
            val profile = getCurrentProfileName()
            visible = types.contains(ContainerType.PLAYER)
            this.text = profile
            height = 15
            top = 1
            tooltipText = I18n.translate("inventoryprofiles.tooltip.apply_profile_button")

        }

        private val flex = InnerFlex().apply {
            parent = this@ProfilesUICollectionWidget
            visible = types.contains(ContainerType.PLAYER)
            absoluteBounds = this@ProfilesUICollectionWidget.absoluteBounds.copy(width = this@ProfilesUICollectionWidget.absoluteBounds.width - 30,
                                                                                 x = this@ProfilesUICollectionWidget.absoluteBounds.x + 15,
                                                                                 height = 17)
        }

        init {
            flex.flex.addAndFit(profileButton)
            prevProfileButton.setBottomLeft(7, 0)
            nextProfileButton.setBottomRight(7, 0)
        }
    }

    inner class InnerFlex(): Widget() {
        val flex = BiFlex(this,
                          Axis.HORIZONTAL)

    }

    inner class ActiveProfileButtonWidget(onClick: () -> Unit): ButtonWidget(onClick) {
        override var text: String
            get() {
                return getCurrentProfileName()
            }
            set(_) {}
        var tooltipText: String = ""
        override fun render(mouseX: Int,
                            mouseY: Int,
                            partialTicks: Float) {
            super.render(mouseX,
                         mouseY,
                         partialTicks)
            if (GuiSettings.SHOW_BUTTON_TOOLTIPS.booleanValue && contains(mouseX,
                                                                          mouseY) && tooltipText.isNotEmpty()) {
                Tooltips.addTooltip(tooltipText,
                                    mouseX,
                                    mouseY)
            }
        }
        override fun mouseClicked(x: Int,
                                  y: Int,
                                  button: Int): Boolean {
            return super.mouseClicked(x,y,button) && visible
        }
    }
}