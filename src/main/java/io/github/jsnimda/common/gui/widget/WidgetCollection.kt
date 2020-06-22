package io.github.jsnimda.common.gui.widget

class WidgetCollection internal constructor(val owner: Widget) : Iterable<Widget> {

  private val list = mutableListOf<Widget>()

  fun asList(): List<Widget> =
    list

  val size
    get() = list.size

  private fun canAdd(aChild: Widget): Boolean {
    if (aChild is RootWidget) return false
    var p = owner
    while (aChild != p) {
      p = p.parent ?: return true
    }
    return false
  }

  fun add(aChild: Widget) {
    if (aChild !in list && canAdd(aChild)) {
      aChild.parent?.widgets?.remove(aChild)
      assert(aChild.parent == null)
      list.add(aChild)
      setParentReference(aChild)
    }
  }

  fun get(index: Int) =
    list[index]

  fun remove(child: Widget) {
    list.remove(child)
    clearParentReference(child)
  }

  private fun setParentReference(child: Widget) {
    child._parent = owner
  }

  private fun clearParentReference(child: Widget) {
    if (child.parent == owner) {
      child._parent = null
    }
  }

  fun contains(child: Widget): Boolean =
    list.contains(child)

  fun clear() {
    list.forEach { clearParentReference(it) }
    list.clear()
  }

  override fun iterator() = list.iterator()

}