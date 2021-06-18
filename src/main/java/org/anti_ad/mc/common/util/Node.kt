package org.anti_ad.mc.common.util

import org.anti_ad.mc.common.Log

// two way associative node
// implement hierarchical structure
class Node<T>(val value: T) {
  private var mParent: Node<T>? = null
  var parent: Node<T>?
    get() = mParent
    set(value) {
      value?.add(this) ?: mParent?.remove(this)
    }

  private val mChildren = mutableSetOf<Node<T>>()
  val children: Set<Node<T>>
    get() = mChildren

  // deepContains: any ancestor (parent of parent) of child == this
  // ref: js Node.contains(), contains(self) returns true
  fun deepContains(child: Node<T>): Boolean {
    var c = child
    while (this != c) {
      c = c.mParent ?: return false
    }
    return true
  }

  fun add(child: Node<T>) {
    if (child.deepContains(this)) { // The new child element contains the parent.
      Log.error("The new child node contains the parent")
    } else { // continue even if child in mChildren (= move to the end)
      child.mParent?.remove(child)
      mChildren.add(child)
      child.mParent = this
    }
  }

  fun remove(child: Node<T>) {
    if (mChildren.remove(child)) {
      child.mParent = null
    }
  }
}