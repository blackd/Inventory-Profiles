package org.anti_ad.mc.ipnext.item

sealed class ItemStack {
    abstract val itemType: ItemType
    abstract val count: Int

    operator fun component1() = itemType
    operator fun component2() = count

    inline val overstacked: Boolean
        get() = count > itemType.maxCount

    final override fun toString() = "${count}x $itemType"

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemStack) return false

        if (isEmpty() && other.isEmpty()) return true
        if (itemType != other.itemType) return false
        if (count != other.count) return false

        return true
    }

    final override fun hashCode(): Int {
        if (isEmpty()) return 0 // temp solution for StackOverflowError
        var result = itemType.hashCode()
        result = 31 * result + count
        return result
    }

//  fun copy(itemType: ItemType = this.itemType, count: Int = this.count): ItemStack { // no use
//    return ItemStack(itemType, count)
//  }

    fun copyAsMutable(): MutableItemStack {
        return MutableItemStack(itemType,
                                count)
    }

    companion object {
        operator fun invoke(itemType: ItemType,
                            count: Int): ItemStack {
            return ImmutableItemStack(itemType,
                                      count)
        }
    }
}

class ImmutableItemStack(override val itemType: ItemType,
                         override val count: Int) : ItemStack()

class MutableItemStack(override var itemType: ItemType,
                       override var count: Int) : ItemStack() {
    companion object
}
