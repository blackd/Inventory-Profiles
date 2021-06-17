package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.extensions.detectable
import io.github.jsnimda.common.math2d.Point
import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.math2d.intersects
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.common.vanilla.alias.RenderSystem
import io.github.jsnimda.common.vanilla.render.*
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.SwitchType.HOLD
import io.github.jsnimda.inventoryprofiles.config.SwitchType.TOGGLE
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.parser.LockSlotsLoader
import net.minecraft.client.util.math.MatrixStack


/*
  slots ignored for:
    - clean cursor
    - move match / move crafting
    - sort
    - continuous crafting supplies storage
    - auto refill supplies storage
 */
object LockSlotsHandler {
  val lockedInvSlotsStoredValue = mutableSetOf<Int>() // locked invSlot list
  val enabled: Boolean
    get() = ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !ModSettings.LOCK_SLOTS_QUICK_DISABLE.isPressing()
  val lockedInvSlots: Iterable<Int>
    get() = if (enabled) lockedInvSlotsStoredValue else listOf()

  private val slotLocations: Map<Int, Point>
    get() {
      val screen = Vanilla.screen() as? ContainerScreen<*> ?: return mapOf()
      return Vanilla.container().`(slots)`.mapNotNull { slot ->
        val playerSlot = vPlayerSlotOf(slot, screen)
        return@mapNotNull if (playerSlot.`(inventory)` is PlayerInventory)
          playerSlot.`(invSlot)` to slot.`(topLeft)` else null
      }.toMap()
    }

  private var displayingConfig by detectable(false) { _, newValue ->
    if (!newValue) LockSlotsLoader.save() // save on close
  }

  // ============
  // render
  // ============
  private val TEXTURE = Identifier("inventoryprofiles", "textures/gui/overlay_new.png")
  private val backgroundSprite = Sprite(TEXTURE, Rectangle(40, 8, 32, 32))
  private val foregroundSprite: Sprite
    get() = backgroundSprite.right().down(ModSettings.LOCKED_SLOTS_FOREGROUND_STYLE.integerValue - 1)
  private val configSprite = backgroundSprite.left()
  private val configSpriteLocked = configSprite.down()

  fun onBackgroundRender() {
    if (displayingConfig) return
    if (!ModSettings.SHOW_LOCKED_SLOTS_BACKGROUND.booleanValue) return
    drawSprite(backgroundSprite, null)
  }

  fun onForegroundRender() {
    if (!enabled) return
    val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
    val matrixStack2: MatrixStack = RenderSystem.getModelViewStack()
    matrixStack2.push()  // see HandledScreen.render()
    //rMatrixStack = matrixStack2
    val topLeft = screen.`(containerBounds)`.topLeft
    matrixStack2.translate(-topLeft.x.toDouble(), -topLeft.y.toDouble(), 0.0)
    RenderSystem.applyModelViewMatrix()

    //gTranslatef(-topLeft.x.toFloat(), -topLeft.y.toFloat(), 0f)
    drawForeground()
    drawConfig()
    matrixStack2.pop() //gPopMatrix()
    RenderSystem.applyModelViewMatrix();
  }

  fun postRender() { // display config
  }

  private fun drawForeground() {
    if (!ModSettings.SHOW_LOCKED_SLOTS_FOREGROUND.booleanValue) return
    drawSprite(foregroundSprite, null)
  }

  private fun drawConfig() {
    if (!displayingConfig) return
    drawSprite(configSpriteLocked, configSprite)
  }

  private fun drawSprite(lockedSprite: Sprite?, openSprite: Sprite?) {
    if (!enabled) return
    val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
//    rClearDepth() // use translate or zOffset
    rDisableDepth()
    //RenderSystem.enableBlend()
    val topLeft = screen.`(containerBounds)`.topLeft + Point(8, 8) // slot center offset
    for ((invSlot, slotTopLeft) in slotLocations) {
      if (invSlot in lockedInvSlotsStoredValue)
        lockedSprite?.let { rDrawCenteredSprite(it, topLeft + slotTopLeft) }
      else
        openSprite?.let { rDrawCenteredSprite(it, topLeft + slotTopLeft) }
    }
    //RenderSystem.disableBlend()
    rEnableDepth()
  }

  // ============
  // input
  // ============

  var clicked = false
  var mode = 0 // 0 set lock slot 1 clear lock slot

  fun onTickInGame() {
    if (!enabled) return
    val screen = Vanilla.screen() as? ContainerScreen<*> ?: run {
      displayingConfig = false
      clicked = false
      return
    }
    if (clicked) {
      val line = MouseTracer.asLine
      val topLeft = screen.`(containerBounds)`.topLeft - Size(1, 1)
      for ((invSlot, slotTopLeft) in slotLocations) {
        if ((mode == 0) == (invSlot !in lockedInvSlotsStoredValue)
          && line.intersects(Rectangle(topLeft + slotTopLeft, Size(18, 18)))
        ) {
          if (mode == 0)
            lockedInvSlotsStoredValue.add(invSlot)
          else
            lockedInvSlotsStoredValue.remove(invSlot)
        }
      }
    }
  }

  fun onCancellableInput(): Boolean {
    if (!enabled) return false
    val screen = Vanilla.screen() as? ContainerScreen<*> ?: return false
    when (ModSettings.LOCK_SLOTS_CONFIG_SWITCH_TYPE.value) {
      TOGGLE -> if (ModSettings.LOCK_SLOTS_SWITCH_CONFIG_MODIFIER.isActivated()) displayingConfig = !displayingConfig
      HOLD -> displayingConfig = ModSettings.LOCK_SLOTS_SWITCH_CONFIG_MODIFIER.isPressing()
    }
    val currentClicked = (displayingConfig && ModSettings.LOCK_SLOTS_CONFIG_KEY.isPressing())
        || ModSettings.LOCK_SLOTS_QUICK_CONFIG_KEY.isPressing()
    if (currentClicked != clicked) {
      if (!currentClicked) {
        clicked = false
        return true
      } // else currentClicked == true
      val topLeft = screen.`(containerBounds)`.topLeft - Size(1, 1)
      // check if on slot
      val focused = slotLocations.asIterable().firstOrNull { (_, slotTopLeft) ->
        Rectangle(topLeft + slotTopLeft, Size(18, 18)).contains(MouseTracer.location)
      }
      focused?.let { (invSlot, _) ->
        clicked = true
        mode = if (invSlot in lockedInvSlotsStoredValue) 1 else 0
        return true
      }
    }
    return false
  }
}