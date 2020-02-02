package me.lx.sample.group.entity

import androidx.databinding.ObservableArrayList

/**
 * 组数据的实体类
 */
class GroupEntity {
    var header: String? = null
    var footer: String? = null
    var childList: ObservableArrayList<ChildEntity> = ObservableArrayList()


    constructor(header: String?, footer: String?, childList: ObservableArrayList<ChildEntity>) {
        this.header = header
        this.footer = footer
        this.childList = childList
    }

    fun setChildren(childList: ObservableArrayList<ChildEntity>) {
        this.childList = childList
    }
}