package me.lx.sample.group.entity

import androidx.databinding.ObservableArrayList

/**
 * 组数据的实体类
 */
class TwoLevelGroupEntity {
    var headerText: String? = null
    var footerText: String? = null
    var childGroupList: ObservableArrayList<ChildGroupEntity> = ObservableArrayList()

    ///
    fun hasHeader(): Boolean {
        return false
    }

    fun hasFooter(): Boolean {
        return false
    }
}

class ChildGroupEntity {
    var childText: String? = null
    var childChildList: ObservableArrayList<ChildChildEntity> = ObservableArrayList()
    override fun toString(): String {
        return "ChildGroupEntity(childText=$childText)"
    }

}

class ChildChildEntity {
    var childChildText: String? = null

    constructor() {

    }
    constructor(childChildText: String?) {
        this.childChildText = childChildText
    }


    override fun toString(): String {
        return childChildText?:""
    }

}
