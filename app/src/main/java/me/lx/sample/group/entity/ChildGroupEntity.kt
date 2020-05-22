package me.lx.sample.group.entity

import androidx.databinding.ObservableArrayList

/**
 * 组数据的实体类
 */
class TwoLevelGroupEntity {
    var headerText: String? = null
    var footerText: String? = null
    var childGroupList: ObservableArrayList<CgEntity> = ObservableArrayList()

    class CgEntity {
        var cgHeaderText: String? = null
        var cgFooterText: String? = null

        var cgIndexInGroup: Int = 0

        var childChildList: ObservableArrayList<CcEntity> = ObservableArrayList()
        override fun toString(): String {
            return "CgEntity(childText=$cgHeaderText)"
        }

        class CcEntity {
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

    }
}




