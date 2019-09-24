package me.lx.rv.group

/**
 * 这个类是用来记录分组列表中组的结构的。
 * 通过GroupStructure记录每个组是否有头部，是否有尾部和子项的数量。从而能方便的计算
 * 列表的长度和每个组的组头、组尾和子项在列表中的位置。
 */
class GroupStructure(
    private var hasHeader: Boolean,
    private var hasFooter: Boolean,
    var childrenCount: Int) {


    fun hasHeader(): Boolean {
        return hasHeader
    }

    fun hasFooter(): Boolean {
        return hasFooter
    }
    fun setHasHeader(hasHeader: Boolean) {
        this.hasHeader = hasHeader
    }

    fun setHasFooter(hasFooter: Boolean) {
        this.hasFooter = hasFooter
    }
}
