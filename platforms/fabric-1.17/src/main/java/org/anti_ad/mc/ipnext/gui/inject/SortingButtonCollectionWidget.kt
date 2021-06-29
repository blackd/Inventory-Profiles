package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.widget.Overflow
import org.anti_ad.mc.common.gui.widget.setBottomRight
import org.anti_ad.mc.common.gui.widget.setTopRight
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CreativeInventoryScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.*
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rDrawSprite
import org.anti_ad.mc.ipnext.config.ContinuousCraftingCheckboxValue.*
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(isInventoryTab)`
import org.anti_ad.mc.ipnext.inventory.ContainerType.*
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions

class SortingButtonCollectionWidget(val screen: ContainerScreen<*>) : Widget() {
    val TEXTURE = IdentifierHolder("inventoryprofilesnext",
                                   "textures/gui/gui_buttons.png")

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
    } // do nothing

    // try to render this as late as possible (but need to before tooltips render)
    fun postBackgroundRender(mouseX: Int,
                             mouseY: Int,
                             partialTicks: Float) {
        rStandardGlState()
        rClearDepth()
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
            tx = 10
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_REGULAR_SORT_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_button")
        }
        private val sortInColumnButton = SortButtonWidget { -> GeneralInventoryActions.doSortInColumns() }.apply {
            tx = 20
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_SORT_IN_COLUMNS_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_columns_button")
        }
        private val sortInRowButton = SortButtonWidget { -> GeneralInventoryActions.doSortInRows() }.apply {
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
                .filter { (_, keybind) -> keybind?.keyCodes?.isEmpty() != true }
                .joinToString("\n")
                { (suffix, keybind) ->
                    I18n.translate("$prefix.$suffix",
                                   keybind?.displayText?.toUpperCase())
                }
            // extra I18n.translate null is ok
        }
        private val moveAllToContainer = SortButtonWidget { -> GeneralInventoryActions.doMoveMatch(false) }.apply {
            tx = 50
            this@SortingButtonCollectionWidget.addChild(this)
            visible = moveAllVisible
            tooltipText = moveAllToolTip
        }
        private val moveAllToPlayer = SortButtonWidget { -> GeneralInventoryActions.doMoveMatch(true) }.apply {
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
                moveAllToContainer.setBottomRight(bottom + if (isPlayer) 12 else 0,
                                                  right)
                if (moveAllToPlayer.visible) {
                    moveAllToPlayer.setTopRight(top,
                                                right)
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
                            this.setTopRight(top,
                                             right)
                        } else {
                            this.setBottomRight(bottom,
                                                right)
                        }
                        right += 12
                    }
                }
            }
            // checkbox location
            if (types.contains(PLAYER)) {
                continuousCraftingCheckbox.setBottomRight(113,
                                                          31)
            } else {
                continuousCraftingCheckbox.setBottomRight(96,
                                                          81)
            }
        }
    }

    open inner class SortButtonWidget : TexturedButtonWidget {
        constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
        constructor(clickEvent: () -> Unit) : super(clickEvent)
        constructor() : super()

        var tx = 0
        var ty = 0
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
                                                                          mouseY) && tooltipText.isNotEmpty()
            ) {
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
    }
}