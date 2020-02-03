package me.lx.rv.group

/**
 * 这个类是用来记录分组列表中组的结构的。
 * 通过GroupStructure记录每个组是否有头部，是否有尾部和子项的数量。从而能方便的计算
 * 列表的长度和每个组的组头、组尾和子项在列表中的位置。
 */
class GroupStructure {
    var childrenCount: Int = 0
    private var hasHeader: Boolean = false
    private var hasFooter: Boolean = false
     var headerCount = 0
     var footerCount = 0

    constructor(hasHeader: Boolean, hasFooter: Boolean, childrenCount: Int) {
        this.hasHeader = hasHeader
        this.hasFooter = hasFooter
        this.childrenCount = childrenCount
        countHeaderFooterCount()
    }

    private fun countHeaderFooterCount() {
        if (hasHeader) {
            headerCount = 1
        } else {
            headerCount = 0
        }
        if (hasFooter) {
            footerCount = 1
        } else {
            footerCount = 0
        }
    }


    fun hasHeader(): Boolean {
        return hasHeader
    }

    fun hasFooter(): Boolean {
        return hasFooter
    }

    fun setHasHeader(hasHeader: Boolean) {
        this.hasHeader = hasHeader
        countHeaderFooterCount()
    }

    fun setHasFooter(hasFooter: Boolean) {
        this.hasFooter = hasFooter
        countHeaderFooterCount()
    }
}
