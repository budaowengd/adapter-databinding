package me.lx.sample.group.entity

import androidx.databinding.ObservableArrayList

/**
 * 组数据的实体类
 */
class TwoLevelGroupEntity {
    var headerText: String? = null
    var footerText: String? = null
    var childList: ObservableArrayList<TwoLevelChildEntity> = ObservableArrayList()

    ///
    fun hasHeader(): Boolean {
        return false
    }

    fun hasFooter(): Boolean {
        return false
    }
}

class TwoLevelChildEntity() {
    var childText: String? = null
    var childChildList: ObservableArrayList<ChildChildEntity> = ObservableArrayList()
}

class ChildChildEntity() {
    var childChildText: String? = null
}
