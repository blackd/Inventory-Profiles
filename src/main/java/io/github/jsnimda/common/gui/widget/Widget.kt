package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.util.event
import io.github.jsnimda.common.gui.Point
import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.widget.Overflow.*
import io.github.jsnimda.common.vanilla.VanillaRender
import kotlin.properties.Delegates

fun Widget.moveToCenter() {
  parent?.let { parent ->
    location = Point((parent.width - width) / 2, (parent.height - height) / 2)
  }
}

open class Widget {

  internal var _parent: Widget? = null
  var parent: Widget?
    get() = _parent
    set(value) {
      value?.widgets?.add(this) ?: _parent?.widgets?.remove(this)
    }

  val widgets by lazy { WidgetCollection(this) }

  //region Positioning

  protected val containerWidth
    get() = parent?.width ?: VanillaRender.screenWidth
  protected val containerHeight
    get() = parent?.height ?: VanillaRender.screenHeight
  protected val containerScreenX
    get() = parent?.screenX ?: 0
  protected val containerScreenY
    get() = parent?.screenY ?: 0

  var left: Int
    get() = location.x
    set(value) = if (anchor.right) { // ref: right
      bounds = bounds.copy(x = value, width = left + width - value)
    } else {
      location = location.copy(x = value)
    }
  var top: Int
    get() = location.y
    set(value) = if (anchor.bottom) { // ref: bottom
      bounds = bounds.copy(y = value, height = top + height - value)
    } else {
      location = location.copy(y = value)
    }
  var width: Int
    get() = size.width
    set(value) { // TODO anchor dependency?
      size = size.copy(width = value)
    }
  var height: Int
    get() = size.height
    set(value) { // TODO anchor dependency?
      size = size.copy(height = value)
    }

  var location by Delegates.observable(Point(0, 0)) { _, oldValue, newValue ->
    if (oldValue != newValue) locationChanged(oldValue, newValue)
  }
  var size by Delegates.observable(Size(0, 0)) { _, oldValue, newValue ->
    if (oldValue != newValue) sizeChanged(oldValue, newValue)
  }
  var bounds: Rectangle
    get() = Rectangle(location, size)
    set(value) {
      location = value.location
      size = value.size
    }

  val screenX: Int
    get() = containerScreenX + left
  val screenY: Int
    get() = containerScreenY + top
  val screenLocation
    get() = Point(screenX, screenY)
  val absoluteBounds
    get() = Rectangle(screenLocation, size)

  var right: Int
    get() = containerWidth - left - width
    set(value) = if (anchor.left) {
      size = size.copy(width = containerWidth - left - value)
    } else {
      location = location.copy(x = containerWidth - value - width)
    }
  var bottom: Int
    get() = containerHeight - top - height
    set(value) = if (anchor.top) {
      size = size.copy(height = containerHeight - top - value)
    } else {
      location = location.copy(y = containerHeight - value - height)
    }

  var anchor: AnchorStyles = AnchorStyles.default

  data class LocationChangedEvent(val oldValue: Point, val newValue: Point)

  val locationChanged by event<LocationChangedEvent>()
  fun locationChanged(oldValue: Point, newValue: Point) {
    screenLocationChanged()
    locationChanged(LocationChangedEvent(oldValue, newValue))
  }

  data class SizeChangedEvent(val oldValue: Size, val newValue: Size)

  val sizeChanged by event<SizeChangedEvent>() // use event model to avoid NullPointerException during class init
  fun sizeChanged(oldValue: Size, newValue: Size) {
    fun resize(anchorLeast: Boolean, anchorMost: Boolean, oldContainer: Int, newContainer: Int,
               least: Int, central: Int): Pair<Int, Int> {
      if (anchorLeast && !anchorMost) return least to central // increment to right / bottom
      val increment = newContainer - oldContainer
      if (anchorLeast && anchorMost) return least to (central + increment) // increment to width / height
      if (!anchorLeast && anchorMost) return (least + increment) to central // increment to left / top
      // (!anchorLeast && !anchorMost) => increment to least and most, fix central
      val compensation = (increment % 2) * (oldContainer % 2) // so that location is guaranteed
      return (least + increment / 2 + compensation) to central
    }
    children().forEach { child ->
      val anchor = child.anchor
      val (left, width) = resize(anchor.left, anchor.right, oldValue.width, newValue.width, child.left, child.width)
      val (top, height) = resize(anchor.top, anchor.bottom, oldValue.height, newValue.height, child.top, child.height)
      child.location = Point(left, top)
      child.size = Size(width, height)
    }
    sizeChanged(SizeChangedEvent(oldValue, newValue))
  }

  val screenLocationChanged by event<Unit>()
  fun screenLocationChanged() {
    children().forEach { it.screenLocationChanged() }
    screenLocationChanged(Unit)
  }

  //endregion

  var active = true
  var visible = true
  //  var focused = false
  var text: String = ""
  var zIndex: Int = 0

  fun children(): List<Widget> =
      widgets.asList()

  fun childrenZIndexed(): List<Widget> =
      children().sortedBy { it.zIndex }

  open fun contains(mouseX: Int, mouseY: Int): Boolean =
      absoluteBounds.contains(mouseX, mouseY)

  var overflow = UNSET // TODO render hidden

  //region Events

  var focused = false
    private set(value) {
      if (field != value) {
        field = value
        if (value) gotFocus()
        else lostFocus()
      }
    }

  open fun gotFocus() {}

  open fun lostFocus() {
    focusedWidget = null
  }

  var focusedWidget: Widget? = null
    set(value) {
      if (field != value) {
        val oldValue = field
        field = value
        value?.focused = true
        oldValue?.focused = false
      }
    }
  var isDragging = false

  open fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    childrenZIndexed().forEach { if (it.visible) it.render(mouseX, mouseY, partialTicks) }
  }

  fun isMouseOver(x: Int, y: Int): Boolean =
      (parent?.let { parent ->
        when (parent.overflow) {
          VISIBLE -> parent.visible
          UNSET, HIDDEN -> parent.isMouseOver(x, y)
        }
      } ?: true) && visible && (contains(x, y) || (overflow == VISIBLE && children().any { it.isMouseOver(x, y) }))

  open fun mouseClicked(x: Int, y: Int, button: Int): Boolean =
      childrenZIndexed().asReversed().any {
        it.isMouseOver(x, y) && it.mouseClicked(x, y, button).apply {
          if (this) {
            focusedWidget = it
            if (button == 0) isDragging = true // left click
          }
        }
      }.also { if (!it) focusedWidget = null }

  open fun mouseReleased(x: Int, y: Int, button: Int): Boolean = // TODO better solution
      false.also { childrenZIndexed().asReversed().forEach { it.mouseReleased(x, y, button) } }

  open fun mouseScrolled(x: Int, y: Int, amount: Double): Boolean =
      childrenZIndexed().asReversed().any { it.isMouseOver(x, y) && it.mouseScrolled(x, y, amount) }

  open fun mouseDragged(x: Double, y: Double, button: Int, dx: Double, dy: Double): Boolean = // TODO precise dx dy
      isDragging && focusedWidget?.mouseDragged(x, y, button, dx, dy) ?: false

  open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
      focusedWidget?.keyPressed(keyCode, scanCode, modifiers) ?: false

  open fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
      focusedWidget?.keyReleased(keyCode, scanCode, modifiers) ?: false

  open fun charTyped(charIn: Char, modifiers: Int): Boolean =
      focusedWidget?.charTyped(charIn, modifiers) ?: false

  //endregion

  final override fun equals(other: Any?): Boolean {
    return super.equals(other)
  }

  final override fun hashCode(): Int {
    return super.hashCode()
  }

}