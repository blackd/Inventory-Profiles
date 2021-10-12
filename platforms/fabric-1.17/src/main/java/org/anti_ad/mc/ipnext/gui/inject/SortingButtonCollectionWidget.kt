package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.widget.Axis
import org.anti_ad.mc.common.gui.widget.BiFlex
import org.anti_ad.mc.common.gui.widget.Overflow
import org.anti_ad.mc.common.gui.widget.setBottomLeft
import org.anti_ad.mc.common.gui.widget.setBottomRight
import org.anti_ad.mc.common.gui.widget.setTopRight
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.integration.HintsManager
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CreativeInventoryScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rDrawSprite
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.ContinuousCraftingCheckboxValue.*
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.event.ProfileSwitchHandler
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(isInventoryTab)`
import org.anti_ad.mc.ipnext.inventory.ContainerType.*
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import java.util.*

private val TEXTURE = IdentifierHolder("inventoryprofilesnext",
                               "textures/gui/gui_buttons.png")


abstract class InsertableWidget: Widget() {

    abstract fun postBackgroundRender(mouseX: Int,
                             mouseY: Int,
                             partialTicks: Float);

    abstract val screen: ContainerScreen<*>

}

class PlayerUICollectionWidget(override val screen: ContainerScreen<*>): InsertableWidget() {



    override fun postBackgroundRender(mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {

        rStandardGlState()
        rClearDepth()
        //overflow = Overflow.VISIBLE
        val parentBounds = screen.`(containerBounds)`
        absoluteBounds = parentBounds.copy(y = parentBounds.bottom + 3, height = 20)
        init()
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(absoluteBounds.inflated(1),
                         0xffff00.opaque)
        }
        //    Tooltips.renderAll()
    }

    private var initialized = false
    fun init() {
        if (initialized) return
        initialized = true
        InitWidgets()
    }

    inner class InitWidgets { // todo cleanup code
        val container = Vanilla.container()
        val types = ContainerTypes.getTypes(container)

        private val nextProfileButton = ProfileButtonWidget(ProfileSwitchHandler::nextProfile).apply {
            tx = 50
            ty = 20
            this@PlayerUICollectionWidget.addChild(this)
            visible = types.contains(PLAYER)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.next_profile_button")
        }

        private val prevProfileButton = ProfileButtonWidget(ProfileSwitchHandler::prevProfile).apply {
            tx = 60
            ty = 20
            this@PlayerUICollectionWidget.addChild(this)
            visible = types.contains(PLAYER)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.prev_profile_button")
        }

        private val profileButton = ActiveProfileButtonWidget(ProfileSwitchHandler::applyCurrent).apply {
            //"show something!"
            //this.width = 10
            //this@PlayerUICollectionWidget.addChild(this)
            parent = this@PlayerUICollectionWidget
            val profile = getCurrentProfileName()
            visible = types.contains(PLAYER)
            this.text = profile
            height = 15
            top = 1
            tooltipText = I18n.translate("inventoryprofiles.tooltip.apply_profile_button")

        }

        private val flex = InnerFlex().apply {
            parent = this@PlayerUICollectionWidget
            visible = types.contains(PLAYER)
            absoluteBounds = this@PlayerUICollectionWidget.absoluteBounds.copy(width = this@PlayerUICollectionWidget.absoluteBounds.width - 30,
                                                                               x = this@PlayerUICollectionWidget.absoluteBounds.x + 15,
                                                                               height = 17)
        }

        init {
            //flex.addAndFit(prevProfileButton)
            //flex.flex.normal.addSpace(10)
            flex.flex.addAndFit(profileButton)
            //flex.addAndFit(nextProfileButton)
            prevProfileButton.setBottomLeft(7, 0)
            nextProfileButton.setBottomRight(7, 0)
            //profileButton.setBottomLeft(0, 20)
        }
    }

    private fun getCurrentProfileName(): String {
        return ProfileSwitchHandler.activeProfileName ?: "§cNONE§r"
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

class SortingButtonCollectionWidget(override val screen: ContainerScreen<*>) : InsertableWidget() {

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
    } // do nothing

    // try to render this as late as possible (but need to before tooltips render)
    override fun postBackgroundRender(mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {
        rStandardGlState()
        rClearDepth()
        overflow = Overflow.VISIBLE
        absoluteBounds = screen.`(containerBounds)`
        init()
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(absoluteBounds.inflated(1),
                         0xffff00.opaque)
        }
//    Tooltips.renderAll()
    }

    var initialized = false
    fun init() {
        if (initialized) return
        initialized = true
        InitWidgets()
    }

    init {
        overflow = Overflow.VISIBLE
    }

    inner class InitWidgets { // todo cleanup code
        val container = Vanilla.container()
        val types = ContainerTypes.getTypes(container)

        val dummyRenderUpdater = object : Widget() { // update buttons in CreativeInventoryScreen
            init {
                this@SortingButtonCollectionWidget.addChild(this)
            }

            val buttons by lazy(LazyThreadSafetyMode.NONE) {
                listOf(sortButton,
                       sortInColumnButton,
                       sortInRowButton)
            }
            val originalVisibles by lazy(LazyThreadSafetyMode.NONE) { buttons.map { it.visible } }
            override fun render(mouseX: Int,
                                mouseY: Int,
                                partialTicks: Float) {
                if (screen !is CreativeInventoryScreen) return
                val visible = screen.`(isInventoryTab)`
                buttons.forEachIndexed { index, button ->
                    button.visible = originalVisibles[index] && visible
                }
            }
        }

        val addChestSide = types.contains(SORTABLE_STORAGE)
        val addNonChestSide = types.contains(PURE_BACKPACK)
        val shouldAdd = addChestSide || addNonChestSide
        private val sortButton = SortButtonWidget { -> GeneralInventoryActions.doSort() }.apply {
            hints = HintsManager.hintFor(IPNButton.SORT, screen.javaClass)
            tx = 10
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_REGULAR_SORT_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_button")
        }
        private val sortInColumnButton = SortButtonWidget { -> GeneralInventoryActions.doSortInColumns() }.apply {
            hints = HintsManager.hintFor(IPNButton.SORT_COLUMNS, screen.javaClass)
            tx = 20
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_SORT_IN_COLUMNS_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_columns_button")
        }
        private val sortInRowButton = SortButtonWidget { -> GeneralInventoryActions.doSortInRows() }.apply {
            hints = HintsManager.hintFor(IPNButton.SORT_ROWS, screen.javaClass)
            tx = 30
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_SORT_IN_ROWS_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_rows_button")
        }



        val moveAllVisible = GuiSettings.SHOW_MOVE_ALL_BUTTON.booleanValue &&
                types.containsAny(setOf(SORTABLE_STORAGE,
                                        NO_SORTING_STORAGE,
                                        CRAFTING))
        val moveAllToolTip: String = with(ModSettings) {
            val prefix = "inventoryprofiles.tooltip.move_all_button"
            val line1 = if (ALWAYS_MOVE_ALL.booleanValue) "title_move_all" else "title_move_matching"
            val line2 = if (ALWAYS_INCLUDE_HOTBAR.booleanValue) "exclude_hotbar" else "include_hotbar"
            val line3 = if (ALWAYS_MOVE_ALL.booleanValue) "move_matching_only" else "move_all"
            val key2 = INCLUDE_HOTBAR_MODIFIER.mainKeybind
            val key3 = MOVE_ALL_MODIFIER.mainKeybind
            return@with listOf(line1 to null,
                               line2 to key2,
                               line3 to key3)
                .filter {
                        (_, keybind) -> keybind?.keyCodes?.isEmpty() != true
                }.joinToString("\n") { (suffix, keybind) ->
                    I18n.translate("$prefix.$suffix",
                                   keybind?.displayText?.uppercase(Locale.getDefault()))
                }
            // extra I18n.translate null is ok
        }
        private val moveAllToContainer = SortButtonWidget { -> GeneralInventoryActions.doMoveMatch(false) }.apply {
            hints = HintsManager.hintFor(IPNButton.MOVE_TO_CONTAINER, screen.javaClass)
            tx = 50
            this@SortingButtonCollectionWidget.addChild(this)
            visible = moveAllVisible
            tooltipText = moveAllToolTip
        }

        private val moveAllToPlayer = SortButtonWidget { -> GeneralInventoryActions.doMoveMatch(true) }.apply {
            hints = HintsManager.hintFor(IPNButton.MOVE_TO_PLAYER, screen.javaClass)
            tx = 60
            this@SortingButtonCollectionWidget.addChild(this)
            visible = moveAllVisible && !types.contains(CRAFTING)
            tooltipText = moveAllToolTip
        }

        // ============
        // continuous crafting
        // ============
        fun updateConfigValue(newValue: Boolean) {
            GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.value = newValue
            SaveLoadManager.save() // todo save when onClose instead of every time check box value change
        }

        var continuousCraftingValue
                by detectable(GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue) { _, newValue ->
                    updateConfigValue(newValue)
                }

        init {
            continuousCraftingValue = when (GuiSettings.CONTINUOUS_CRAFTING_CHECKBOX_VALUE.value) {
                REMEMBER -> GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue
                CHECKED -> true
                UNCHECKED -> false
            }
        }

        private val continuousCraftingCheckbox = SortButtonWidget { -> switchContinuousCraftingValue() }.apply {
//      tx = 70 or 80
            tx = if (continuousCraftingValue) 80 else 70
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue && types.contains(CRAFTING)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.continuous_crafting_checkbox")
        }

        fun switchContinuousCraftingValue() {
            continuousCraftingValue = !continuousCraftingValue
            continuousCraftingCheckbox.tx = if (continuousCraftingValue) 80 else 70
        }

        init {
            // right = 7, each + 12
            val bottom = 85
            val top = 5
            var right = 7
            if (types.contains(CREATIVE)) {
                right += 18
            }
            // move all location
            if (moveAllVisible) {
                val isPlayer = types.contains(PLAYER)
                moveAllToContainer.setBottomRight(bottom + (if (isPlayer) 12 else 0) + moveAllToContainer.hints.bottom() ,
                                                  right + moveAllToContainer.hints.right())
                if (moveAllToPlayer.visible) {
                    moveAllToPlayer.setTopRight(top + moveAllToPlayer.hints.top(),
                                                right + moveAllToPlayer.hints.right())
                }
                if (!isPlayer) { // player _| shape
                    right += 12
                }
            }
            // sort buttons location
            listOf(sortInRowButton,
                   sortInColumnButton,
                   sortButton).forEach { button ->
                with(button) {
                    if (visible) {
                        if (addChestSide) {
                            this.setTopRight(top + hints.top(),
                                             right + hints.right())
                        } else {
                            this.setBottomRight(bottom + hints.bottom(),
                                                right + hints.right())
                        }
                        right += 12
                    }
                }
            }

            // checkbox location
            if (types.contains(PLAYER)) {
                continuousCraftingCheckbox.setBottomRight(113 + continuousCraftingCheckbox.hints.bottom(),
                                                          31 + continuousCraftingCheckbox.hints.right())
            } else {
                continuousCraftingCheckbox.setBottomRight(96 + continuousCraftingCheckbox.hints.bottom(),
                                                          81 + continuousCraftingCheckbox.hints.right())
            }
        }
    }
}

open class SortButtonWidget : TexturedButtonWidget {
    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    var tx = 0
    var ty = 0
    var hints = HintsManager.zeroZero
    var tooltipText: String = ""
    override val texture: IdentifierHolder
        get() = TEXTURE
    override val texturePt: Point
        get() = Point(tx,
                      ty)
    override val hoveringTexturePt: Point
        get() = Point(tx,
                      ty + 10)

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

    init {
        size = Size(10,
                    10)
    }
}

abstract class TexturedButtonWidget : ButtonWidget {
    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    abstract val texture: IdentifierHolder
    abstract val texturePt: Point
    abstract val hoveringTexturePt: Point

    override fun renderButton(hovered: Boolean) {
        val textureLocation = if (hovered) hoveringTexturePt else texturePt
        rDrawSprite(Sprite(texture,
                           Rectangle(textureLocation,
                                     size)),
                    screenX,
                    screenY)
    }

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        return super.mouseClicked(x,y,button) && visible
    }

}

private open  class ProfileButtonWidget: SortButtonWidget {
    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        return super.mouseClicked(x,y,button) && visible
    }
}

internal fun Triple<Int, Int, Int>.right(): Int = this.first
internal fun Triple<Int, Int, Int>.top(): Int = this.second
internal fun Triple<Int, Int, Int>.bottom(): Int = this.third
