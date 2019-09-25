package me.lx.sample.group.adapter

/**
 * 多种不同子项的列表 它跟[VariousAdapter]相比只是去掉了头部和尾部。
 * 去掉头部和尾部的方式跟[NoHeaderAdapter]和[NoFooterAdapter]一样。
 * 这种列表适用于把多个不同的列表合并成一个列表。
 */
class VariousChildAdapter : VariousAdapter() {


    override fun hasHeader(groupPosition: Int): Boolean {
        return false
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return false
    }

}
