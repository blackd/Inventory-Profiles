package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.gui.Point
import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.gui.widget.Overflow
import io.github.jsnimda.common.util.*
import io.github.jsnimda.common.vanilla.VanillaRender
import io.github.jsnimda.common.vanilla.render.rDepthMask

data class LocationChangedEvent(val oldValue: Point, val newValue: Point)
data class SizeChangedEvent(val oldValue: Size, val newValue: Size)

// break Widget class into several components
open class Widget : IWidget<Widget>, Iterable<Widget> {
  var active = true // not used in this class
  var text = ""     // not used in this class

  val sizeChanged = Event<SizeChangedEvent>()
  val locationChanged = Event<LocationChangedEvent>()
  val screenLocationChanged = Event<Unit>()
  override var anchor = AnchorStyles.default

  override var visible = true
  final override var overflow = Overflow.UNSET
  override var isDragging = false
  override var zIndex = 0

  //region position
  override var location by detectable(Point(0, 0)) { oldValue, newValue ->
    screenLocationChanged()
    locationChanged(LocationChangedEvent(oldValue, newValue))
  }
  override var size by detectable(Size(0, 0)) { oldValue, newValue ->
    resizeChildren(oldValue, newValue)
    sizeChanged(SizeChangedEvent(oldValue, newValue))
  }

  private fun screenLocationChanged() {
    children.forEach { it.screenLocationChanged() }
    screenLocationChanged(Unit)
  }
  //endregion

  //region parent/child
  private val node = Node(this)
  override var parent: Widget?
    get() = node.parent?.value
    set(value) {
      value?.addChild(this) ?: parent?.removeChild(this)
    }
  override val children
    get() = node.children.map { it.value }
  open val allowParent: Boolean // if this node can be a child of other node
    get() = true
  val childCount: Int
    get() = node.children.size

  fun deepContains(child: Widget) = node.deepContains(child.node)
  fun contains(child: Widget) = node.children.contains(child.node)
  fun removeChild(child: Widget) = node.remove(child.node)
  fun addChild(child: Widget) =
    if (child.allowParent) node.add(child.node)
    else Log.error("cannot add widget as child")

  fun clearChildren() {
    while (node.children.isNotEmpty()) node.remove(node.children.first())
  }

  override fun iterator() = children.iterator()
  //endregion

  // event

  override fun contains(mouseX: Int, mouseY: Int): Boolean {
    return absoluteBounds.contains(mouseX, mouseY)
  }

  // focus

  var focused by detectable(false) { _, value -> if (value) gotFocus() else lostFocus() }
  override var focusedWidget: Widget? by detectable(null) { oldValue, newValue ->
    newValue?.focused = true
    oldValue?.focused = false
  }

  open fun gotFocus() {}
  open fun lostFocus() {
    focusedWidget = null
  }

  // todo make inherited properties final

  final override fun equals(other: Any?) = super.equals(other)
  final override fun hashCode() = super.hashCode()
}

private interface IWidget<T : IWidget<T>> :
  IWidgetHierarchical<T>, IWidgetPositioning, IWidgetEventTarget<T>, IWidgetRenderer {
  override var parent: T?
  override val children: List<T>
  override var absoluteBounds: Rectangle
    get() = super.absoluteBounds
    set(value) {
      super.absoluteBounds = value
    }

  val zIndex: Int
  override fun childrenZIndexed() =
    children.sortedBy { it.zIndex }
}

private interface IWidgetHierarchical<T : IWidgetHierarchical<T>> {
  val parent: T?
  val children: List<T>
}

// ============
// widget position
// ============
//region widget position

private interface IWidgetPositioning {
  val parent: IWidgetPositioning?

  /*
    setting location/size should not depend on anchor
    this location relative to parent location
  */

  var location: Point
  var size: Size
  val anchor: AnchorStyles

  /*
    setting left/top/right/bottom depends on anchor (i.e. anchor the opposite side or not)
    (for no depending, use location instead)
    while setting width/height does not
  */

  var left: Int
    get() = location.x
    set(value) =
      if (anchor.right)  // ref: right
        bounds = bounds.copy(x = value, width = left + width - value)
      else
        location = location.copy(x = value)
  var top: Int
    get() = location.y
    set(value) =
      if (anchor.bottom)  // ref: bottom
        bounds = bounds.copy(y = value, height = top + height - value)
      else
        location = location.copy(y = value)
  var width: Int
    get() = size.width
    set(value) {
      size = size.copy(width = value)
    }
  var height: Int
    get() = size.height
    set(value) {
      size = size.copy(height = value)
    }

  var bounds: Rectangle
    get() = Rectangle(location, size)
    set(value) {
      location = value.location
      size = value.size
    }

  // ============
  // parent related
  // ============

  val containerWidth
    get() = parent?.width ?: VanillaRender.screenWidth
  val containerHeight
    get() = parent?.height ?: VanillaRender.screenHeight
  val containerSize
    get() = parent?.size ?: VanillaRender.screenSize
  val containerScreenX
    get() = parent?.screenX ?: 0
  val containerScreenY
    get() = parent?.screenY ?: 0
  val containerScreenLocation
    get() = parent?.screenLocation ?: Point(0, 0)

  /*
    setting right/bottom depends on anchor
  */

  var right: Int
    get() = containerWidth - left - width
    set(value) =
      if (anchor.left)
        size = size.copy(width = containerWidth - left - value)
      else
        location = location.copy(x = containerWidth - value - width)
  var bottom: Int
    get() = containerHeight - top - height
    set(value) =
      if (anchor.top)
        size = size.copy(height = containerHeight - top - value)
      else
        location = location.copy(y = containerHeight - value - height)

  /*
    setting screenX/screenY/screenLocation does not depend on anchor
  */

  var screenX: Int
    get() = screenLocation.x
    set(value) {
      screenLocation = screenLocation.copy(x = value)
    }
  var screenY: Int
    get() = screenLocation.y
    set(value) {
      screenLocation = screenLocation.copy(y = value)
    }
  var screenLocation: Point // todo cache value and update by screenLocationChanged ? (reduce look up to parent)
    get() = containerScreenLocation + location
    set(value) {
      location = value - containerScreenLocation
    }
  var absoluteBounds: Rectangle
    get() = Rectangle(screenLocation, size)
    set(value) {
      screenLocation = value.location
      size = value.size
    }
}

// first + second + third = total, return (dFirst, dSecond)
private fun resize(anchorFirst: Boolean, anchorThird: Boolean, oldTotal: Int, newTotal: Int): Pair<Int, Int> {
  if (anchorFirst && !anchorThird) return 0 to 0 // third is free (changeable)
  val increment = newTotal - oldTotal
  if (anchorFirst && anchorThird) return 0 to increment // second is free
  if (!anchorFirst && anchorThird) return increment to 0 // first is free
  // first and third is free, anchor second
  val compensation = (increment % 2) * (oldTotal % 2) // so that location is guaranteed
  return (increment / 2 + compensation) to 0
}

private fun <T> T.resizeChildren(oldValue: Size, newValue: Size)
    where T : IWidgetPositioning, T : IWidgetHierarchical<T> {
  children.forEach { child ->
    val anchor = child.anchor
    val (dLeft, dWidth) = resize(anchor.left, anchor.right, oldValue.width, newValue.width)
    val (dTop, dHeight) = resize(anchor.top, anchor.bottom, oldValue.height, newValue.height)
    child.location += Point(dLeft, dTop)
    child.size += Size(dWidth, dHeight)
  }
}

//endregion
// ============
// widget input event
// ============

private interface IWidgetEventTarget<T : IWidgetEventTarget<T>> {
  val children: List<T>
  fun childrenZIndexed(): List<T>
  fun contains(mouseX: Int, mouseY: Int): Boolean // absolute bounds contains mouse

  val visible: Boolean
  val overflow: Overflow
  var isDragging: Boolean
  var focusedWidget: T? // setter do gotFocus/lostFocus

  // usually called from parent to check if this should capture mouse event
  fun captures(x: Int, y: Int): Boolean =
    visible && (contains(x, y) || (overflow == Overflow.VISIBLE && children.any { it.captures(x, y) }))

  fun mouseClicked(x: Int, y: Int, button: Int): Boolean =
    childrenZIndexed().asReversed().any {
      it.captures(x, y) && it.mouseClicked(x, y, button)
        .ifTrue { focusedWidget = it; if (button == 0) isDragging = true }
    }.ifFalse { focusedWidget = null }

  fun mouseReleased(x: Int, y: Int, button: Int): Boolean = // TODO find better solution
    false.also {
      if (button == 0) isDragging = false
      childrenZIndexed().asReversed().forEach { it.mouseReleased(x, y, button) }
    }

  fun mouseScrolled(x: Int, y: Int, amount: Double): Boolean =
    childrenZIndexed().asReversed().any { it.captures(x, y) && it.mouseScrolled(x, y, amount) }

  fun mouseDragged(x: Double, y: Double, button: Int, dx: Double, dy: Double): Boolean = // TODO precise dx dy
    isDragging && focusedWidget?.mouseDragged(x, y, button, dx, dy) ?: false

  fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    focusedWidget?.keyPressed(keyCode, scanCode, modifiers) ?: false

  fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    focusedWidget?.keyReleased(keyCode, scanCode, modifiers) ?: false

  fun charTyped(charIn: Char, modifiers: Int): Boolean =
    focusedWidget?.charTyped(charIn, modifiers) ?: false
}

// ============
// widget rendering
// ============

private interface IWidgetRenderer {
  val visible: Boolean
  val overflow: Overflow
  val absoluteBounds: Rectangle
  fun childrenZIndexed(): List<IWidgetRenderer>
  fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    if (overflow == Overflow.HIDDEN) { // notice that mask only apply to children (self render unaffected)
      rDepthMask(absoluteBounds) {
        renderChildren(mouseX, mouseY, partialTicks)
      }
    } else {
      renderChildren(mouseX, mouseY, partialTicks)
    }
  }
  private fun renderChildren(mouseX: Int, mouseY: Int, partialTicks: Float) {
    childrenZIndexed().forEach { if (it.visible) it.render(mouseX, mouseY, partialTicks) }
  }
}

// line 254